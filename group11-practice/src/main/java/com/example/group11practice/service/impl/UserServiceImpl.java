package com.example.group11practice.service.impl;

import com.example.group11practice.entity.RolePermission;
import com.example.group11practice.entity.User;
import com.example.group11practice.entity.UserRole;
import com.example.group11practice.model.PermissionModel;
import com.example.group11practice.model.RoleModel;
import com.example.group11practice.model.UserModel;
import com.example.group11practice.repository.*;
import com.example.group11practice.service.UserService;
import com.example.group11practice.utils.BaseServiceImpl;
import com.example.group11practice.utils.CheckUtil;
import com.example.group11practice.utils.OrikaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<UserModel, User, Long> implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    protected Class<UserModel> getModelType() {
        return UserModel.class;
    }

    @Override
    protected Class<User> getEntityType() {
        return User.class;
    }

    private List<Long> queryRoleIdsByUserId(Long userId) {
        return userRoleRepository.findByUserIdAndDeleted(userId, false).stream().map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public UserModel queryUserByLoginName(String loginName) {
        User user = userRepository.findByLoginNameAndDeleted(loginName, false);
        return mapBean(user, UserModel.class);
    }

    @Override
    public Long queryUserIdByLoginName(String loginName) {
        UserModel userModel = this.queryUserByLoginName(loginName);
        if(CheckUtil.isEmpty(userModel)) {
            return null;
        }
        return userModel.getId();
    }

    @Override
    public List<RoleModel> queryRoleByUserId(Long userId) {
        List<Long> roleIdList = this.queryRoleIdsByUserId(userId);
        if (CheckUtil.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        return OrikaUtil.mapAsList(roleRepository.findAllById(roleIdList), RoleModel.class);
    }

    @Override
    public List<PermissionModel> queryPermissionByUserId(Long userId) {
        List<Long> roleIdList = this.queryRoleIdsByUserId(userId);
        if (CheckUtil.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        List<Long> permissionIdList = rolePermissionRepository.findByRoleIdInAndDeleted(roleIdList, false).stream().map(
                RolePermission::getPermissionId).collect(Collectors.toList());
        return OrikaUtil.mapAsList(permissionRepository.findAllById(permissionIdList), PermissionModel.class);
    }

}
