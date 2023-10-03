package com.example.group11practice.controller;

import com.example.group11practice.enums.PageEnum;
import com.example.group11practice.model.UserModel;
import com.example.group11practice.service.FollowService;
import com.example.group11practice.service.UserService;
import com.example.group11practice.utils.CheckUtil;
import com.example.group11practice.utils.JWTUtil;
import com.example.group11practice.utils.OrikaUtil;
import com.example.group11practice.utils.RestResult;
import com.example.group11practice.vo.UserVO;
import com.example.group11practice.vo.query.UserQueryVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
public class FollowController {

    @Autowired
    private UserService userService;

    @GetMapping("me/fan-list")
    @ApiOperation(notes = "查询当前登录用户的粉丝列表", value = "查询查看当前登录用户的粉丝列表", tags = "关注管理")
    public RestResult<List<UserVO>> queryMyFans(UserQueryVO params, HttpServletRequest httpServletRequest) {
        String token = JWTUtil.getToken(httpServletRequest);
        Long userId = JWTUtil.getUserId(token);
        log.info("[queryMyFanList],userId={}", userId);

        List<UserModel> userModelList = userService.queryFollowingUserModelByBeFollowedUserId(userId);
        return RestResult.ok(OrikaUtil.mapAsList(userModelList, UserVO.class));
    }
}
