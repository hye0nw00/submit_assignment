package com.example.demo;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
@Getter
public class StorageProperties {
    private String location = "attached-files";
}
