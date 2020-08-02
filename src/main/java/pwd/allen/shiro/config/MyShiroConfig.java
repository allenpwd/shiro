package pwd.allen.shiro.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AllSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pwd.allen.shiro.realm.MyShiroRealm;
import pwd.allen.shiro.realm.MyShiroRealmTwo;

import java.util.List;

/**
 *
 * 多个realm时
 * 身份认证：根据认证策略
 * 权限认证：只要有一个通过，就认证通过
 *
 * @author pwd
 * @create 2019-03-31 9:26
 **/
@Configuration
public class MyShiroConfig {

    /**
     * 去掉会报错：Parameter 0 of method authorizationAttributeSourceAdvisor in ShiroAnnotationProcessorAutoConfiguration required a bean named 'authorizer' that could not be found
     * 个人分析：MyShiroRealm也是Authorizer子类，导致ShiroWebAutoConfiguration的默认authorizer无法生效
     * 最好还是自己定义SecurityManager
     *
     * 认证策略：参考AuthenticationStrategy实现类
     * @see org.apache.shiro.authc.pam.AuthenticationStrategy
     * @see ModularRealmAuthenticator 默认用的是AtLeastOneSuccessfulStrategy
     *
     * @param realms
     * @return
     */
    @Bean
    public SessionsSecurityManager securityManager(List<Realm> realms) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(cacheManager());
        securityManager.setRealms(realms);

        // 设置认证策略为所有都匹配才算通过
//        ModularRealmAuthenticator.class.cast(securityManager.getAuthenticator()).setAuthenticationStrategy(new AllSuccessfulStrategy());

        // 设置 记住我 cookie超时时间为1小时
        CookieRememberMeManager.class.cast(securityManager.getRememberMeManager()).getCookie().setMaxAge(3600);

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

    @Bean
    public MyShiroRealmTwo myShiroRealmTwo() {
        MyShiroRealmTwo myShiroRealm = new MyShiroRealmTwo();
        // 设置验证密码的散列算法规则。不设置的话默认是SimpleCredentialsMatcher，等值匹配
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(100);
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return myShiroRealm;
    }

    /**
     * 配置拦截url的规则，
     *  url：使用ant风格模式，通配符支持?、*、**；匹配顺序是按照在配置中的声明顺序匹配
     *  拦截器：可以参考org.apache.shiro.web.filter.mgt.DefaultFilter的枚举拦截器
     *      authc：表示需要身份认证通过后才能访问；
     *      roles[admin]：表示需要有 admin 角色授权才能访问；
     *      perms["user:create"]：表示需要有“user:create”权限才能访问
     *
     * 疑问：好像每个请求都得匹配这个拦截链才行，否则好像拿到的Subject是未认证的，这样基于注解的方式会校验有问题
     *
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/logout", "logout");
        // login需要设置成authc，这样才能直接使用FormAuthenticationFilter的登录操作，否则就需要自己去调用Subject.login登录
        chainDefinition.addPathDefinition("/login", "authc");
        chainDefinition.addPathDefinition("/**", "user");
        return chainDefinition;
    }

    /**
     * 缓存管理器
     * @return
     */
    @Bean
    public CacheManager cacheManager() {
        return new MemoryConstrainedCacheManager();
    }
}
