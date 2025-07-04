package net.zhi365.encryptdemo.compoent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.zhi365.encryptdemo.config.AesProperties;
import net.zhi365.encryptdemo.utils.AESUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AesCryptoService {

    private final AesProperties aesProps;

    /**
     * 配置文件中的加密选项未开启或请求路径在忽略路径中时跳过加解密
     * @param request
     * @return
     */
    public boolean isSkip(HttpServletRequest request) {
        if (!aesProps.isEnable()) {
            return true;
        }

        String path = request.getRequestURI();

        String contextPath = request.getContextPath();

        List<String> ignores = aesProps.getIgnorePaths(contextPath);
        return ignores != null && ignores.contains(path);
    }

    /**
     * 加密
     * @param plaintext
     * @return
     * @throws Exception
     */
    public String encrypt(String plaintext) throws Exception {
        return AESUtil.encrypt(plaintext, aesProps.getKey());
    }

    /**
     * 解密
     * @param ciphertext
     * @return
     * @throws Exception
     */
    public String decrypt(String ciphertext) throws Exception {
        return AESUtil.decrypt(ciphertext, aesProps.getKey());
    }
}
