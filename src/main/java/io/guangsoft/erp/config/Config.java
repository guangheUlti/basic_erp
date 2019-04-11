package io.guangsoft.erp.config;

import io.guangsoft.erp.realm.Realm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class Config {

    @Bean
    public Realm realmManager() {
        // 加密相关
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        // 散列次数
        hashedCredentialsMatcher.setHashIterations(2);
        Realm realm = new Realm();
        realm.setCredentialsMatcher(hashedCredentialsMatcher);
        return realm;
    }

    @Bean
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        //注入自定义cookie(主要是设置寿命, 默认的一年太长)
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        //设置RememberMe的cookie有效期为7天
        simpleCookie.setMaxAge(604800);
        rememberMeManager.setCookie(simpleCookie);
        return rememberMeManager;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realmManager());
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //当用户未登录则跳转到登录路径
        shiroFilterFactoryBean.setLoginUrl("/login");
        //登录成功后要跳转的链接,表单登录方式有效
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //未授权界面,指定没有权限操作时跳转页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/warning");
        //配置不会被过滤的链接顺序判断,过虑器链定义，从上向下顺序执行，一般将/**放在最下边
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        //对静态资源设置匿名访问,anon:所有url都可以匿名访问
        filterChainDefinitionMap.put("/assets/**", "anon");
        //放开登录接口，允许进行登录操作
        filterChainDefinitionMap.put("/shiro/login", "anon");
        //配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/shiro/logout", "logout");
        //authc:所有url都必须认证通过才可以访问
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

}