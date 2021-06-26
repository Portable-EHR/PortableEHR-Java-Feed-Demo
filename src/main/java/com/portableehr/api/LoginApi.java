/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.25).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.portableehr.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portableehr.network.server.request.login.LoginRequest;
import com.portableehr.network.server.response.FeedApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface LoginApi {

    @RequestMapping(value = "/login",
        produces = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<? extends FeedApiResponse> loginPost(@RequestBody LoginRequest loginRequest) throws JsonProcessingException;

}
