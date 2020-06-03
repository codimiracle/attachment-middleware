package com.codimiracle.web.middleware.attachment.pojo.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "attachment")
public class Attachment {
    public static final int STATUS_UPLOADED_FAILURE = -1;
    public static final int STATUS_UPLOADING = 0;
    public static final int STATUS_UPLOADED = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String Id;

    @Column(name = "reference_code")
    private String referenceCode;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_original_name")
    private String fileOriginalName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "uploaded_at")
    private Date uploadedAt;

    /**
     * 上传状态：1：上传成功 -1：上传失败， 0：正在处理
     */
    private Integer status;

    /**
     * 附件标识，用于区别模块，特定一对多关系
     */
    private String identifier;

    @JsonIgnore
    @Column(name = "file_path")
    private String filePath;

    @Column(name = "uploader_id")
    private String uploaderId;
}