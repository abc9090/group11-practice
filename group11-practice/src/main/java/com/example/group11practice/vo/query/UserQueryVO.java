package com.example.group11practice.vo.query;

import lombok.Data;

@Data
public class UserQueryVO extends QueryVO {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String loginName;

    private String userName;

    private String email;

    private String phone;

}
