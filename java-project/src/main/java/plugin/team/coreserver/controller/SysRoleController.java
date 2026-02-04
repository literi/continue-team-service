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

import plugin.team.coreserver.domain.SysRole;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.service.SysRoleService;

@Controller
@RequestMapping("/admin/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {
        PageResult<SysRole> pageResult = roleService.list(page, size);
        model.addAttribute("pageResult", pageResult);
        return "fragments/content/role-list :: roleList";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        SysRole role = roleService.getById(id);
        model.addAttribute("role", role);
        return "fragments/components/role-form :: roleForm";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("role", new SysRole());
        return "fragments/components/role-form :: roleForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@ModelAttribute SysRole role,
                                   @RequestParam(required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (status != null) {
                role.setStatus("true".equals(status) || Boolean.parseBoolean(status));
            }
            
            if (role.getId() == null || role.getId().isEmpty()) {
                roleService.create(role);
            } else {
                roleService.update(role.getId(), role);
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
            roleService.delete(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
