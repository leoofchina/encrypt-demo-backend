package net.zhi365.encryptdemo.config;

import lombok.RequiredArgsConstructor;
import net.zhi365.encryptdemo.compoent.AesDecryptArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcAesConfig implements WebMvcConfigurer {
    private final AesDecryptArgumentResolver resolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }
}
