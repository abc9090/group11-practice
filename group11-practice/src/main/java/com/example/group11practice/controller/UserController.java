package com.example.group11practice.controller;

import com.example.group11practice.entity.User;
import com.example.group11practice.enums.PageEnum;
import com.example.group11practice.model.UserModel;
import com.example.group11practice.service.UserService;
import com.example.group11practice.utils.*;
import com.example.group11practice.vo.query.UserQueryVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("sys/user/login")
    @ApiOperation(notes = "用户登录获取token", value = "用户登录获取token", tags = "用户管理")
    public RestResult login(@RequestParam("loginName") String loginName,
                            @RequestParam("password") String password) {
        UserModel userModel = userService.queryUserByLoginName(loginName);
        if (userModel.getPassword().equals(ShiroUtil.sha256(password, userModel.getSalt()))) {
            return RestResult.ok(JWTUtil.sign(userModel.getLoginName(), userModel.getId()));
        } else {
            throw Group11Exception.unauthorized("未认证");
        }
    }

    @GetMapping("/sys/user/loginname/{loginName}")
    @ApiOperation(notes = "根据用户名查询用户信息", value = "根据用户名查询用户信息", tags = "用户管理")
    public RestResult<UserModel> queryUserByLoginName(@PathVariable String loginName) {
        UserModel result = userService.queryUserByLoginName(loginName);
        return RestResult.ok(result);
    }

    @GetMapping("/sys/user/id/{id}")
    @ApiOperation(notes = "根据用户id查询用户信息", value = "根据用户id查询用户信息", tags = "用户管理")
    public RestResult<UserModel> queryById(@PathVariable Long id) {
        UserModel result = userService.findById(id);
        return RestResult.ok(result);
    }

    @PostMapping("exapi/sys/user/id")
    @ApiOperation(notes = "新增用户信息", value = "新增用户信息", tags = "用户管理")
    public RestResult<UserModel> updateUserById(@RequestBody UserModel form, HttpServletRequest httpServletRequest) {
        String token = JWTUtil.getToken(httpServletRequest);
        Long userId = JWTUtil.getUserId(token);
        log.info("[insertUser],form={},userId={}", form, userId);
        form.setUpdateBy(userId.toString());

        String salt = RandomStringUtils.randomAlphanumeric(20);
        form.setSalt(salt);
        form.setPassword(ShiroUtil.sha256(form.getPassword(),salt));
        Long id = userService.insertOne(form);
        return RestResult.ok(userService.findById(id));
    }
    @PutMapping("/sys/user/id/{id}")
    @ApiOperation(notes = "修改用户信息", value = "修改用户信息", tags = "用户管理")
    public RestResult<UserModel> updateUserById(@PathVariable Long id, @RequestBody UserModel form,
                                                HttpServletRequest httpServletRequest) {
        String token = JWTUtil.getToken(httpServletRequest);
        Long userId = JWTUtil.getUserId(token);
        log.info("[insertUser],form={},userId={}", form, userId);
        form.setId(id);
        form.setUpdateBy(userId.toString());
        //用id更新用户数据
        userService.updateById(form);

        return RestResult.ok(userService.findById(id));
    }

    @GetMapping("/sys/user")
    @ApiOperation(notes = "根据多条件分页查询用户列表", value = "根据多条件分页查询用户列表", tags = "用户管理")
    public RestResult<Page<UserModel>> queryUserList(UserQueryVO params) {
        log.info("[queryUserList] params={}", params);
        Integer pageNo = CheckUtil.isNotEmpty(params.getPageNo()) ? params.getPageNo() : PageEnum.DEFAULT_PAGE_NO.getKey();
        Integer pageSize = CheckUtil.isNotEmpty(params.getPageSize()) ? params.getPageSize() : PageEnum.DEFAULT_PAGE_SIZE.getKey();

        Specification<User> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CheckUtil.isNotEmpty(params.getId())) {
                predicates.add(builder.equal(root.get("id"), params.getId()));
            }
            if (CheckUtil.isNotEmpty(params.getLoginName())) {
                predicates.add(builder.like(root.get("loginName"), "%" + params.getLoginName() + "%"));
            }
            if (CheckUtil.isNotEmpty(params.getUserName())) {
                predicates.add(builder.like(root.get("loginName"), "%" + params.getUserName() + "%"));
            }
            if (CheckUtil.isNotEmpty(params.getEmail())) {
                predicates.add(builder.like(root.get("email"), "%" + params.getEmail() + "%"));
            }
            if (CheckUtil.isNotEmpty(params.getPhone())) {
                predicates.add(builder.like(root.get("phone"), "%" + params.getPhone() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return RestResult.ok(userService.findAll(spec, pageable));

    }

}
