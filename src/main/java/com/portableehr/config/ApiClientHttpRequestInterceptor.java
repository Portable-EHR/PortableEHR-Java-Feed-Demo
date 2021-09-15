/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ApiClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Log log = LogFactory.getLog(ApiClientHttpRequestInterceptor.class);
    private ObjectMapper om = new ObjectMapper();

    public ApiClientHttpRequestInterceptor() {
        om.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("client >> URI: " + request.getURI());
        if(log.isDebugEnabled())log.debug("client >> HTTP Method: " + request.getMethod());
        if(log.isDebugEnabled())log.debug("client >> HTTP Headers: " + headersToString(request.getHeaders()));
        log.info("client >> Request Body: " + System.lineSeparator() + new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info("client << HTTP Status Code: " + response.getRawStatusCode());
        if(log.isDebugEnabled())log.debug("client << Status Text: " + response.getStatusText());
        if(log.isDebugEnabled())log.debug("client << HTTP Headers: " + headersToString(response.getHeaders()));
        log.info("client << Response Body: " + System.lineSeparator() + bodyToString(response.getBody()));
    }

    private String headersToString(HttpHeaders headers) {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
            builder.append(System.lineSeparator()).append("\t");
            builder.append(entry.getKey()).append("=[");
            for(String value : entry.getValue()) {
                builder.append(value).append(",");
            }
            builder.setLength(builder.length() - 1); // Get rid of trailing comma
            builder.append("]");
        }
        return builder.toString();
    }

    private String bodyToString(InputStream body) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
        String line = bufferedReader.readLine();
        while (line != null) {
            builder.append(line).append(System.lineSeparator());
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        JsonNode jnode = om.readTree(builder.toString());
        return om.writeValueAsString(jnode);
    }

}
