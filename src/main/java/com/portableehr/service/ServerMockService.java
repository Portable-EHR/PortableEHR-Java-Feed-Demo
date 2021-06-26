/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ServerMockService {

    private static final Logger log = LoggerFactory.getLogger(ServerMockService.class);

    public static final String SERVER_LOGIN_POST_ROOT                       = "mocks/feedResponses/login/post";
    public static final String SERVER_PATIENT_POST_ROOT                     = "mocks/feedResponses/patient/post";
    public static final String SERVER_PATIENT_PEHRREACHABILITY_POST_ROOT    = "mocks/feedResponses/patient/pehrReachability/post";
    public static final String SERVER_PRACTITIONER_POST_ROOT                = "mocks/feedResponses/practitioner/post";
    public static final String SERVER_PRIVATEMESSAGE_CONTENT_POST_ROOT      = "mocks/feedResponses/privateMessage/content/post";
    public static final String SERVER_PRIVATEMESSAGE_STATUS_POST_ROOT       = "mocks/feedResponses/privateMessage/status/post";
    public static final String SERVER_APPOINTMENT_POST_ROOT                 = "mocks/feedResponses/appointment/post";
    public static final String SERVER_APPOINTMENT_DISPOSITION_POST_ROOT     = "mocks/feedResponses/appointment/disposition/post";

    private final State appState;

    private final FileUtils fileUtils;

    public ServerMockService(State appState, FileUtils fileUtils) {
        this.appState = appState;
        this.fileUtils = fileUtils;
    }

    @PostConstruct
    public void init(){
        appState.setServerLoginSelected("default.json");
        appState.setServerPatientSingleSelected("single.json");
        appState.setServerPatientBundleSelected("bundle_empty.json");
        appState.setServerPatientPehrReachabilitySelected("default.json");
        appState.setServerPractitionerSingleSelected("single.json");
        appState.setServerPractitionerBundleSelected("bundle_empty.json");
        appState.setServerPrivateMessageContentSelected("default.json");
        appState.setServerPrivateMessageStatusSelected("default.json");
        appState.setServerAppointmentSingleSelected("single_confirmed.json");
        appState.setServerAppointmentBundleSelected("bundle_empty.json");
        appState.setServerAppointmentDispositionSelected("default.json");

        reloadOptions();
    }

    public State reloadOptions() {
        appState.getServerLoginOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_LOGIN_POST_ROOT)){
            appState.getServerLoginOptions().add(json);
        }

        appState.getServerPatientOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_PATIENT_POST_ROOT)){
            appState.getServerPatientOptions().add(json);
        }

        appState.getServerPatientPehrReachabilityOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_PATIENT_PEHRREACHABILITY_POST_ROOT)){
            appState.getServerPatientPehrReachabilityOptions().add(json);
        }

        appState.getServerPractitionerOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_PRACTITIONER_POST_ROOT)){
            appState.getServerPractitionerOptions().add(json);
        }

        appState.getServerPrivateMessageContentOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_PRIVATEMESSAGE_CONTENT_POST_ROOT)){
            appState.getServerPrivateMessageContentOptions().add(json);
        }

        appState.getServerPrivateMessageStatusOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_PRIVATEMESSAGE_STATUS_POST_ROOT)){
            appState.getServerPrivateMessageStatusOptions().add(json);
        }

        appState.getServerAppointmentOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_APPOINTMENT_POST_ROOT)){
            appState.getServerAppointmentOptions().add(json);
        }

        appState.getServerAppointmentDispositionsOptions().clear();
        for(String json: fileUtils.listFiles(SERVER_APPOINTMENT_DISPOSITION_POST_ROOT)){
            appState.getServerAppointmentDispositionsOptions().add(json);
        }

        return appState;
    }
}
