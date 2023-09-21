package com.example.group11practice.service.impl;

import com.example.group11practice.entity.Role;
import com.example.group11practice.model.RoleModel;
import com.example.group11practice.repository.RoleRepository;
import com.example.group11practice.service.RoleService;
import com.example.group11practice.utils.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<RoleModel, Role, Long> implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected Class<RoleModel> getModelType() {
        return RoleModel.class;
    }

    @Override
    protected Class<Role> getEntityType() {
        return Role.class;
    }
}
