/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class ApiServerHttpRequestInterceptor implements HandlerInterceptor {

    private final Log log = LogFactory.getLog(ApiServerHttpRequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logRequest(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logResponse(response);
    }

    private void logRequest(HttpServletRequest request) throws IOException {
        log.info("server << URI: " + request.getRequestURI());
        log.info("server << HTTP Method: " + request.getMethod());
        log.info("server << HTTP Headers: " + headersToString(request));
    }

    private void logResponse(HttpServletResponse response) throws IOException {
        log.info("server >> HTTP Status Code: " + response.getStatus());
        log.info("server >> Status Text: " + HttpStatus.valueOf(response.getStatus()));
        log.info("server >> HTTP Headers: " + headersToString(response));
    }

    private String headersToString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
            String headerName = e.nextElement();

            builder.append(System.lineSeparator()).append("\t");
            builder.append(headerName).append("=[");

            for (Enumeration<String> eValue = request.getHeaders(headerName); eValue.hasMoreElements();) {
                builder.append(eValue.nextElement()).append(",");
            }

            builder.setLength(builder.length() - 1); // Get rid of trailing comma
            builder.append("]");
        }
        return builder.toString();
    }

    private String headersToString(HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        for(String headerName : response.getHeaderNames()) {
            builder.append(System.lineSeparator()).append("\t");
            builder.append(headerName).append("=[");

            for(String headerValue : response.getHeaders(headerName)) {
                builder.append(headerValue).append(",");
            }

            builder.setLength(builder.length() - 1); // Get rid of trailing comma
            builder.append("]");
        }
        return builder.toString();
    }
}
