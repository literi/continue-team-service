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

import plugin.team.coreserver.domain.SysPermission;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.service.SysPermissionService;

@Controller
@RequestMapping("/admin/permission")
public class SysPermissionController {

    private final SysPermissionService permissionService;

    public SysPermissionController(SysPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {
        PageResult<SysPermission> pageResult = permissionService.list(page, size);
        model.addAttribute("pageResult", pageResult);
        return "fragments/content/permission-list :: permissionList";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        SysPermission permission = permissionService.getById(id);
        model.addAttribute("permission", permission);
        return "fragments/components/permission-form :: permissionForm";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("permission", new SysPermission());
        return "fragments/components/permission-form :: permissionForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@ModelAttribute SysPermission permission,
                                   @RequestParam(required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (status != null) {
                permission.setStatus("true".equals(status) || Boolean.parseBoolean(status));
            }
            
            if (permission.getId() == null || permission.getId().isEmpty()) {
                permissionService.create(permission);
            } else {
                permissionService.update(permission.getId(), permission);
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
            permissionService.delete(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
