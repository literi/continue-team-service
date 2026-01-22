package plugin.team.coreserver.service;

import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务类
 */
@Service
@Slf4j
public class CaptchaService {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成验证码
     *
     * @return 验证码VO
     */
    public plugin.team.coreserver.common.vo.CaptchaVO createCaptcha() {
        // 生成uuid
        String uuid = UUID.randomUUID().toString();
        // 生成文字验证码
        String code = captchaProducer.createText();
        // 生成图片验证码
        BufferedImage image = captchaProducer.createImage(code);
        // 转换为base64字符串
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        try {
            javax.imageio.ImageIO.write(image, "png", outputStream);
        } catch (java.io.IOException e) {
            log.error("生成验证码图片失败", e);
            throw new RuntimeException("生成验证码图片失败", e);
        }
        String base64Img = "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());

        // 将验证码存入redis，有效期60秒
        redisTemplate.opsForValue().set("captcha:" + uuid, code, 60, TimeUnit.SECONDS);

        plugin.team.coreserver.common.vo.CaptchaVO captchaVO = new plugin.team.coreserver.common.vo.CaptchaVO();
        captchaVO.setUuid(uuid);
        captchaVO.setImg(base64Img);
        captchaVO.setType("image");

        return captchaVO;
    }

    /**
     * 验证验证码
     *
     * @param uuid 验证码uuid
     * @param code 用户输入的验证码
     * @return 验证结果
     */
    public Boolean validateCaptcha(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }
        String key = "captcha:" + uuid;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode == null) {
            return false;
        }
        boolean result = savedCode.equalsIgnoreCase(code);
        if (result) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
        }
        return result;
    }
}