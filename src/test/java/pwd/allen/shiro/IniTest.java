package pwd.allen.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author 门那粒沙
 * @create 2020-07-22 21:32
 **/
public class IniTest {

    /**
     * 设置全局的SecurityManager，SecurityManager一个应用只需一个
     * @param iniResourcePath
     */
    public void loadIni(String iniResourcePath) {
        // 获取SecurityManager 工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(iniResourcePath);

        // 得到 SecurityManager实例，并绑定给SecurityUtils
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    /**
     * 直接在ini中配置用户认证信息以及角色权限信息
     */
    @Test
    public void ini() {
        loadIni("classpath:ini.ini");

        // 得到Subject
        Subject subject = SecurityUtils.getSubject();
        // 创建用户/密码身份验证Token（用户身份/凭证）
        UsernamePasswordToken token = new UsernamePasswordToken("pwd", "123");

        try {
            // 登录，进行身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }

        // 用户身份是否已认证
        System.out.println(subject.isAuthenticated());

        //<editor-fold desc="用户角色校验">
        System.out.println(subject.hasRole("role1"));
        System.out.println(subject.hasAllRoles(Arrays.asList("role1", "role2")));

        // checkRole 和 checkRoles 在判断为假的情况下会抛出 UnauthorizedException 异常
        subject.checkRole("role1");
        //</editor-fold>

        //<editor-fold desc="资源权限校验">
        System.out.println(Arrays.toString(subject.isPermitted("pms1:update", "pms2:abc:two", "pms3:add")));
        subject.checkPermission("pms1:test");
        //</editor-fold>

        // 退出
        subject.logout();
    }

    /**
     * 使用自定义的realm
     */
    @Test
    public void myRealm() {
        loadIni("classpath:my.ini");

        // 得到Subject
        Subject subject = SecurityUtils.getSubject();
        // 创建用户/密码身份验证Token（用户身份/凭证）
        UsernamePasswordToken token = new UsernamePasswordToken("user", "123456");

        try {
            // 登录，进行身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }

        // 用户已经登录
        Assert.assertEquals(true, subject.isAuthenticated());

        // 退出
        subject.logout();
    }
}
