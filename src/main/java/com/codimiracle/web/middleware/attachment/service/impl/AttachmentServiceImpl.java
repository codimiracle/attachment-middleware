package com.codimiracle.web.middleware.attachment.service.impl;
/*
 * MIT License
 *
 * Copyright (c) 2020 codimiracle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, Publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.codimiracle.web.middleware.attachment.mapper.AttachmentMapper;
import com.codimiracle.web.middleware.attachment.pojo.po.Attachment;
import com.codimiracle.web.middleware.attachment.service.AttachmentService;
import com.codimiracle.web.mybatis.contract.AbstractService;
import com.codimiracle.web.mybatis.contract.ServiceException;
import com.codimiracle.web.utilities.code.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;


/**
 * using database to manage attachment
 *
 * @author Codimiracle
 */
@Slf4j
@Service
@Transactional
public class AttachmentServiceImpl extends AbstractService<String, Attachment> implements AttachmentService {
    @Resource
    private AttachmentMapper attachmentMapper;
    @Value("${attachment-middleware.anonymous.max-upload-file-size:0}")
    private Long anonymousMaxUploadFileSize;
    @Value("${attachment-middleware.default-uploader-id:0}")
    private String defaultUploaderId;
    @Value("${attachment-middleware.upload-dir:./upload}")
    private String uploadDir;
    @Value("${attachment-middleware.reference-code.prefix:}")
    private String referenceCodePrefix;
    @Value("${attachment-middleware.reference-code.suffix:}")
    private String referenceCodeSuffix;

    /**
     * upload file with anonymous user
     *
     * @param multipartFile submit in form
     * @return {@link Attachment} represents a upload record
     * @see #upload(String, MultipartFile)
     */
    @Override
    public Attachment upload(MultipartFile multipartFile) {
        return upload(defaultUploaderId, multipartFile);
    }

    private String getUploadFileName(String referenceCode, String originalName) {
        return referenceCode + originalName.substring(originalName.indexOf('.'));
    }

    private String getUploadFilePath(String uploadDir, String fileName) {
        String directory = uploadDir;
        if (Objects.isNull(directory)) {
            log.warn("doesn't specify upload dir yet, it will use default folder [./upload].");
            directory = "./upload";
        }
        File dir = Paths.get(directory).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return Paths.get(directory, fileName).toString();
    }


    /**
     * upload a attachment
     *
     * @param uploaderId    who upload this file
     * @param multipartFile submit in form
     * @return {@link Attachment} represents a upload record
     * @see #upload(MultipartFile)
     */
    @Override
    public Attachment upload(String uploaderId, MultipartFile multipartFile) {
        return upload(uploaderId, multipartFile, null);
    }

    /**
     * upload a attachment with identifier
     * in some scenarios, a entity will have more than one attachment,
     * {@code identifyer } can reduce unnecessary table mapping.
     *
     * @param uploaderId    who uploaded this attachment
     * @param multipartFile upload file part.
     * @param identifier    identify which entity having this attachment
     * @return {@link Attachment} represents a upload record
     */
    @Override
    public Attachment upload(String uploaderId, MultipartFile multipartFile, String identifier) {
        if (Objects.isNull(uploaderId)) {
            uploaderId = defaultUploaderId;
        }
        if (multipartFile.isEmpty()) {
            throw new ServiceException("can not upload empty file or request.");
        }
        if (Objects.isNull(uploaderId) || uploaderId.isEmpty()) {
            if (multipartFile.getSize() > anonymousMaxUploadFileSize) {
                throw new ServiceException("the max upload size of anonymous is exceeded");
            }
        }
        Attachment attachment = new Attachment();

        attachment.setReferenceCode(CodeUtils.randomCode(referenceCodePrefix, referenceCodeSuffix, multipartFile.getOriginalFilename() + System.currentTimeMillis(), 16));
        attachment.setFileOriginalName(multipartFile.getOriginalFilename());
        attachment.setMimeType(multipartFile.getContentType());
        attachment.setFileSize(multipartFile.getSize());
        attachment.setUploadedAt(new Date());
        attachment.setStatus(Attachment.STATUS_UPLOADING);
        attachment.setUploaderId(uploaderId);
        attachment.setIdentifier(identifier);

        String originalName = multipartFile.getOriginalFilename();
        Assert.notNull(originalName, "original should not be null.");
        String uploadFileName = getUploadFileName(attachment.getReferenceCode(), originalName);
        attachment.setFileName(uploadFileName);
        String uploadFilePath = getUploadFilePath(uploadDir, uploadFileName);
        attachment.setFilePath(uploadFilePath);
        try {
            Files.copy(multipartFile.getInputStream(), Paths.get(uploadFilePath));
            attachment.setStatus(Attachment.STATUS_UPLOADED);
        } catch (IOException e) {
            log.error("upload file failed:", e);
            attachment.setStatus(Attachment.STATUS_UPLOADED_FAILURE);
        }
        try {
            save(attachment);
        } catch (Throwable throwable) {
            try {
                Files.deleteIfExists(Paths.get(uploadFilePath));
            } catch (IOException e) {
                log.warn("can not delete file in transaction rollback", e);
                // enclose
            }
            //throw again
            throw throwable;
        }
        return attachment;
    }

    @Override
    public String getDefaultUploadId() {
        return this.defaultUploaderId;
    }

    @Override
    public String getUploadDir() {
        return uploadDir;
    }

    @Override
    public Attachment findByReferenceCode(String referenceCode) {
        return findBy("referenceCode", referenceCode);
    }

    /**
     * delete the record and delete the source file.
     *
     * @param id persistent object id
     */
    @Override
    public void deleteById(String id) {
        Attachment attachment = findById(id);
        File file = Paths.get(attachment.getFilePath()).toFile();
        if (file.exists()) {
            if (file.delete()) {
                super.deleteById(id);
            } else {
                throw new ServiceException("rollback for deleting source file failed");
            }
        }
    }

    /**
     * comparing with {@link #deleteById(String)}, logically delete will not delete the source file
     *
     * @param id record id
     */
    @Override
    public void deleteByIdLogically(String id) {
        super.deleteByIdLogically(id);
    }
}
