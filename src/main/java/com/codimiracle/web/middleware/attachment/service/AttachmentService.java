package com.codimiracle.web.middleware.attachment.service;

import com.codimiracle.web.middleware.attachment.pojo.po.Attachment;
import com.codimiracle.web.mybatis.contract.Service;
import org.springframework.web.multipart.MultipartFile;


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
}
