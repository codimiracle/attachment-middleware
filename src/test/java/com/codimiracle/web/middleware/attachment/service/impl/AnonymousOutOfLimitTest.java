package com.codimiracle.web.middleware.attachment.service.impl;

import com.codimiracle.web.middleware.attachment.service.AttachmentService;
import com.codimiracle.web.mybatis.contract.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {"attachment-middleware.anonymous.max-upload-file-size=0", "attachment-middleware.default-uploader-id="})
public class AnonymousOutOfLimitTest {
    @Autowired
    private AttachmentService attachmentService;

    @Test
    public void outOfLimit() {
        String content = "Hello world";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content.getBytes());
        assertTrue(multipartFile.getSize() > 0);
        assertThrows(ServiceException.class, () -> {
            attachmentService.upload(multipartFile);
        });
    }
}
