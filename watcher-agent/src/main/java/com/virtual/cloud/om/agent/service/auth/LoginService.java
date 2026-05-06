package com.virtual.cloud.om.agent.service.auth;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.sdk.entity.mysql.PwdStrategy;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.ModifyUser;
import com.virtual.cloud.om.sdk.dto.SysUserDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.mapper.PwdStrategyMapper;
import com.virtual.cloud.om.sdk.mapper.SysUserMapper;
import com.virtual.cloud.om.sdk.utils.*;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import com.virtual.cloud.om.agent.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/5 10:52
 */

@Service
@Slf4j
public class LoginService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PwdStrategyMapper pwdStrategyMapper;

    @Autowired
    private SysUserService sysUserService;


    //过期时间，单位分钟
    private static int expire = 30;
    private static int idleTimeout = 60 * 60 * 2;

    public String doLogin(SysUserDTO sysUserDTO) throws Exception {
        HttpSession session = WebUtils.session();

        String token = this.checkUser(sysUserDTO);
        sysUserService.updateLastLoginTime(sysUserDTO.getUsername());

        //将token设置到Cookie中
        Cookie cookie = CookieUtil.readCookie(WebUtils.request(), Constant.TOKEN_NAME);
        if (Objects.nonNull(cookie)) {
            CookieUtil.delCookie(WebUtils.request(),WebUtils.response(),Constant.TOKEN_NAME);
        }
        CookieUtil.addCookie(WebUtils.response(), Constant.ACCESS_PATH, Constant.TOKEN_NAME, token, idleTimeout, true);
        session.setMaxInactiveInterval(idleTimeout); // 单位是秒
        return token;
    }

    /**效验用户生成token*/
    private String checkUser(SysUserDTO sysUserDTO) throws Exception {
        if (Objects.isNull(sysUserDTO)) {
            throw new AppException(ErrorCodes.NAME_PWD_IS_NULL);
        }

        //根据用户名获取用户信息
        SysUser sysUser = sysUserService.findUserByUserName(Constant.username);
        if (Objects.isNull(sysUser)) {
            throw new AppException(ErrorCodes.USER_DOES_NOT_EXIT);
        }

        //校验密码 - 前端已改为明文传输，不再解密
        String password = sysUserDTO.getPassword();
        String dbPassword = SM4Utils.webDecryptText(sysUser.getPassword());
        if (!StringUtils.equals(password,dbPassword)){
            throw new AppException(ErrorCodes.PWD_ERR);
        }


        //颁发令牌
        String token = JwtTokenUtil.generateToken(sysUserDTO);
        return token;
    }

    public void modifyUser(ModifyUser modifyUser){
        if (Objects.isNull(modifyUser)){
            throw new AppException(ErrorCodes.MODIFY_USER_ERROR);
        }
        if (!StringUtils.equals(SM4Utils.webDecryptText(modifyUser.getNewPassword()),SM4Utils.webDecryptText(modifyUser.getRenewPassword()))){
            throw new AppException(ErrorCodes.WRONG_PASSWORD);
        }
        this.validatePasswordComplexity(SM4Utils.webDecryptText(modifyUser.getNewPassword()));
        //根据用户名获取用户信息
        SysUser sysUser = sysUserService.findUserByUserName(Constant.username);
        if (!StringUtils.equals(SM4Utils.webDecryptText(modifyUser.getOldPassword()),SM4Utils.webDecryptText(sysUser.getPassword()))){
            throw new AppException(ErrorCodes.WRONG_PASSWORD);
        }
        sysUser.setPassword(modifyUser.getNewPassword());
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(sysUser);
    }

    public void doLogout(){
        CookieUtil.delCookie(WebUtils.request(),WebUtils.response(),Constant.TOKEN_NAME);
    }

    public Boolean verify(String token){
        try {
            java.util.Date expireTime = JwtTokenUtil.getExpirationDateFromToken(token);
            if (expireTime.before(new java.util.Date())) {
                return false;
            }
            /** 20分钟刷新一次 */
            if (expireTime.before(new java.util.Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20)))) {
                String newToken = JwtTokenUtil.refreshToken(token);
                CookieUtil.addCookie(WebUtils.response(), Constant.ACCESS_PATH, Constant.TOKEN_NAME, newToken, idleTimeout, true);
            }
            //校验并解析token
            String username = JwtTokenUtil.getUsernameFromToken(token);
            SysUser user = sysUserService.findUserByUserName(Constant.username);
            if (!user.getUsername().equals(username)){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清空当前session
     * @param session
     */
    public static void deleteSession(HttpSession session) {
        if (session == null) {
            return;
        }
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            // 获取session的属性名称
            String name = attributeNames.nextElement();
            session.removeAttribute(name);
        }
    }

    /**查询当前密码策略配置*/
    public Integer searchPasswordComplexity(){
        List<PwdStrategy> pwdStrategies = pwdStrategyMapper.selectList(null);
        if (CollUtil.isEmpty(pwdStrategies)){
            return Constant.DataCenter.SIMPLE_PASSWORD;
        }
        PwdStrategy pwdStrategy = pwdStrategies.get(0);
        return pwdStrategy.getPwdComplex();
    }


    /**
     * 根据当前密码策略配置 校验密码复杂度
     * 这个方法主要操作员密码校验
     *
     * @param password
     */
    private void validatePasswordComplexity(String password){
        List<PwdStrategy> pwdStrategies = pwdStrategyMapper.selectList(null);
        if (CollUtil.isEmpty(pwdStrategies)){
            return;
        }
        PwdStrategy pwdStrategy = pwdStrategies.get(0);
        validateLength(pwdStrategy, password);
        validateSpecialWord(pwdStrategy, password);
    }

    /**
     * 校验密码长度
     *
     * @param dto      密码策略
     * @param password 密码
     */
    private void validateLength(PwdStrategy dto, String password) {
        if (StringUtils.isBlank(password)) {
            throw new AppException(ErrorCodes.The_password_can_contain,dto.getMinLength());
        }

        Integer minLength = dto.getMinLength();
        if (password.length() < minLength || password.length() > 32) {
            throw new AppException(ErrorCodes.The_password_can_contain,dto.getMinLength());
        }
    }

    /**
     * 根据密码策略 校验密码中是否需要包含特殊字符
     *
     * @param dto
     * @param password
     */
    private void validateSpecialWord(PwdStrategy dto, String password) {

        //字母、数字
        if (dto.getPwdComplex() == 1) {
            // 密码强度
            // 至少包含四类字符(大写字母、小写字母、数字、特殊字符)中的三类字符。
            int strong = 0;
            if (password.matches(".*[a-zA-Z]+.*")) {    // 密码中包含字母
                ++strong;
            }
            if (password.matches(".*[0-9]+.*")) {    // 密码中包含数字
                ++strong;
            }
            if (strong < 2) {
                throw new AppException(ErrorCodes.The_password_does_not_meet_the_complexity_requirements1);
            }
        }

        //特殊字符
        if (dto.getPwdComplex() == 2) {
            // 密码强度
            // 至少包含四类字符(大写字母、小写字母、数字、特殊字符)中的三类字符。
            int strong = 0;
            if (password.matches(".*[^a-zA-Z0-9]+.*")) {    // 密码中包含其他字符
                ++strong;
            }
            if (strong < 1) {
                throw new AppException(ErrorCodes.The_password_does_not_meet_the_complexity_requirements2);
            }
        }

        //字母、数字、特殊字符
        if (dto.getPwdComplex() == 3) {
            // 密码强度
            // 至少包含四类字符(大写字母、小写字母、数字、特殊字符)中的三类字符。
            int strong = 0;
            if (password.matches(".*[a-zA-Z]+.*")) {    // 密码中包含字母
                ++strong;
            }
            if (password.matches(".*[0-9]+.*")) {    // 密码中包含数字
                ++strong;
            }
            if (password.matches(".*[^a-zA-Z0-9]+.*")) {    // 密码中包含其他字符
                ++strong;
            }
            if (strong < 3) {
                throw new AppException(ErrorCodes.The_password_does_not_meet_the_complexity_requirements3);
            }
        }

        //字母数字
        if (dto.getPwdComplex() == 4) {
            // 密码强度
            // 至少包含四类字符(大写字母、小写字母、数字、特殊字符)中的三类字符。
            int strong = 0;

            if (password.matches(".*[a-z]+.*")) {    // 密码中包含小写字母
                ++strong;
            }
            if (password.matches(".*[A-Z]+.*")) {    // 密码中包含大写字母
                ++strong;
            }
            if (password.matches(".*[0-9]+.*")) {    // 密码中包含数字
                ++strong;
            }
            if (password.matches(".*[^a-zA-Z0-9]+.*")) {    // 密码中包含其他字符
                ++strong;
            }
            if (strong < 4) {
                throw new AppException(ErrorCodes.The_password_does_not_meet_the_complexity_requirements4);
            }
        }
    }
}