package com.example.group11practice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtil {

    // 过期时间5分钟
    private static final int EXPIRE_IN_MINUTE = 5;

    private static final String TOKEN_PARAM = "_token";

    public final static String hashAlgorithmName = "SHA-256";

    /**
     * 循环次数
     */
    public final static int hashIterations = 16;

    private static final String SECRET = "group11-secret";


    public static boolean verify(String token, String loginName) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(genSecret(loginName));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("loginName", loginName)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.info("token is invalid{}", e.getMessage());
            return false;
        }
    }


    public static String getToken(HttpServletRequest request) {
        return request == null ? null : (request.getHeader("Authorization") != null ? request.getHeader("Authorization") : request.getParameter(TOKEN_PARAM));
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getLoginName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginName").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static Long getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return Long.valueOf(jwt.getSubject());
        } catch (JWTDecodeException e) {
            log.error("error：{}", e.getMessage());
            return null;
        }
    }


    public static String sign(String loginName, Long userId) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            Algorithm algorithm = Algorithm.HMAC256(genSecret(loginName));
            return JWT.create()
                    .withHeader(map)
                    .withClaim("loginName", loginName)
                    .withSubject(String.valueOf(userId))
                    .withIssuedAt(new Date())
                    .withExpiresAt(DateUtils.addMinutes(new Date(), EXPIRE_IN_MINUTE))
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("error：{}", e);
            return null;
        }
    }

    private static String genSecret(String loginName) {
        return ShiroUtil.sha256(loginName, SECRET);
    }


}
