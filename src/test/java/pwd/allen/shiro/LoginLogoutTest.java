package pwd.allen.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 门那粒沙
 * @create 2020-07-22 21:32
 **/
public class LoginLogoutTest {

    @Test
    public void test() {
        // 获取SecurityManager 工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");

        // 得到 SecurityManager实例，并绑定给SecurityUtils
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

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

        // 用户已经登录
        Assert.assertEquals(true, subject.isAuthenticated());

        // 退出
        subject.logout();
    }
}
