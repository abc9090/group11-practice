package com.example.group11practice.shiro;


import com.example.group11practice.model.UserModel;
import com.example.group11practice.service.UserService;
import com.example.group11practice.utils.CheckUtil;
import com.example.group11practice.utils.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;


    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String loginName = JWTUtil.getLoginName(principals.toString());
        UserModel userModel = userService.queryUserByLoginName(loginName);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<String> roleNameList = Arrays.asList(userModel.getRole());
//        List<String> permissionNameList = userService.queryPermissionByUserId(userModel.getId()).stream().map(
//                PermissionModel::getPerName).collect(Collectors.toList());
        simpleAuthorizationInfo.addRoles(roleNameList);
//        simpleAuthorizationInfo.addStringPermissions(permissionNameList);
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得loginName，用于和数据库进行对比
        String loginName = JWTUtil.getLoginName(token);
        if (CheckUtil.isEmpty(loginName)) {
            throw new AuthenticationException("token invalid");
        }
        UserModel userModel = userService.queryUserByLoginName(loginName);
        if (CheckUtil.isEmpty(userModel)) {
            throw new AuthenticationException("User didn't existed!");
        }

        if (!JWTUtil.verify(token, loginName)) {
            throw new AuthenticationException("Username or password error");
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
