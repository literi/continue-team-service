package plugin.team.coreserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HtmxController {

    /** 主内容区按菜单加载的 Thymeleaf 片段（供 HTMX 替换 #contentContainer） */
    @GetMapping("/load-content/{menu}")
    public String loadContent(@PathVariable String menu, Model model) {
        switch (menu) {
            case "dashboard":
                model.addAttribute("totalUser", 128);
                model.addAttribute("todayActive", 42);
                model.addAttribute("totalOrder", 356);
                return "fragments/content/dashboard :: dashboard";
            case "setting":
                return "fragments/content/setting :: setting";
            default:
                return "fragments/content/404 :: notFound";
        }
    }
}
