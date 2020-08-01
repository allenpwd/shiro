package pwd.allen.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * shiro注解实现
 * @see org.apache.shiro.aop.AnnotationMethodInterceptor 的实现类
 *
 * @author pwd
 * @create 2019-03-31 7:57
 **/
@RestController
public class MyController {

    /**
     * @param request
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index(HttpServletRequest request) {
        return new ModelAndView("/index");
    }

    /**
     * get请求返回到登录页面
     * post请求会走 FormAuthenticationFilter 的 登录认证，若出错则会将异常信息放到request.attribute中
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#failureKeyAttribute
     * @param request
     * @param map
     * @return
     */
    @RequestMapping("/login")
    @RequiresGuest //通过身份认证或者记住我登录的话，会报错access denied
    public ModelAndView login(HttpServletRequest request, Map map) {
        if (request.getAttribute("shiroLoginFailure") != null) {
            System.out.println(request.getAttribute("shiroLoginFailure"));
            return new ModelAndView("/error");
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return new ModelAndView("/index");
        }
        return new ModelAndView("/login");
    }

    @GetMapping("/isAuthenticated")
    @RequiresAuthentication
    public ModelAndView isAuthenticated(HttpServletRequest request) {
        return new ModelAndView("/isAuthenticated");
    }

    @GetMapping("/isUser")
    @RequiresUser
    public ModelAndView isUser(HttpServletRequest request, Map<String, Object> map) {
        Subject subject = SecurityUtils.getSubject();
        map.put("subject", subject);
        return new ModelAndView("/isUser", map);
    }

    @GetMapping("/admin")
    @RequiresRoles("admin")
    public ModelAndView admin(HttpServletRequest request) {
        return new ModelAndView("/admin");
    }

    @GetMapping("/md5")
    @RequiresPermissions("md5:test")
    @RequiresRoles(value = {"md5", "admin"}, logical = Logical.OR)
    public ModelAndView md5() {
        return new ModelAndView("md5");
    }

    @GetMapping("/sha256")
    @RequiresPermissions("sha256:test")
    @RequiresRoles(value = {"sha256", "admin"}, logical = Logical.OR)
    public ModelAndView sha256() {
        return new ModelAndView("sha256");
    }

    @GetMapping("/unauthorized")
    public ModelAndView unauthorized() {
        return new ModelAndView("/unauthorized");
    }

}
