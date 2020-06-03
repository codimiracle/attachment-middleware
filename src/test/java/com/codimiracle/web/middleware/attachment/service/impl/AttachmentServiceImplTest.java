package com.codimiracle.web.middleware.attachment.service.impl;

import com.codimiracle.web.middleware.attachment.pojo.po.Attachment;
import com.codimiracle.web.middleware.attachment.service.AttachmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureMockMvc
@SpringBootTest
public class AttachmentServiceImplTest {

    @Autowired
    private AttachmentService attachmentService;
    private String content;
    private MockMultipartFile multipartFile;
    private String uploaderId;

    @BeforeEach
    public void setUp() {
        this.uploaderId = "12100";
        this.content = "Hello world";
        this.multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content.getBytes());
    }

    @Test
    public void uploadDirNonExists() throws Exception {
        String uploadDir = attachmentService.getUploadDir();
        if (Files.exists(Paths.get(uploadDir))) {
            if (!FileSystemUtils.deleteRecursively(Paths.get(uploadDir))) {
                fail("can not delete directory, is it using by other application ?");
                return;
            }
        }
        uploadByAnonymous();
    }

    @Test
    public void uploadByUploader() throws Exception {
        Attachment attachment = attachmentService.upload(uploaderId, multipartFile);
        assertTrue(Files.exists(Paths.get(attachment.getFilePath())));
        assertNotNull(attachment);
        assertEquals(multipartFile.getContentType(), attachment.getMimeType());
        assertEquals(multipartFile.getOriginalFilename(), attachment.getFileOriginalName());
        assertEquals(multipartFile.getSize(), attachment.getFileSize());
        assertEquals(attachment.getUploaderId(), uploaderId);
        assertEquals(content, new String(Files.readAllBytes(Paths.get(attachment.getFilePath()))));
    }

    @Test
    public void uploadByAnonymous() throws Exception {
        Attachment attachment = attachmentService.upload(multipartFile);
        assertTrue(Files.exists(Paths.get(attachment.getFilePath())));
        assertNotNull(attachment);
        assertEquals(multipartFile.getContentType(), attachment.getMimeType());
        assertEquals(multipartFile.getOriginalFilename(), attachment.getFileOriginalName());
        assertEquals(multipartFile.getSize(), attachment.getFileSize());
        assertEquals(attachmentService.getDefaultUploadId(), attachment.getUploaderId());
        assertEquals(content, new String(Files.readAllBytes(Paths.get(attachment.getFilePath()))));
    }


    @Test
    public void delete() {
        Attachment attachment = attachmentService.upload(multipartFile);
        assertTrue(Files.exists(Paths.get(attachment.getFilePath())));
        attachmentService.deleteById(attachment.getId());
        assertFalse(Files.exists(Paths.get(attachment.getFilePath())));
    }

    public void deleteLogically() {
        Attachment attachment = attachmentService.upload(multipartFile);
        assertTrue(Files.exists(Paths.get(attachment.getFilePath())));
        attachmentService.deleteByIdLogically(attachment.getId());
        assertTrue(Files.exists(Paths.get(attachment.getFilePath())));
    }

    @AfterEach
    public void tearDown() throws IOException {
        this.content = null;
        this.multipartFile = null;
        this.uploaderId = null;
        FileSystemUtils.deleteRecursively(Paths.get(attachmentService.getUploadDir()));
    }
}