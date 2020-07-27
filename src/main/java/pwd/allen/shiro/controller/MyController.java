package pwd.allen.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pwd
 * @create 2019-03-31 7:57
 **/
@RestController
public class MyController {

    /**
     * get请求返回到登录页面
     * post请求会走 FormAuthenticationFilter 的 登录认证，若出错则会将异常信息放到request.attribute中
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#failureKeyAttribute
     * @param request
     * @param map
     * @return
     */
    @RequestMapping("/login")
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

    @GetMapping("/index")
    public ModelAndView index(HttpServletRequest request) {
        return new ModelAndView("/index");
    }

    @GetMapping("/admin")
    @RequiresRoles("admin")
    @RequiresPermissions("admin:select")
    public ModelAndView admin(HttpServletRequest request) {
        return new ModelAndView("/admin");
    }

    @GetMapping("/role")
    @RequiresPermissions("user:test")
    @RequiresRoles(value = {"user", "admin"}, logical = Logical.OR)
    public ModelAndView role() {
        return new ModelAndView("/role");
    }

    @GetMapping("/unauthorized")
    public ModelAndView unauthorized() {
        return new ModelAndView("/unauthorized");
    }

}
