package net.zhi365.encryptdemo.compoent;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.zhi365.encryptdemo.config.AesProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseEncryptAspect {

    private final AesProperties aesProps;
    private final ObjectMapper objectMapper;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object aroundResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (!aesProps.isEnable()) {
            return result;
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String path = request.getRequestURI();

        // 获取 /encrypt
        String contextPath = request.getContextPath();

        if (aesProps.getIgnorePaths(contextPath).contains(path)) {
            return result;
        }

        String json = objectMapper.writeValueAsString(result);
        String encrypted = encrypt(json, aesProps.getKey());

        return Map.of("data", encrypted);
    }

    private String encrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }
}