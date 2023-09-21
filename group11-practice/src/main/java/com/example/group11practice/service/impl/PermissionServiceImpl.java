package com.example.group11practice.service.impl;

import com.example.group11practice.entity.Permission;
import com.example.group11practice.model.PermissionModel;
import com.example.group11practice.repository.PermissionRepository;
import com.example.group11practice.service.PermissionService;
import com.example.group11practice.utils.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PermissionServiceImpl extends BaseServiceImpl<PermissionModel, Permission, Long> implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    protected Class<PermissionModel> getModelType() {
        return PermissionModel.class;
    }

    @Override
    protected Class<Permission> getEntityType() {
        return Permission.class;
    }
}
