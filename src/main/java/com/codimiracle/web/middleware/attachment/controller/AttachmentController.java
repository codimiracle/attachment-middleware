package com.codimiracle.web.middleware.attachment.controller;
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

import com.codimiracle.web.basic.contract.ApiResponse;
import com.codimiracle.web.basic.contract.Page;
import com.codimiracle.web.basic.contract.PageSlice;
import com.codimiracle.web.basic.contract.util.ApiResponses;
import com.codimiracle.web.middleware.attachment.http.MultipartFileSender;
import com.codimiracle.web.middleware.attachment.pojo.po.Attachment;
import com.codimiracle.web.middleware.attachment.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * a reference of attachment service using example.
 *
 * @author Codimiracle
 */
@Slf4j
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    public ApiResponse<Attachment> uploadFile(String userId, MultipartFile uploadfile) {
        return ApiResponses.data(attachmentService.upload(userId, uploadfile));
    }

    private void fileToResponse(String filepath, HttpServletResponse response) throws IOException {
        Path file = new File(filepath).toPath();
        response.setContentType(Files.probeContentType(file));
        Files.copy(file, response.getOutputStream());
    }

    private void attachmentToResponse(Attachment attatchment, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Path path = Paths.get(attatchment.getFilePath());
        MultipartFileSender.fromPath(path)
                .with(request)
                .with(response)
                .serveResource();
    }

    private void avatarNotFound(HttpServletResponse response) throws IOException {
        fileToResponse("./assets/avatar-not-found.png", response);
    }

    private void coverNotFound(HttpServletResponse response, String identifier) throws IOException {
        if (Objects.isNull(identifier)) {
            fileToResponse("./assets/empty.png", response);
        }
        fileToResponse("./assets/empty" + identifier + ".png", response);
    }

    private void downloadAttachmentOrNotFound(HttpServletRequest request, HttpServletResponse response, String referenceCode, Supplier<? extends Throwable> notFound) throws Throwable {
        Attachment attachment = attachmentService.findByReferenceCode(referenceCode);
        if (Objects.nonNull(attachment) && Objects.equals(attachment.getStatus(), Attachment.STATUS_UPLOADED)) {
            Path file = Paths.get(attachment.getFilePath());
            if (Files.exists(file)) {
                attachmentToResponse(attachment, request, response);
                return;
            }
        }
        Throwable throwable = notFound.get();
        if (throwable != null) {
            throw throwable;
        }
    }

    public void downloadAttachment(HttpServletRequest request, HttpServletResponse response, String referenceCode) throws Throwable {
        downloadAttachmentOrNotFound(request, response, referenceCode, () -> {
            try {
                response.sendError(404, "Resource not found.");
            } catch (IOException e) {
                return e;
            }
            return null;
        });
    }

    public void downloadAvatar(HttpServletRequest request, HttpServletResponse response, String referenceCode) throws Throwable {
        downloadAttachmentOrNotFound(request, response, referenceCode, () -> {
            try {
                avatarNotFound(response);
            } catch (IOException e) {
                return e;
            }
            return null;
        });
    }

    public void downloadCover(HttpServletRequest request, HttpServletResponse response, String referenceCode, String identifier) throws Throwable {
        downloadAttachmentOrNotFound(request, response, referenceCode, () -> {
            try {
                coverNotFound(response, identifier);
            } catch (IOException e) {
                return e;
            }
            return null;
        });
    }

    public ApiResponse<Void> delete(String id) {
        attachmentService.deleteById(id);
        return ApiResponses.message(200, "删除成功！");
    }

    public ApiResponse<Attachment> entity(String id) {
        Attachment attachment = attachmentService.findById(id);
        return ApiResponses.data(attachment);
    }

    public ApiResponse<PageSlice<Attachment>> collection(Page page) {
        PageSlice<Attachment> slice = attachmentService.findAll(page);
        return ApiResponses.data(slice);
    }
}
