/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.controller;

import com.portableehr.service.ServerLogEntry;
import com.portableehr.service.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ServerController {

    private static final Logger log = LoggerFactory.getLogger(ServerController.class);

    private final State appState;

    public ServerController(State appState) {
        this.appState = appState;
    }

    @PutMapping("/server/login")
    public ResponseEntity<Void> selectLogin(String option){
        log.info("Set server's /login selected option : " + option);
        appState.setServerLoginSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/server/feed/patientSingle")
    public ResponseEntity<Void> selectPatientSingle(String option){
        log.info("Set server's pullSingle /feed/patient selected option : " + option);
        appState.setServerPatientSingleSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/patientBundle")
    public ResponseEntity<Void> selectPatientBundle(String option){
        log.info("Set server's pullBundle /feed/patient selected option : " + option);
        appState.setServerPatientBundleSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/patient/pehrReachability")
    public ResponseEntity<Void> selectPatientPEHRReachability(String option){
        log.info("Set server's /feed/pehrReachability selected option : " + option);
        appState.setServerPatientPehrReachabilitySelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/practitionerSingle")
    public ResponseEntity<Void> selectPractitionerSingle(String option){
        log.info("Set server's pullSingle /feed/practitioner selected option : " + option);
        appState.setServerPractitionerSingleSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/practitionerBundle")
    public ResponseEntity<Void> selectPractitionerBundle(String option){
        log.info("Set server's pullBundle /feed/practitioner selected option : " + option);
        appState.setServerPractitionerBundleSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/privateMessage/content")
    public ResponseEntity<Void> selectPrivateMessageContent(String option){
        log.info("Set server's /feed/privateMessage/content selected option : " + option);
        appState.setServerPrivateMessageContentSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/privateMessage/status")
    public ResponseEntity<Void> selectStatus(String option){
        log.info("Set server's /feed/privateMessage/content selected option : " + option);
        appState.setServerPrivateMessageStatusSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/appointmentSingle")
    public ResponseEntity<Void> selectAppointmentSingle(String option){
        log.info("Set server's pullSingle /feed/appointment selected option : " + option);
        appState.setServerAppointmentSingleSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/appointmentBundle")
    public ResponseEntity<Void> selectAppointmentBundle(String option){
        log.info("Set server's pullBundle /feed/appointment selected option : " + option);
        appState.setServerAppointmentBundleSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/server/feed/appointment/disposition")
    public ResponseEntity<Void> selectAppointmentDisposition(String option){
        log.info("Set server's /feed/appointment/disposition selected option : " + option);
        appState.setServerAppointmentDispositionSelected(option);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = "/server/logs", produces = { "application/json" })
    public ResponseEntity<List<ServerLogEntry>> getServerLogs(){
        try {
            List<ServerLogEntry> response = null;
            synchronized (appState.getServerLogs()){
                response = appState.getServerLogs().stream().collect(Collectors.toList());
                appState.getServerLogs().clear();
            }

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
