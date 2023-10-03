package com.example.group11practice.service.impl;

import com.example.group11practice.entity.Follow;
import com.example.group11practice.entity.User;
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
    private FollowRepository followRepository;

    @Override
    protected Class<UserModel> getModelType() {
        return UserModel.class;
    }

    @Override
    protected Class<User> getEntityType() {
        return User.class;
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
    public List<Long> queryFollowingUserIdListByBeFollowedUserId(Long beFollowedUserId) {
        return followRepository.findByBeFollowedUserIdAndDeleted(beFollowedUserId, false).stream().map(
                Follow::getFollowingUserId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryBeFollowedUserIdListByFollowingUserId(Long FollowingUserId) {
        return followRepository.findByFollowingUserIdAndDeleted(FollowingUserId, false).stream().map(
                Follow::getFollowingUserId).collect(Collectors.toList());
    }

    @Override
    public List<UserModel> queryFollowingUserModelByBeFollowedUserId(Long beFollowedUserId) {
        List<Long> followingUserIdList = this.queryFollowingUserIdListByBeFollowedUserId(beFollowedUserId);
        if(CheckUtil.isEmpty(followingUserIdList)) {
            return new ArrayList<>();
        }
        return OrikaUtil.mapAsList(userRepository.findAllById(followingUserIdList), UserModel.class);
    }


}
