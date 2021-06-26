/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.controller;

import com.portableehr.service.ClientMockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientMockService clientMockService;

    public ClientController(ClientMockService clientMockService) {
        this.clientMockService = clientMockService;
    }

    @PutMapping("/client/login")
    public ResponseEntity<String> callLogin(String option){
        log.info("Call client's /login with option : " + option);

        try {
            String response = clientMockService.callLogin(option);

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/client/backend/patient/reachability")
    public ResponseEntity<String> callPatientReachability(String option){
        log.info("Call client's /backend/patient/reachability with option : " + option);

        try {
            String response = clientMockService.callPatientReachability(option);

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/client/backend/privateMessage/notification")
    public ResponseEntity<String> callPrivateMessageNotifications(String option){
        log.info("Call client's /backend/privateMessage/notification with option : " + option);

        try {
            String response = clientMockService.callPrivateMessageNotifications(option);

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/client/backend/idissuers")
    public ResponseEntity<String> callIdIssuers(String option){
        log.info("Call client's /backend/idissuers with option : " + option);

        try {
            String response = clientMockService.callIdIssuers(option);

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
