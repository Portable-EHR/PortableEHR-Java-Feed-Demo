/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.portableehr.network.ApiStatus;
import com.portableehr.network.ApiStatusEnum;
import com.portableehr.model.server.Appointment;
import com.portableehr.model.server.Patient;
import com.portableehr.model.server.Practitioner;
import com.portableehr.network.server.request.appointment.AppointmentDispositionParameters;
import com.portableehr.network.server.request.appointment.AppointmentPullParameters;
import com.portableehr.network.server.request.appointment.AppointmentPullParametersDeserializer;
import com.portableehr.network.server.request.appointment.AppointmentPullRequest;
import com.portableehr.network.server.request.patient.PatientReachabilityRequest;
import com.portableehr.network.server.request.patient.PatientPullParameters;
import com.portableehr.network.server.request.patient.PatientPullParametersDeserializer;
import com.portableehr.network.server.request.patient.PatientPullRequest;
import com.portableehr.network.server.request.practitioner.PractitionerPullParameters;
import com.portableehr.network.server.request.practitioner.PractitionerPullParametersDeserializer;
import com.portableehr.network.server.request.practitioner.PractitionerPullRequest;
import com.portableehr.network.server.request.privateMessage.PrivateMessageContentRequest;
import com.portableehr.network.server.request.privateMessage.PrivateMessageStatusRequest;
import com.portableehr.network.server.response.FeedApiResponse;
import com.portableehr.network.server.response.appointment.*;
import com.portableehr.network.server.response.patient.PatientPullBundleResponseContent;
import com.portableehr.network.server.response.patient.PatientPullResponse;
import com.portableehr.network.server.response.patient.PatientPullResponseDeserializer;
import com.portableehr.network.server.response.patient.PatientPullSingleResponseContent;
import com.portableehr.network.server.response.practitioner.PractitionerPullBundleResponseContent;
import com.portableehr.network.server.response.practitioner.PractitionerPullResponse;
import com.portableehr.network.server.response.practitioner.PractitionerPullResponseDeserializer;
import com.portableehr.network.server.response.practitioner.PractitionerPullSingleResponseContent;
import com.portableehr.network.server.response.privateMessage.PrivateMessageContentResponse;
import com.portableehr.network.server.response.privateMessage.PrivateMessageStatusResponse;
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
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RestController
public class FeedApiController implements FeedApi {

    private static final Logger log = LoggerFactory.getLogger(FeedApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final State appState;

    private final FileUtils fileUtils;

    public FeedApiController(ObjectMapper objectMapper, HttpServletRequest request, State appState, FileUtils fileUtils) {
        this.objectMapper = objectMapper;
        this.request = request;

        SimpleModule module = new SimpleModule();
        module.addDeserializer(PatientPullParameters.class, new PatientPullParametersDeserializer());
        module.addDeserializer(PatientPullResponse.class, new PatientPullResponseDeserializer());

        module.addDeserializer(PractitionerPullParameters.class, new PractitionerPullParametersDeserializer());
        module.addDeserializer(PractitionerPullResponse.class, new PractitionerPullResponseDeserializer());

        module.addDeserializer(AppointmentPullParameters.class, new AppointmentPullParametersDeserializer());
        module.addDeserializer(AppointmentPullResponse.class, new AppointmentPullResponseDeserializer());

        this.objectMapper.registerModule(module);
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        this.appState = appState;
        this.fileUtils = fileUtils;
    }

    public ResponseEntity<? extends FeedApiResponse> feedPatientPost(@RequestBody PatientPullRequest patientPullRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(patientPullRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                String content = "";
                PatientPullResponse response = null;
                String option = null;
                switch (patientPullRequest.getCommand()){
                    case "pullSingle" :
                        option = appState.getServerPatientSingleSelected();
                        content = fileUtils.getOptionContent(ServerMockService.SERVER_PATIENT_POST_ROOT, option);
                        response = objectMapper.readValue(content, PatientPullResponse.class);
                        ((PatientPullSingleResponseContent)response.getResponseContent()).getResult().setLastUpdated(new Date());
                        break;

                    case "pullBundle" :
                        option = appState.getServerPatientBundleSelected();
                        content = fileUtils.getOptionContent(ServerMockService.SERVER_PATIENT_POST_ROOT, option);
                        response = objectMapper.readValue(content, PatientPullResponse.class);
                        for (Patient patient : ((PatientPullBundleResponseContent)response.getResponseContent()).getResults()){
                            patient.setLastUpdated(new Date());
                        }
                        break;
                }

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/patient" + patientPullRequest.getCommand(), option));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

    public ResponseEntity<? extends FeedApiResponse> feedPatientPehrReachabilityPost(@RequestBody PatientReachabilityRequest patientPEHRReachabilityRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(patientPEHRReachabilityRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                String content = fileUtils.getOptionContent(ServerMockService.SERVER_PATIENT_POST_ROOT, appState.getServerPatientSingleSelected());
                FeedApiResponse response = objectMapper.readValue(content, FeedApiResponse.class);

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/patient/reachability", appState.getServerPatientSingleSelected()));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

    public ResponseEntity<? extends FeedApiResponse> feedPractitionerPost(@RequestBody PractitionerPullRequest practitionerPullRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(practitionerPullRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {

                String content = "";
                PractitionerPullResponse response = null;
                String option = "";
                switch (practitionerPullRequest.getCommand()){
                    case "pullSingle" :
                        option = appState.getServerPractitionerSingleSelected();
                        content = fileUtils.getOptionContent(ServerMockService.SERVER_PRACTITIONER_POST_ROOT, option);
                        response = objectMapper.readValue(content, PractitionerPullResponse.class);
                        ((PractitionerPullSingleResponseContent)response.getResponseContent()).getResult().setLastUpdated(new Date());
                        break;

                    case "pullBundle" :
                        option = appState.getServerPractitionerBundleSelected();
                        content = fileUtils.getOptionContent(ServerMockService.SERVER_PRACTITIONER_POST_ROOT, option);
                        response = objectMapper.readValue(content, PractitionerPullResponse.class);
                        for (Practitioner practitioner : ((PractitionerPullBundleResponseContent)response.getResponseContent()).getResults()){
                            practitioner.setLastUpdated(new Date());
                        }
                        break;
                }

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/practitioner" + practitionerPullRequest.getCommand(), option));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

    public ResponseEntity<? extends FeedApiResponse> feedPrivateMessageContentPost(@RequestBody PrivateMessageContentRequest privateMessageContentRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(privateMessageContentRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                String content = fileUtils.getOptionContent(ServerMockService.SERVER_PRIVATEMESSAGE_CONTENT_POST_ROOT, appState.getServerPrivateMessageContentSelected());
                PrivateMessageContentResponse response = objectMapper.readValue(content, PrivateMessageContentResponse.class);

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/privateMessage/content", appState.getServerPrivateMessageContentSelected()));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

    public ResponseEntity<? extends FeedApiResponse> feedPrivateMessageStatusPost(@RequestBody PrivateMessageStatusRequest privateMessageStatusRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(privateMessageStatusRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                String content = fileUtils.getOptionContent(ServerMockService.SERVER_PRIVATEMESSAGE_STATUS_POST_ROOT, appState.getServerPrivateMessageStatusSelected());
                PrivateMessageStatusResponse response = objectMapper.readValue(content, PrivateMessageStatusResponse.class);

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/privateMessage/status", appState.getServerPrivateMessageStatusSelected()));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

    public ResponseEntity<? extends FeedApiResponse> feedAppointmentPost(@RequestBody AppointmentPullRequest appointmentPullRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(appointmentPullRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                AppointmentPullResponse response = null;
                String content = "";
                String option = null;
                Calendar startDate = Calendar.getInstance();
                startDate.add(Calendar.HOUR, 48);
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar.HOUR, 49);
                switch (appointmentPullRequest.getCommand()){
                    case "pullSingle" :
                        option = appState.getServerAppointmentSingleSelected();
                        content = fileUtils.getOptionContent(ServerMockService.SERVER_APPOINTMENT_POST_ROOT, option);
                        response = objectMapper.readValue(content, AppointmentPullResponse.class);

                        UUID id = UUID.randomUUID();
                        ((AppointmentPullSingleResponseContent)response.getResponseContent()).getResult().setId(id.toString());
                        ((AppointmentPullSingleResponseContent)response.getResponseContent()).getResult().setFeedItemId(id);
                        ((AppointmentPullSingleResponseContent)response.getResponseContent()).getResult().setLastUpdated(new Date());
                        ((AppointmentPullSingleResponseContent)response.getResponseContent()).getResult().setStartTime(startDate.getTime());
                        ((AppointmentPullSingleResponseContent)response.getResponseContent()).getResult().setEndTime(endDate.getTime());

                        break;

                    case "pullBundle" :
                        option = appState.getServerAppointmentBundleSelected();
                        content = fileUtils.getOptionContent(ServerMockService.SERVER_APPOINTMENT_POST_ROOT, option);
                        response = objectMapper.readValue(content, AppointmentPullResponse.class);
                        for(Appointment appointment : ((AppointmentPullBundleResponseContent)response.getResponseContent()).getResults()){
                            UUID appId = UUID.randomUUID();
                            appointment.setId(appId.toString());
                            appointment.setFeedItemId(appId);
                            appointment.setLastUpdated(new Date());
                            appointment.setStartTime(startDate.getTime());
                            appointment.setEndTime(endDate.getTime());
                        }
                        break;
                }

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/appointment " +appointmentPullRequest.getCommand(), option));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }


    public ResponseEntity<? extends FeedApiResponse> feedAppointmentDispositionPost(@RequestBody AppointmentDispositionParameters appointmentDispositionRequest) throws JsonProcessingException {
        log.info("server << Request Body : " + objectMapper.writeValueAsString(appointmentDispositionRequest));
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                String content = fileUtils.getOptionContent(ServerMockService.SERVER_APPOINTMENT_DISPOSITION_POST_ROOT, appState.getServerAppointmentDispositionSelected());
                AppointmentDispositionResponse response = objectMapper.readValue(content, AppointmentDispositionResponse.class);

                appState.getServerLogs().add(new ServerLogEntry("POST", "/feed/appointment/disposition", appState.getServerAppointmentDispositionSelected()));
                log.info("server >> Response Body: : " + objectMapper.writeValueAsString(response));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
            }
        } else {
            log.error("The only valid value for the header 'Accept' is 'application/json'. Invalid value : " + accept);
        }

        return new ResponseEntity<>(new FeedApiResponse(new ApiStatus(ApiStatusEnum.INTERNAL, "Opps.. better check the log"), null),HttpStatus.OK);
    }

}
