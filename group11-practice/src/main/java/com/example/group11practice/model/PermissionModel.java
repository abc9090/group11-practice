package com.example.group11practice.model;

import lombok.Data;

import java.util.Date;

@Data
public class PermissionModel {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String perName;

    private String perDesc;

    private String perUrl;

    private Boolean deleted;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    private String remark;

}
