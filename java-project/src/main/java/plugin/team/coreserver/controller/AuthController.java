package plugin.team.coreserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plugin.team.coreserver.common.Result;
import plugin.team.coreserver.common.vo.CaptchaVO;
import plugin.team.coreserver.service.AuthService;
import plugin.team.coreserver.service.CaptchaService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(name = "认证管理", description = "用户认证、登录、注册相关接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private CaptchaService captchaService;
    
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，需要提供有效的验证码")
    @ApiResponse(responseCode = "200", description = "登录成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Result.class)))
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String captcha = request.get("captcha");
        String uuid = request.get("uuid");
        
        if (username == null || password == null) {
            return ResponseEntity.ok(Result.error(400, "Username and password are required"));
        }
        
        // 验证验证码
        if (captcha == null || uuid == null) {
            return ResponseEntity.ok(Result.error(400, "Captcha and uuid are required"));
        }
        
        Boolean captchaValid = captchaService.validateCaptcha(uuid, captcha);
        if (!captchaValid) {
            return ResponseEntity.ok(Result.error(400, "Invalid captcha"));
        }
        
        String token = authService.authenticate(username, password);
        
        if (token == null) {
            return ResponseEntity.ok(Result.error(401, "Invalid credentials"));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        
        return ResponseEntity.ok(Result.success("Login successful", response));
    }
    
    @Operation(summary = "用户注册", description = "用户注册接口，需要提供租户ID、用户名、密码、邮箱和角色")
    @ApiResponse(responseCode = "200", description = "注册成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Result.class)))
    @PostMapping("/register")
    public ResponseEntity<Result<String>> register(@RequestBody Map<String, String> request) {
        String tenantId = request.get("tenantId");
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String role = request.getOrDefault("role", "DEVELOPER");
        
        try {
            authService.registerUser(Long.parseLong(tenantId), username, password, email, role);
            return ResponseEntity.ok(Result.success("User registered successfully"));
        } catch (Exception e) {
            log.error("Error registering user", e);
            return ResponseEntity.ok(Result.error(e.getMessage()));
        }
    }
    
    @Operation(summary = "获取验证码", description = "获取图形验证码，用于登录验证")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Result.class)))
    @GetMapping("/captcha")
    public ResponseEntity<Result<CaptchaVO>> getCaptcha() {
        try {
            CaptchaVO captcha = captchaService.createCaptcha();
            return ResponseEntity.ok(Result.success(captcha));
        } catch (Exception e) {
            log.error("Error generating captcha", e);
            return ResponseEntity.ok(Result.error("Failed to generate captcha"));
        }
    }
}
