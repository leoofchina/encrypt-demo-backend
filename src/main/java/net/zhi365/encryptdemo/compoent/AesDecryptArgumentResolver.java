package net.zhi365.encryptdemo.compoent;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.zhi365.encryptdemo.config.AesProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AesDecryptArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;
    private final AesProperties aesProps;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class)
                && aesProps.isEnable();
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String path = request.getRequestURI();

        // 获取 /encrypt
        String contextPath = request.getContextPath();

        // 命中忽略路径直接抛异常，跳过解析（或你可以选择 return null 或 joinPoint.proceed）
        if (aesProps.getIgnorePaths(contextPath).contains(path)) {
            throw new IllegalStateException("AES decrypt skipped for ignored path: " + path);
        }

        // 非 JSON 请求不处理
        String contentType = request.getContentType();
        if (contentType == null || !contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            return null;
        }

        // 读取原始请求体（应为：{ "data": "<Base64密文>" }）
        InputStream is = request.getInputStream();
        Map<String, String> encryptedMap = objectMapper.readValue(is, Map.class);
        String cipherText = encryptedMap.get("data");

        if (cipherText == null || cipherText.isBlank()) {
            throw new IllegalArgumentException("Missing encrypted data in request");
        }

        String decryptedJson = decrypt(cipherText, aesProps.getKey());

        // 反序列化为目标参数类型（如 DeviceForm）
        return objectMapper.readValue(decryptedJson, parameter.getParameterType());
    }

    private String decrypt(String encryptedBase64, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES"); // ECB 模式
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);
        byte[] result = cipher.doFinal(decoded);
        return new String(result, StandardCharsets.UTF_8);
    }
}
