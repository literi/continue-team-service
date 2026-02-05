package plugin.team.coreserver.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugin.team.coreserver.domain.Oauth2RegisteredClient;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.service.Oauth2RegisteredClientService;

@Controller
@RequestMapping("/admin/oauth2/client")
public class Oauth2ClientController {

    private final Oauth2RegisteredClientService clientService;

    public Oauth2ClientController(Oauth2RegisteredClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {
        PageResult<Oauth2RegisteredClient> pageResult = clientService.list(page, size);
        model.addAttribute("pageResult", pageResult);
        return "fragments/content/oauth2-client-list :: clientList";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        Oauth2RegisteredClient client = clientService.getById(id);
        model.addAttribute("client", client);
        return "fragments/components/oauth2-client-form :: clientForm";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("client", new Oauth2RegisteredClient());
        return "fragments/components/oauth2-client-form :: clientForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@ModelAttribute Oauth2RegisteredClient client,
                                   @RequestParam(required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (status != null) {
                client.setStatus("true".equals(status) || Boolean.parseBoolean(status));
            }
            
            if (client.getId() == null || client.getId().isEmpty()) {
                clientService.create(client);
            } else {
                clientService.update(client.getId(), client);
            }
            
            result.put("success", true);
            result.put("message", "保存成功");
            result.put("closeModal", true);
            result.put("refreshTarget", "#contentContainer");
            result.put("refreshUrl", "/admin/oauth2/client/list?page=1");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> delete(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            clientService.delete(id);
            result.put("success", true);
            result.put("message", "删除成功");
            result.put("refreshTarget", "#contentContainer");
            result.put("refreshUrl", "/admin/oauth2/client/list?page=1");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
