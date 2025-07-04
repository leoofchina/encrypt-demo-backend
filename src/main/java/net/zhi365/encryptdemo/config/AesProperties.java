package net.zhi365.encryptdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "aes")
public class AesProperties {

    private boolean enable = false;

    private String key;

    private List<String> ignorePaths;

    public List<String> getIgnorePaths(String contextPath) {
        // 自动拼接 contextPath 前缀
        List<String> fullPaths = new ArrayList<>();
        for (String path : ignorePaths) {
            fullPaths.add(contextPath + path);
        }
        return fullPaths;
    }
}