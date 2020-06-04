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
import java.util.List;

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

    @Test
    public void findByIdentifier() {
        String identifier = "some-attachment";
        for (int i = 0; i < 10; i++) {
            attachmentService.upload(uploaderId + i, multipartFile, identifier);
        }
        List<Attachment> attachments = attachmentService.findByIdentifier(identifier);
        assertNotNull(attachments);
        assertEquals(10, attachments.size());
        Attachment attachment = attachments.get(attachments.size() - 1);
        assertEquals(uploaderId + 9, attachment.getUploaderId());
    }

    @AfterEach
    public void tearDown() throws IOException {
        this.content = null;
        this.multipartFile = null;
        this.uploaderId = null;
        FileSystemUtils.deleteRecursively(Paths.get(attachmentService.getUploadDir()));
    }
}