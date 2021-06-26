/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.portableehr.network.ApiStatusEnum;
import com.portableehr.network.client.response.FeedHubApiResponse;
import com.portableehr.network.client.response.idIssuers.IdIssuersResponse;
import com.portableehr.network.client.response.login.LoginResponse;
import com.portableehr.network.client.response.patient.PatientReachabilityResponse;
import com.portableehr.network.client.request.FeedBackendRequest;
import com.portableehr.network.client.request.RequestToFeedHub;
import com.portableehr.network.client.request.login.LoginRequest;
import com.portableehr.network.client.request.patient.PatientReachabilityRequest;
import com.portableehr.network.client.request.privateMessage.PrivateMessageNotificationParameters;
import com.portableehr.network.client.request.privateMessage.PrivateMessageNotificationParametersDeserializer;
import com.portableehr.network.client.request.privateMessage.PrivateMessageNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ClientMockService {

    private static final Logger log = LoggerFactory.getLogger(ClientMockService.class);

    public static final String CLIENT_LOGIN_POST_ROOT                           = "mocks/feedRequests/login/post";
    public static final String CLIENT_PATIENT_REACHABILITY_POST_ROOT            = "mocks/feedRequests/backend/patient/reachability/post";
    public static final String CLIENT_PRIVATEMESSAGE_NOTIFICATIONS_POST_ROOT    = "mocks/feedRequests/backend/privateMessage/notifications/post";
    public static final String CLIENT_IDISSUERS_POST_ROOT                       = "mocks/feedRequests/backend/idIssuers/post";

    @Value("${feedhub.basePath}")
    private String feedHubBasePath;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    private final State appState;

    private final FileUtils fileUtils;

    public ClientMockService(ObjectMapper objectMapper, RestTemplate restTemplate, State appState, FileUtils fileUtils) {
        this.objectMapper = objectMapper;
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrivateMessageNotificationParameters.class, new PrivateMessageNotificationParametersDeserializer());
        this.objectMapper.registerModule(module);
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        this.restTemplate = restTemplate;
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        this.restTemplate.getMessageConverters().add(0, converter);
        this.appState = appState;
        this.fileUtils = fileUtils;
    }

    @PostConstruct
    public void init(){
        reloadOptions();
    }

    public State reloadOptions() {
        appState.getClientLoginOptions().clear();
        for(String json: fileUtils.listFiles(CLIENT_LOGIN_POST_ROOT)){
            appState.getClientLoginOptions().add(json);
        }

        appState.getClientPatientReachabilityOptions().clear();
        for(String json: fileUtils.listFiles(CLIENT_PATIENT_REACHABILITY_POST_ROOT)){
            appState.getClientPatientReachabilityOptions().add(json);
        }

        appState.getClientPrivateMessageNotificationOptions().clear();
        for(String json: fileUtils.listFiles(CLIENT_PRIVATEMESSAGE_NOTIFICATIONS_POST_ROOT)){
            appState.getClientPrivateMessageNotificationOptions().add(json);
        }

        appState.getClientIdIssuersOptions().clear();
        for(String json: fileUtils.listFiles(CLIENT_IDISSUERS_POST_ROOT)){
            appState.getClientIdIssuersOptions().add(json);
        }

        return appState;
    }

    private <T extends RequestToFeedHub, M extends FeedHubApiResponse> M callFeedHub(String path, T requestBody, Class<M> responseClass) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(appState.getClientJWTAuthToken());
        HttpEntity<RequestToFeedHub> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<M> responseEntity = restTemplate.postForEntity(feedHubBasePath + path, requestEntity, responseClass);

        if (responseEntity.getStatusCode().is2xxSuccessful() && ApiStatusEnum.OK.equals(responseEntity.getBody().getRequestStatus().getStatus())) {
            return responseEntity.getBody();
        } else {
            StringBuilder sb = new StringBuilder(responseEntity.getStatusCode().toString());
            if(responseEntity.getBody() != null) {
                sb.append(" | status = ").append(responseEntity.getBody().getRequestStatus().getStatus());
                sb.append(" | message = ").append(responseEntity.getBody().getRequestStatus().getMessage());
            }
            throw new Exception(sb.toString());
        }
    }

    public String callLogin(String option) throws Exception {
        String json = fileUtils.getOptionContent(ClientMockService.CLIENT_LOGIN_POST_ROOT, option);

        LoginRequest loginRequest = objectMapper.readValue(json, LoginRequest.class);

        LoginResponse loginOKResponse = callFeedHub("/login", loginRequest, LoginResponse.class);

        appState.setClientJWTAuthToken(loginOKResponse.getResponseContent().getToken());
        return objectMapper.writeValueAsString(loginOKResponse);
    }


    public String callPatientReachability(String option) throws Exception {
        String json = fileUtils.getOptionContent(ClientMockService.CLIENT_PATIENT_REACHABILITY_POST_ROOT, option);

        PatientReachabilityRequest reachabilityRequest = objectMapper.readValue(json, PatientReachabilityRequest.class);

        PatientReachabilityResponse reachabilityResponse = callFeedHub("/backend/patient/reachability", reachabilityRequest, PatientReachabilityResponse.class);

        return objectMapper.writeValueAsString(reachabilityResponse);
    }

    public String callPrivateMessageNotifications(String option) throws Exception {
        String json = fileUtils.getOptionContent(ClientMockService.CLIENT_PRIVATEMESSAGE_NOTIFICATIONS_POST_ROOT, option);

        PrivateMessageNotificationRequest privateMessageNotificationRequest = objectMapper.readValue(json, PrivateMessageNotificationRequest.class);
        privateMessageNotificationRequest.getParameters().setMessageId(UUID.randomUUID().toString());

        FeedHubApiResponse privateMessageNotificationResponse = callFeedHub("/backend/privateMessage/notification", privateMessageNotificationRequest, FeedHubApiResponse.class);

        return objectMapper.writeValueAsString(privateMessageNotificationResponse);
    }

    public String callIdIssuers(String option) throws Exception {
        String json = fileUtils.getOptionContent(ClientMockService.CLIENT_IDISSUERS_POST_ROOT, option);

        FeedBackendRequest idIssuersRequest = objectMapper.readValue(json, FeedBackendRequest.class);

        IdIssuersResponse idIssuersResponse = callFeedHub("/backend/idissuers", idIssuersRequest, IdIssuersResponse.class);

        return objectMapper.writeValueAsString(idIssuersResponse);
    }

}
