package io.guangsoft.erp.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.ArrayList;
import java.util.List;

public class Realm extends AuthorizingRealm {

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        String password = "4d521acb9b8b3b4fa082ab16b3bd363a";
        String salt = "guanghe";
        return new SimpleAuthenticationInfo(userName, password, ByteSource.Util.bytes(salt), this.getName());
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("使用了自定义的realm,用户授权...");
        // 角色
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        roles.add("user");
        // 权限
        List<String> permissions = new ArrayList<>();
        permissions.add("admin:select");
        permissions.add("admin:update");
        permissions.add("admin:delete");
        permissions.add("user:select");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermissions(permissions);
        simpleAuthorizationInfo.addRoles(roles);
        return simpleAuthorizationInfo;
    }

}