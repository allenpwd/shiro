#### 身份认证流程
1. 首先调用 Subject.login(token)进行登录,其会自动委托给 SecurityManager,调用之前必
须通过 SecurityUtils.setsecuritymanager设置
2. Securitymanager负责真正的身份验证逻辑;它会委托给 Authenticator进行身份验证
3. Authenticator才是真正的身份验证者, Shiro API中核心的身份认证入口点,此处可以自定义插入自己的实现
4. Authenticator可能会委托给相应的 AuthenticationStrategy进行多 Realm身份验证,默认
Modularrealmauthenticator会调用 AuthenticationStrategy进行多 Realm身份验证;
5. Authenticator会把相应的 token传入 Realm,从 Realm获取身份验证信息,如果没有返
回/抛出异常表示身份验证失败了。此处可以配置多个 Realm,将按照相应的顺序及策略进
行访问。
