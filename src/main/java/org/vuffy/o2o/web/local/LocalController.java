package org.vuffy.o2o.web.local;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("local")
public class LocalController {
    /**
     * 绑定账号
     *
     * @return
     */
    @RequestMapping(value = "/accountbind", method = RequestMethod.GET)
    private String accountBind() {
        return "local/accountbind";
    }

    /**
     * 修改密码
     *
     * @return
     */
    @RequestMapping(value = "/changepsw", method = RequestMethod.GET)
    private String changepsw() {
        return "local/changepsw";
    }

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    private String login() {
        return "local/login";
    }


}
