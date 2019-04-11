package io.guangsoft.erp.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shiro")
public class ShiroController {

    @RequestMapping(value = "login")
    public String login(String username, String password, Boolean rememberMe) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        usernamePasswordToken.setRememberMe(rememberMe);
        String msg = null;
        try {
            subject.login(usernamePasswordToken);
            msg = "身份认证成功！";
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            msg = "账号不存在！";
        } catch (LockedAccountException e) {
            e.printStackTrace();
            msg = "账号被锁定！";
        } catch (DisabledAccountException e) {
            e.printStackTrace();
            msg = "账号被禁用！";
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            msg = "凭证/密码错误！";
        } catch (ExpiredCredentialsException e) {
            e.printStackTrace();
            msg = "凭证/密码过期！";
        } catch (ExcessiveAttemptsException e) {
            e.printStackTrace();
            msg = "登录失败次数过多！";
        }
        JSONObject result = new JSONObject();
        if(subject.isAuthenticated()) {
            result.put("code", 0);
        } else {
            result.put("code", -1);
        }
        result.put("msg", msg);
        return result.toString();
    }
}