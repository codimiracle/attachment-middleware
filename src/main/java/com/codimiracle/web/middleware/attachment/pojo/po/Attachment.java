package com.codimiracle.web.middleware.attachment.pojo.po;
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @EqualsAndHashCode.Exclude
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