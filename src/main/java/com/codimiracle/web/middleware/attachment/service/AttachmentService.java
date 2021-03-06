package com.codimiracle.web.middleware.attachment.service;
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
import com.codimiracle.web.middleware.attachment.pojo.po.Attachment;
import com.codimiracle.web.mybatis.contract.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * manage uploaded attachment, and retrieve exists attachment attachment service
 *
 * @author Codimiracle
 */
public interface AttachmentService extends Service<String, Attachment> {
    Attachment upload(MultipartFile multipartFile);

    Attachment upload(String uploaderId, MultipartFile multipartFile);

    Attachment upload(String uploaderId, MultipartFile multipartFile, String identifier);

    String getDefaultUploadId();

    String getUploadDir();

    Attachment findByReferenceCode(String referenceCode);

    List<Attachment> findByIdentifier(String identifier);
}
