package pwd.allen.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author pwd
 * @create 2019-03-31 7:57
 **/
@RestController
public class MyController {

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/login");
    }

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("/index");
    }

    @GetMapping("/admin")
    @RequiresRoles("admin")
    @RequiresPermissions("admin:select")
    public ModelAndView admin() {
        return new ModelAndView("/admin");
    }

    @GetMapping("/role")
    @RequiresPermissions("role:test")
    @RequiresRoles(value = {"role", "admin"}, logical = Logical.OR)
    public ModelAndView role() {
        return new ModelAndView("/role");
    }

    @GetMapping("/unauthorized")
    public ModelAndView unauthorized() {
        return new ModelAndView("/unauthorized");
    }
}
