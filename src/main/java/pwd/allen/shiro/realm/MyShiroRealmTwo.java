package pwd.allen.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

/**
 * @author pwd allen
 * @create 2019-03-30 12:56
 **/
public class MyShiroRealmTwo extends AuthorizingRealm {

    /**
     * 只需根据用户名去查密码并返回
     * 校验逻辑位置：org.apache.shiro.realm.AuthenticatingRealm#assertCredentialsMatch
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        System.out.println("[FirstRealm] doGetAuthenticationInfo");

        //1. 把 AuthenticationToken 转换为 UsernamePasswordToken
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        //2. 从 UsernamePasswordToken 中来获取 username
        String username = upToken.getUsername();

        //3. 调用数据库的方法, 从数据库中查询 username 对应的用户记录
        System.out.println("从数据库中获取 username: " + username + " 所对应的用户信息.");

        //4. 若用户不存在, 则可以抛出 UnknownAccountException 异常
        if("unknown".equals(username)){
            throw new UnknownAccountException("用户不存在!");
        }

        //5. 根据用户信息的情况, 决定是否需要抛出其他的 AuthenticationException 异常.
        if("monster".equals(username)){
            throw new LockedAccountException("用户被锁定");
        }

        //6. 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo
        //以下信息是从数据库中获取的.
        //1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象.
        Object principal = username;
        //2). credentials: 密码.
        Object credentials = null;
        if ("admin".equals(username)) {
            credentials = "6f45623f187aeb90bc06089405d1cde1bc7c82236ca6cd90fc99247bad80c434";
        } else if ("sha256".equals(username)) {
            credentials = "feb7196755a856f8031801c1cc8e62ecd0ea3f7371999d8d3c3e9ac69aafa5f3";
        }

        //3). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
        String realmName = getName();
        //4). 盐值.
        ByteSource credentialsSalt = ByteSource.Util.bytes(username);

        SimpleAuthenticationInfo info = null; //new SimpleAuthenticationInfo(principal, credentials, realmName);
        info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
        return info;
    }


    /**
     * 授权会被 shiro 回调的方法
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        //1. 从 PrincipalCollection 中来获取登录用户的信息
        Object principal = principals.getPrimaryPrincipal();

        //2. 利用登录的用户的信息来用户当前用户的角色或权限(可能需要查询数据库)
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        if ("admin".equals(principal)) {
            roles.add("admin");
            permissions.add("*");
        } else if ("sha256".equals(principal)) {
            roles.add("sha256");
            permissions.add("sha256:*");
        }

        //3. 创建 SimpleAuthorizationInfo, 并设置其 roles 属性.
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);
        info.setStringPermissions(permissions);

        //4. 返回 SimpleAuthorizationInfo 对象。
        return info;
    }

    /**
     * 根据帐号密码生成散列值
     * FormAuthenticationFilter默认的散列算法hashIterations为1024
     *
     * @param args
     */
    public static void main(String[] args) {
        String userName = "admin";
        Object credentials = "123456";

        Object salt = ByteSource.Util.bytes(userName);
        int hashIterations = 100;

        Object result = new SimpleHash(Sha256Hash.ALGORITHM_NAME, credentials, salt, hashIterations);
        System.out.println(result);
    }

}
