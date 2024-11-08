package com.animal.java.qa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@NoArgsConstructor
public class APIClient {

    @Autowired
    private RetryTemplate retryTemplate;
    private RestTemplate retryRestTemplate;

    @Getter
    private RestTemplate recoveryRestTemplate;

    public void setRestTemplates(RestTemplateBuilder retryRestTemplateBuilder, RestTemplateBuilder recoveryRestTemplateBuilder){
        retryRestTemplate = retryRestTemplateBuilder.build();
        recoveryRestTemplate = recoveryRestTemplateBuilder.build();
    }

    protected final <T> ResponseEntity<T> retryableExchange(String url, HttpMethod httpMethod,
                                                            @Nullable HttpEntity<?> requestEntity, Class<T> responseType,
                                                            Object... uriVariables){
        return retryTemplate.execute(
                ctx -> retryRestTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables),
                ctx -> recoveryRestTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables));
    }

    protected final <T> ResponseEntity<T> retryableExchange(String url, HttpMethod httpMethod,
                                                            @Nullable HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType,
                                                            Object... uriVariables){
        return retryTemplate.execute(
                ctx -> retryRestTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables),
                ctx -> recoveryRestTemplate.exchange(url, httpMethod, requestEntity, responseType, uriVariables));
    }
}
