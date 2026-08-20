package com.animal.java.config;

import com.cloudinary.Cloudinary;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${cloudinary.url:}") String cloudinaryUrl) {
        // Empty config is allowed at startup; uploads fail with a clear error if unset.
        if (StringUtils.isBlank(cloudinaryUrl)) {
            return new Cloudinary();
        }
        return new Cloudinary(cloudinaryUrl);
    }
}
