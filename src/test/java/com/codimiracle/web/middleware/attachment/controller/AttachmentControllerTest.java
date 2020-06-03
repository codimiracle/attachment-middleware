package com.codimiracle.web.middleware.attachment.controller;

import com.codimiracle.web.basic.contract.ApiResponse;
import com.codimiracle.web.basic.contract.Page;
import com.codimiracle.web.basic.contract.PageSlice;
import com.codimiracle.web.middleware.attachment.pojo.po.Attachment;
import com.codimiracle.web.middleware.attachment.service.AttachmentService;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AttachmentControllerTest {
    @Autowired
    private AttachmentController attachmentController;
    @Autowired
    private AttachmentService attachmentService;
    private String userId;
    private String content;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        this.userId = "10000";
        this.content = "Berry";
        this.multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content.getBytes());
    }


    @AfterEach
    void tearDown() throws IOException {
        this.userId = null;
        this.content = null;
        this.multipartFile = null;
        FileSystemUtils.deleteRecursively(Paths.get(attachmentService.getUploadDir()));
    }

    @Test
    void uploadFile() {
        ApiResponse<Attachment> attachmentApiResponse = attachmentController.uploadFile(userId, multipartFile);
        assertNotNull(attachmentApiResponse.getData());
    }

    @Test
    void downloadAttachment() throws Throwable {
        String userId = "10000";
        String content = "Berry";
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content.getBytes());
        ApiResponse<Attachment> attachment = attachmentController.uploadFile(userId, multipartFile);
        MockMultipartHttpServletRequest httpRequest = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        attachmentController.downloadAttachment(httpRequest, response, attachment.getData().getReferenceCode());
        assertEquals(multipartFile.getContentType(), response.getContentType());
        assertEquals(new String(multipartFile.getBytes()), response.getContentAsString());
    }

    @Test
    void downloadAvatar() throws Throwable {
        String userId = "10000";
        String content = "Berry";
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content.getBytes());
        ApiResponse<Attachment> attachment = attachmentController.uploadFile(userId, multipartFile);
        MockMultipartHttpServletRequest httpRequest = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        attachmentController.downloadAvatar(httpRequest, response, attachment.getData().getReferenceCode());
        assertEquals(multipartFile.getContentType(), response.getContentType());
        assertEquals(new String(multipartFile.getBytes()), response.getContentAsString());
    }

    @Test
    void downloadCover() throws Throwable {
        String userId = "10000";
        String content = "Berry";
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content.getBytes());
        ApiResponse<Attachment> attachment = attachmentController.uploadFile(userId, multipartFile);
        MockMultipartHttpServletRequest httpRequest = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        attachmentController.downloadCover(httpRequest, response, attachment.getData().getReferenceCode(), "");
        assertEquals(multipartFile.getContentType(), response.getContentType());
        assertEquals(new String(multipartFile.getBytes()), response.getContentAsString());
    }

    @Test
    void delete() {
        ApiResponse<Attachment> attachmentApiResponse = attachmentController.uploadFile(this.userId, this.multipartFile);
        attachmentController.delete(attachmentApiResponse.getData().getId());
        Attachment result = attachmentService.findById(attachmentApiResponse.getData().getId());
        assertNull(result);
        assertFalse(Files.exists(Paths.get(attachmentApiResponse.getData().getFilePath())));
    }

    @Test
    void entity() {
        ApiResponse<Attachment> attachment = attachmentController.uploadFile(this.userId, this.multipartFile);
        ApiResponse<Attachment> result = attachmentController.entity(attachment.getData().getId());
        assertEquals(attachment.getData(), result.getData());
    }

    @Test
    void collection() {
        ApiResponse<PageSlice<Attachment>> apiResponse = attachmentController.collection(new Page());
        assertNotNull(apiResponse.getData());
        assertFalse(apiResponse.getData().getList().isEmpty());
    }
}