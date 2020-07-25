package pwd.allen.shiro.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pwd.allen.shiro.realm.MyShiroRealm;

import java.util.List;

/**
 * @author pwd
 * @create 2019-03-31 9:26
 **/
@Configuration
public class MyShiroConfig {

    @Bean
    public SessionsSecurityManager securityManager(List<Realm> realms) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(realms);
        return securityManager;
    }

    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        // 设置验证密码的散列算法规则。不设置的话默认是SimpleCredentialsMatcher，等值匹配
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Md5Hash.ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(1024);
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return myShiroRealm;
    }

    /**
     * 配置拦截url的规则，
     *  url：使用ant风格模式，通配符支持?、*、**；匹配顺序是按照在配置中的声明顺序匹配
     *  拦截器
     *      authc：表示需要身份认证通过后才能访问；
     *      roles[admin]：表示需要有 admin 角色授权才能访问；
     *      perms["user:create"]：表示需要有“user:create”权限才能访问
     *
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/logout", "logout");
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }
}
