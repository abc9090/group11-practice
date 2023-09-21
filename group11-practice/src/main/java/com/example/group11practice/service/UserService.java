package com.example.group11practice.service;

import com.example.group11practice.model.PermissionModel;
import com.example.group11practice.model.RoleModel;
import com.example.group11practice.model.UserModel;
import com.example.group11practice.utils.BaseService;

import java.util.List;


public interface UserService extends BaseService<UserModel, Long> {

    UserModel queryUserByLoginName(String loginName);

    Long queryUserIdByLoginName(String loginName);

    List<RoleModel> queryRoleByUserId(Long userId);

    List<PermissionModel> queryPermissionByUserId(Long userId);



}
