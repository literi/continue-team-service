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
    public Map<String, Object> save(@ModelAttribute SysUserDTO user,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String password) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 处理status字段的字符串到Boolean转换
            if (status != null) {
                user.setStatus("true".equals(status) || Boolean.parseBoolean(status));
            }
            
            SysUser userInfo = new SysUser();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setNickname(user.getNickname());
            userInfo.setPhone(user.getPhone());
            userInfo.setEmail(user.getEmail());
            userInfo.setStatus(user.getStatus());
            userInfo.setTenantId(user.getTenantId());
            
            // 处理密码字段
            if (password != null && !password.isEmpty()) {
                userInfo.setPassword(password);
            }
            
            if (user.getId() == null || user.getId().isEmpty()) {
                // 新增：密码必填
                if (password == null || password.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "保存失败: 新增用户时密码不能为空");
                    return result;
                }
                userService.create(userInfo);
            } else {
                // 更新：密码可选
                userService.update(user.getId(), userInfo);
            }
            
            result.put("success", true);
            result.put("message", "保存成功");
            result.put("closeModal", true);
            result.put("refreshTarget", "#contentContainer");
            result.put("refreshUrl", "/admin/user/list?page=1");
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
            result.put("refreshTarget", "#contentContainer");
            result.put("refreshUrl", "/admin/user/list?page=1");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
