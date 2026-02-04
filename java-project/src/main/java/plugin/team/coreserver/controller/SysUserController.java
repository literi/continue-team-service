package plugin.team.coreserver.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import plugin.team.coreserver.domain.SysUser;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.dto.SysUserDTO;
import plugin.team.coreserver.service.SysUserService;

@Controller
@RequestMapping("/admin/user")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {
        PageResult<SysUserDTO> pageResult = userService.list(page, size);
        model.addAttribute("pageResult", pageResult);
        
        // 表格列配置
        List<Map<String, String>> columns = Arrays.asList(
            createColumn("username", "用户名"),
            createColumn("nickname", "昵称"),
            createColumn("phone", "手机号"),
            createColumn("email", "邮箱"),
            createColumn("status", "状态"),
            createColumn("createTime", "创建时间")
        );
        model.addAttribute("columns", columns);
        
        return "fragments/content/user-list :: userList";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        SysUserDTO user = userService.getById(id);
        model.addAttribute("user", user);
        return "fragments/components/user-form :: userForm";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new SysUserDTO());
        return "fragments/components/user-form :: userForm";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@ModelAttribute SysUser user,
                                   @RequestParam(required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 处理status字段的字符串到Boolean转换
            if (status != null) {
                user.setStatus("true".equals(status) || Boolean.parseBoolean(status));
            }
            
            if (user.getId() == null || user.getId().isEmpty()) {
                userService.create(user);
            } else {
                userService.update(user.getId(), user);
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
            userService.delete(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    private Map<String, String> createColumn(String field, String label) {
        Map<String, String> col = new HashMap<>();
        col.put("field", field);
        col.put("label", label);
        return col;
    }
}
