package com.virtual.cloud.om.sdk.utils;

import com.google.gson.Gson;
import com.virtual.cloud.om.sdk.dto.SysUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenUtil {
    public static final String SECRET = "OadWatcher";
    public static final String TOKEN_HEAD = "Bearer ";

    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setAllowedClockSkewSeconds(1800L)
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));
    }

    public static String generateToken(SysUserDTO sysUserDTO) {
        String subject = new Gson().toJson(sysUserDTO);
        return  Jwts
                .builder()
                .setSubject(subject)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static String refreshToken(String token) {

        String subject = Jwts.parser()
                .setSigningKey(JwtTokenUtil.SECRET)
                .setAllowedClockSkewSeconds(1800L)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        SysUserDTO user = convertTokenToUser(subject);
        return generateToken(user);
    }

    public static SysUserDTO convertTokenToUser(String token) {
        return new Gson().fromJson(token, SysUserDTO.class);
    }

    public static String getUsernameFromToken(String token) {
        String json;
        try {
            final Claims claims = getClaimsFromToken(token);
            json = claims.getSubject();
            SysUserDTO userDTO = convertTokenToUser(json);
            return userDTO.getUsername();
        } catch (Exception e) {
            json = null;
        }
        return json;
    }

}