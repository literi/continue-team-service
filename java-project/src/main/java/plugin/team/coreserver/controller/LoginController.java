package plugin.team.coreserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 对应 templates/login.html
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("sidebarCollapsed", false);
        model.addAttribute("loginUser", "访客");
        model.addAttribute("totalUser", 0);
        model.addAttribute("todayActive", 0);
        model.addAttribute("totalOrder", 0);
        return "index";
    }
}
