package plugin.team.coreserver.common.vo;

import lombok.Data;

/**
 * 验证码视图对象
 */
@Data
public class CaptchaVO {
    /**
     * 验证码唯一标识
     */
    private String uuid;
    /**
     * 验证码图片Base64字符串
     */
    private String img;
    /**
     * 验证码图片类型
     */
    private String type;

    public CaptchaVO() {
    }

    public CaptchaVO(String uuid, String img, String type) {
        this.uuid = uuid;
        this.img = img;
        this.type = type;
    }
}