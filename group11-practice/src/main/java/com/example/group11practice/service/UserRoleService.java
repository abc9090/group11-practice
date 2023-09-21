package com.example.group11practice.service;

import com.example.group11practice.model.UserRoleModel;
import com.example.group11practice.utils.BaseService;

import java.util.List;

public interface UserRoleService extends BaseService<UserRoleModel, Long> {

    List<UserRoleModel> queryUserRoleByUserId(Long userId);

}
