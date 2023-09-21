package com.example.group11practice.service;

import com.example.group11practice.model.RolePermissionModel;
import com.example.group11practice.utils.BaseService;

import java.util.List;

public interface RolePermissionService extends BaseService<RolePermissionModel, Long> {

    List<RolePermissionModel> queryRolePermissionByRoleId(List<Long> roleIdList);


}
