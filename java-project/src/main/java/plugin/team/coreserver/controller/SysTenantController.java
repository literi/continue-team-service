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

import plugin.team.coreserver.domain.SysTenant;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.service.SysTenantService;

@Controller
@RequestMapping("/admin/tenant")
public class SysTenantController {

    private final SysTenantService tenantService;

    public SysTenantController(SysTenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {
        PageResult<SysTenant> pageResult = tenantService.list(page, size);
        model.addAttribute("pageResult", pageResult);
        return "fragments/content/tenant-list :: tenantList";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        SysTenant tenant = tenantService.getById(id);
        model.addAttribute("tenant", tenant);
        return "fragments/components/tenant-form :: tenantForm";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("tenant", new SysTenant());
        return "fragments/components/tenant-form :: tenantForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@ModelAttribute SysTenant tenant,
                                   @RequestParam(required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (status != null) {
                tenant.setStatus("true".equals(status) || Boolean.parseBoolean(status));
            }
            
            if (tenant.getId() == null || tenant.getId().isEmpty()) {
                tenantService.create(tenant);
            } else {
                tenantService.update(tenant.getId(), tenant);
            }
            result.put("success", true);
            result.put("message", "保存成功");
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
            tenantService.delete(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
