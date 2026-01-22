package plugin.team.coreserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plugin.team.coreserver.common.Result;
import plugin.team.coreserver.common.dto.UserInfoDTO;
import plugin.team.coreserver.infrastructure.security.JwtTokenProvider;
import plugin.team.coreserver.service.UserManagementService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 用户个人信息和菜单相关控制器
 */
@Tag(name = "用户个人信息")
@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserManagementService userManagementService;

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public ResponseEntity<Result<UserInfoDTO>> getCurrentUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Result.error("未提供有效的认证令牌"));
        }

        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.ok(Result.error("无效的令牌"));
        }

        UserInfoDTO userInfo = userManagementService.getUserInfoById(userId);
        if (userInfo == null) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }

        return ResponseEntity.ok(Result.success(userInfo));
    }

    /**
     * 获取当前用户的菜单（根据角色查询）
     */
    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/menu")
    public ResponseEntity<Result<List<Map<String, Object>>>> getCurrentUserMenu(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Result.error("未提供有效的认证令牌"));
        }

        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.ok(Result.error("无效的令牌"));
        }

        // 获取用户的角色
        List<Long> roleIds = userManagementService.getUserRoleIds(userId);
        
        // 根据角色获取菜单权限
        List<Map<String, Object>> menus = userManagementService.getMenusByRoleIds(roleIds);
        
        return ResponseEntity.ok(Result.success(menus));
    }
}