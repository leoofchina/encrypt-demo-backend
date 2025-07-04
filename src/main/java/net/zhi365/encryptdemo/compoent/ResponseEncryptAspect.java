package net.zhi365.encryptdemo.compoent;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.zhi365.encryptdemo.model.EncryptResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 对响应数据进行加密
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ResponseEncryptAspect {

    private final ObjectMapper objectMapper;

    private final AesCryptoService aesCryptoService;

    /**
     * 对RestController注解进行处理
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object aroundResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (aesCryptoService.isSkip(request)) {
            return result;
        }

        String json = objectMapper.writeValueAsString(result);

        String encrypted = aesCryptoService.encrypt(json);

        return new EncryptResponse(encrypted);
    }
}