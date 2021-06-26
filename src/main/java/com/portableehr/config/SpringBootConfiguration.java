/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SpringBootConfiguration implements WebMvcConfigurer {

  @Bean
  @Primary
  public ObjectMapper jacksonObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  @Bean
  public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
    sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

    CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContextBuilder.build()).setSSLHostnameVerifier(
            NoopHostnameVerifier.INSTANCE)
            .build();

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

    requestFactory.setHttpClient(httpClient);

    // To be able to log the client's requests and responses body
    RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));

    List<ClientHttpRequestInterceptor> currentInterceptors = new ArrayList<>();
    ClientHttpRequestInterceptor interceptor = new ApiClientHttpRequestInterceptor();
    currentInterceptors.add(interceptor);
    restTemplate.setInterceptors(currentInterceptors);

    return restTemplate;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ApiServerHttpRequestInterceptor()).addPathPatterns("/login", "/feed/**");
  }

}

