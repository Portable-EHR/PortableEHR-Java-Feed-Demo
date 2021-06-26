/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portableehr.network.ApiStatus;
import com.portableehr.network.ApiStatusEnum;
import com.portableehr.network.server.request.login.LoginRequest;
import com.portableehr.network.server.response.FeedApiResponse;
import com.portableehr.network.server.response.login.LoginResponse;
import com.portableehr.service.FileUtils;
import com.portableehr.service.ServerLogEntry;
import com.portableehr.service.ServerMockService;
import com.portableehr.service.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class LoginApiController implements LoginApi {

    private static final Logger log = LoggerFactory.getLogger(LoginApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final ServerMockService serverMockService;

    private final State appState;

    private final FileUtils fileUtils;

    public LoginApiController(ObjectMapper objectMapper, HttpServletRequest request, ServerMockService serverMockService, State appState, FileUtils fileUtils) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.serverMockService = serverMockService;
        this.appState = appState;
        this.fileUtils = fileUtils;
    }

    public ResponseEntity<? extends FeedApiResponse> loginPost(@RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        log.info("<<< /login received : " + objectMapper.writeValueAsString(loginRequest));
        try {
            String content = fileUtils.getOptionContent(ServerMockService.SERVER_LOGIN_POST_ROOT, appState.getServerLoginSelected());
            LoginResponse response = objectMapper.readValue(content, LoginResponse.class);

            appState.getServerLogs().add(new ServerLogEntry("POST", "/login", appState.getServerLoginSelected()));
            log.info(">>> /login responded : " + objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error during FeedHub login attempt", e);
        }

        log.info(">>> /login responded : 500");
        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

}
