package com.example.group11practice.entity;

import com.example.group11practice.utils.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@Table(name = "USER")
public class User implements BaseEntity<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginName;

    private String userName;

    private String password;

    private String salt;

    private String email;

    private String phone;

    private String role;

    private BigDecimal balance;

    private Boolean deleted;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    private String remark;

}
