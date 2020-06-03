package com.codimiracle.web.middleware.attachment;

import com.codimiracle.web.middleware.attachment.controller.AttachmentController;
import com.codimiracle.web.middleware.attachment.service.AttachmentService;
import com.codimiracle.web.mybatis.contract.Paginator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {
    @Bean
    public AttachmentController attachmentController(AttachmentService attachmentService) {
        return new AttachmentController(attachmentService);
    }

    @Bean
    public Paginator paginator() {
        return new Paginator();
    }
}
