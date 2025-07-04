package net.zhi365.encryptdemo.compoent;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.zhi365.encryptdemo.model.EncryptedPayload;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.InputStream;

/**
 * 使用 HandlerMethodArgumentResolver 对参数进行加密
 */
@Component
@RequiredArgsConstructor
public class AesDecryptArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    private final AesCryptoService aesCryptoService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        InputStream is = request.getInputStream();

        if (aesCryptoService.isSkip(request)) {
            return objectMapper.readValue(is, parameter.getParameterType());
        }
        // 非 JSON 请求不处理
        String contentType = request.getContentType();
        if (contentType == null || !contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            return null;
        }

        EncryptedPayload payload = objectMapper.readValue(is, EncryptedPayload.class);
        String cipherText = payload.getData();

        if (cipherText == null || cipherText.isBlank()) {
            throw new IllegalArgumentException("Missing encrypted data in request");
        }

        String decryptedJson = aesCryptoService.decrypt(cipherText);

        // 反序列化为目标参数类型
        return objectMapper.readValue(decryptedJson, parameter.getParameterType());
    }
}
