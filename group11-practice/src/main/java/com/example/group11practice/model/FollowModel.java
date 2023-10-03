package com.example.group11practice.model;

import lombok.Data;

import java.util.Date;

@Data
public class FollowModel {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long beFollowedUserId;

    private Long FollowingUserId;

    private Boolean deleted;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    private String remark;

}
