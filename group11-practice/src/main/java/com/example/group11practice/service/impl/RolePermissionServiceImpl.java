package com.example.group11practice.service.impl;

import com.example.group11practice.entity.RolePermission;
import com.example.group11practice.model.RolePermissionModel;
import com.example.group11practice.repository.RolePermissionRepository;
import com.example.group11practice.service.RolePermissionService;
import com.example.group11practice.utils.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermissionModel, RolePermission, Long> implements RolePermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    protected Class<RolePermissionModel> getModelType() {
        return RolePermissionModel.class;
    }

    @Override
    protected Class<RolePermission> getEntityType() {
        return RolePermission.class;
    }

    @Override
    public List<RolePermissionModel> queryRolePermissionByRoleId(List<Long> roleIdList) {
        List<RolePermission> rolePermissionList = rolePermissionRepository.findByRoleIdInAndDeleted(roleIdList, false);
        return mapToModelList(rolePermissionList);
    }
}
