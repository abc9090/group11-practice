package com.example.group11practice.shiro;

import com.alibaba.fastjson.JSON;
import com.example.group11practice.utils.ErrorCode;
import com.example.group11practice.utils.JWTUtil;
import com.example.group11practice.utils.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import org.apache.shiro.util.*;

@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = JWTUtil.getToken(httpServletRequest); //得到token
        JWTToken jwtToken = new JWTToken(token); // 解密token
        try {
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
            getSubject(request, response).login(jwtToken);
            // 如果没有抛出异常则代表登入成功，返回true
            return true;
        } catch (AuthenticationException e) {
            log.info("认证失败: token={}, msg={}", token, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws UnauthorizedException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 获取免认证接口 url
        String[] anonUrl = {
                "/swagger/**",
                "/v2/api-docs",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-resources/**",
                "/statics/**",
                "/**/*.js",
                "/**/*.html",
                "/sys/user/login",
                "/sys/logout"
        };
        String[] externalApiUrl = {
                "/exapi/**",
        };

        String reqUri = httpServletRequest.getRequestURI();

        // 静态资源放过
        if(matchUrl(httpServletRequest, Arrays.asList(anonUrl))) {
            return true;
        }

        // 外部接口放过
        if(matchUrl(httpServletRequest, Arrays.asList(externalApiUrl))) {
            return true;
        }

        if (isLoginAttempt(request, response)) {
            return executeLogin(request, response);
        }
        return false;
    }

    private boolean matchUrl(HttpServletRequest httpServletRequest, Iterable<String> anonUrlList) {
        String reqUri = httpServletRequest.getRequestURI();
        for (String u : anonUrlList) {
            if (pathMatcher.match(u, reqUri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


    @Override
    protected boolean sendChallenge(ServletRequest request, ServletResponse response) {
        log.debug("Authentication required: sending 401 Authentication challenge response.");

        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpResponse.getWriter()) {
            String msg = "未认证，请在前端系统进行认证";
            out.print(JSON.toJSON(RestResult.fail(String.valueOf(ErrorCode.SC_UNAUTHORIZED.getCode()), msg)));
        } catch (IOException e) {
            log.error("sendChallenge error：", e);
        }
        return false;
    }
}
