package com.example.group11practice.service.impl;

import com.example.group11practice.entity.UserRole;
import com.example.group11practice.model.UserRoleModel;
import com.example.group11practice.repository.UserRoleRepository;
import com.example.group11practice.service.UserRoleService;
import com.example.group11practice.utils.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleModel, UserRole, Long> implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    protected Class<UserRoleModel> getModelType() {
        return UserRoleModel.class;
    }

    @Override
    protected Class<UserRole> getEntityType() {
        return UserRole.class;
    }

    @Override
    public List<UserRoleModel> queryUserRoleByUserId(Long userId) {
        List<UserRole> userRoleList = userRoleRepository.findByUserIdAndDeleted(userId, false);
        return mapToModelList(userRoleList);
    }
}
