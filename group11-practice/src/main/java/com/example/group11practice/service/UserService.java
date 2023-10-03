package com.example.group11practice.service;

import com.example.group11practice.model.UserModel;
import com.example.group11practice.utils.BaseService;

import java.util.List;


public interface UserService extends BaseService<UserModel, Long> {

    UserModel queryUserByLoginName(String loginName);

    Long queryUserIdByLoginName(String loginName);

    List<Long> queryFollowingUserIdListByBeFollowedUserId(Long beFollowedUserId);

    List<Long> queryBeFollowedUserIdListByFollowingUserId(Long followingUserId);

    List<UserModel> queryFollowingUserModelByBeFollowedUserId(Long beFollowedUserId);
}
