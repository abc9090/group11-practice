package com.example.group11practice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RoleModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String roleName;

    private String roleDesc;

    private Boolean deleted;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    private String remark;

}
