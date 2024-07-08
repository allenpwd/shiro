package pwd.allen.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author pwd allen
 * @create 2019-03-30 12:56
 **/
public class MyShiroRealm extends AuthorizingRealm {

    /**
     * 这个方法只需根据用户名去查密码并返回，校验逻辑在{@link org.apache.shiro.realm.AuthorizingRealm#getAuthenticationInfo}中封装好了
     * 校验认证逻辑被抽出来放在这个位置：{@link org.apache.shiro.realm.AuthenticatingRealm#assertCredentialsMatch}
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
        // 这里强转的类型不一定要是UsernamePasswordToken ，具体要看你在登录接口中所传的对象类型
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
            credentials = "038bdaf98f2037b31f1e75b5b4c9b26e";
        } else if ("md5".equals(username)) {
            credentials = "2924fdef0ccba9b053885e025f5ab2d6";
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
            roles.add("*");
            permissions.add("*");
        } else if ("md5".equals(principal)) {
            roles.add("md5");
            permissions.add("md5:*");
        }

        //3. 创建 SimpleAuthorizationInfo, 并设置其 roles 属性.
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);
        info.setStringPermissions(permissions);

        //4. 返回 SimpleAuthorizationInfo 对象。
        return info;
    }

    /**
     * 重写role的校验规则，使之支持通配符*结尾
     * @param roleIdentifier
     * @param info
     * @return
     */
    @Override
    protected boolean hasRole(String roleIdentifier, AuthorizationInfo info) {
        if (info == null || info.getRoles() == null) {
            return false;
        }
        Iterator<String> iterator = info.getRoles().iterator();
        while (iterator.hasNext()) {
            String role = iterator.next();
            if (role.endsWith("*")) {
                if (role.length() == 1) {
                    return true;
                }
                if (roleIdentifier.startsWith(role.substring(0, role.length() - 1))) {
                    return true;
                }
            }
            if (role.equals(roleIdentifier)) {
                return true;
            }
        }
        return false;
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
        int hashIterations = 1024;

        Object result = new SimpleHash(Md5Hash.ALGORITHM_NAME, credentials, salt, hashIterations);
        System.out.println(result);
    }

}
