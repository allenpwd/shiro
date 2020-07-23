package pwd.allen.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;

/**
 * 自定义 Realm域
 * 实现身份认证，只允许帐号user密码user认证通过
 * @author lenovo
 * @create 2020-07-23 8:58
 **/
public class MyRealmOne implements Realm {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        Object principal = authenticationToken.getPrincipal();
        String credentials = new String((char[]) authenticationToken.getCredentials());
        if (!"user".equals(principal)) {
            throw new UnknownAccountException("用户无效");
        }
        if (!"123456".equals(credentials)) {
            throw new IncorrectCredentialsException("密码错误");
        }
        return new SimpleAuthenticationInfo(credentials, credentials, getName());
    }
}
