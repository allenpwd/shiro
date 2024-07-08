package pwd.allen.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
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
     * 直接在ini中配置用户认证信息以及角色权限信息
     * IniRealm 在 new 完成后就会使用WildcardPermissionResolver解析配置的权限字符串
     * @see org.apache.shiro.authc.pam.ModularRealmAuthenticator
     * @see org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy
     * @see org.apache.shiro.authz.ModularRealmAuthorizer
     * @see org.apache.shiro.realm.text.IniRealm
     * @see org.apache.shiro.authz.permission.WildcardPermissionResolver
     */
    @Test
    public void ini() throws InterruptedException {
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
        System.out.println(String.format("subject.isAuthenticated()=%s", subject.isAuthenticated()));

        //<editor-fold desc="角色校验">
        System.out.println(subject.hasRole("role1"));
        System.out.println(subject.hasAllRoles(Arrays.asList("role1", "role2")));
        // checkRole 和 checkRoles 在判断为假的情况下会抛出 UnauthorizedException 异常
        subject.checkRole("role1");
        //</editor-fold>

        //<editor-fold desc="资源权限校验">
        // 规则：“资源标识符：操作：对象实例 ID” 即对哪个资源的哪个实例可以进行什么操作。其默认支持通配符权限字符串，“:”表示资源/操作/实例的分割；“,”表示操作的分割；“*”表示任意资源/操作/实例。
        System.out.println(Arrays.toString(subject.isPermitted("pms1:update", "pms2:abc:two", "pms3:add")));
        System.out.println(subject.isPermitted("pms1:up*"));
        System.out.println(subject.isPermitted("pms2:ab*"));
        subject.checkPermission("pms1:test");
        //</editor-fold>

        // 可通过会话 共享内容
        Session session = subject.getSession();
        session.setAttribute("name", "test");
        // 即使在线程中也能获取到共享内容
        new Thread(() -> {
            System.out.println(String.format("在线程中获取会话的共享内容:%s", SecurityUtils.getSubject().getSession().getAttribute("name")));
        }).start();

        Thread.sleep(2000);

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
}
