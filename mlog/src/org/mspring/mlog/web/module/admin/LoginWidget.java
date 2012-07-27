/**
 * 
 */
package org.mspring.mlog.web.module.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mspring.mlog.entity.User;
import org.mspring.mlog.service.UserService;
import org.mspring.mlog.web.GlobalUtils;
import org.mspring.mlog.web.Keys;
import org.mspring.platform.utils.CookieUtils;
import org.mspring.platform.web.widget.stereotype.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Gao Youbo
 * @since 2012-7-16
 * @Description
 * @TODO
 */
@Widget
@RequestMapping("/admin")
public class LoginWidget {

    private static final String MESSAGE = "message";

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = { "/login" }, method = { RequestMethod.GET })
    public String login(@ModelAttribute User user, BindingResult result, HttpServletRequest request) {
        User currentUser = GlobalUtils.getCurrentUser(request);
        if (currentUser != null) {
            return "redirect:/admin";
        }
        return "/admin/login";
    }

    @RequestMapping(value = { "/doLogin" }, method = { RequestMethod.POST })
    public String doLogin(@ModelAttribute User user, BindingResult result, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        User loginUser = userService.login(user.getName(), user.getPassword());
        if (loginUser != null) {
            session.setAttribute(Keys.CURRENT_USER, loginUser);
            if (user.isRememberMe()) {
                CookieUtils.setCookie(response, Keys.IS_REMEMBER_USER_COOKIE, "true");
                CookieUtils.setCookie(response, Keys.USERNAME_COOKIE, user.getName());
                CookieUtils.setCookie(response, Keys.PASSWORD_COOKIE, user.getPassword());
            }
            return "redirect:/admin";
        }
        model.addAttribute(MESSAGE, "登录失败");
        return "/admin/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@ModelAttribute User user, BindingResult result, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        session.invalidate();
        CookieUtils.setCookie(response, Keys.IS_REMEMBER_USER_COOKIE, "false");
        return "/admin/login";
    }
}