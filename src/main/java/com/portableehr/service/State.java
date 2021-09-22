/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class State {
    // Server
    private String serverLoginSelected;
    private List<String> serverLoginOptions = new ArrayList();
    private String serverPatientSingleSelected;
    private String serverPatientBundleSelected;
    private List<String> serverPatientOptions = new ArrayList();
    private String serverPatientPehrReachabilitySelected;
    private List<String> serverPatientPehrReachabilityOptions = new ArrayList();
    private String serverPractitionerSingleSelected;
    private String serverPractitionerBundleSelected;
    private List<String> serverPractitionerOptions = new ArrayList();
    private String serverPrivateMessageContentSelected;
    private List<String> serverPrivateMessageContentOptions = new ArrayList();
    private String serverPrivateMessageStatusSelected;
    private List<String> serverPrivateMessageStatusOptions = new ArrayList();
    private String serverAppointmentSingleSelected;
    private String serverAppointmentBundleSelected;
    private List<String> serverAppointmentOptions = new ArrayList();
    private String serverAppointmentDispositionSelected;
    private List<String> serverAppointmentDispositionsOptions = new ArrayList();
    private StringBuilder serverLogs = new StringBuilder();
    private int logEntryCount = 0;
    private int MAX_LOG_ENTRIES = 50;

    // Client
    private String clientJWTAuthToken;
    private List<String> clientLoginOptions = new ArrayList();
    private List<String> clientPatientReachabilityOptions = new ArrayList();
    private List<String> clientPrivateMessageNotificationOptions = new ArrayList();
    private List<String> clientIdIssuersOptions = new ArrayList();

    // Server
    public String getServerLoginSelected() {
        return serverLoginSelected;
    }

    public void setServerLoginSelected(String serverLoginSelected) {
        this.serverLoginSelected = serverLoginSelected;
    }

    public List<String> getServerLoginOptions() {
        return serverLoginOptions;
    }

    public void setServerLoginOptions(List<String> serverLoginOptions) {
        this.serverLoginOptions = serverLoginOptions;
    }

    public String getServerPatientSingleSelected() {
        return serverPatientSingleSelected;
    }

    public void setServerPatientSingleSelected(String serverPatientSingleSelected) {
        this.serverPatientSingleSelected = serverPatientSingleSelected;
    }

    public String getServerPatientBundleSelected() {
        return serverPatientBundleSelected;
    }

    public void setServerPatientBundleSelected(String serverPatientBundleSelected) {
        this.serverPatientBundleSelected = serverPatientBundleSelected;
    }

    public List<String> getServerPatientOptions() {
        return serverPatientOptions;
    }

    public void setServerPatientOptions(List<String> serverPatientOptions) {
        this.serverPatientOptions = serverPatientOptions;
    }

    public String getServerPatientPehrReachabilitySelected() {
        return serverPatientPehrReachabilitySelected;
    }

    public void setServerPatientPehrReachabilitySelected(String serverPatientPehrReachabilitySelected) {
        this.serverPatientPehrReachabilitySelected = serverPatientPehrReachabilitySelected;
    }

    public List<String> getServerPatientPehrReachabilityOptions() {
        return serverPatientPehrReachabilityOptions;
    }

    public void setServerPatientPehrReachabilityOptions(List<String> serverPatientPehrReachabilityOptions) {
        this.serverPatientPehrReachabilityOptions = serverPatientPehrReachabilityOptions;
    }

    public String getServerPractitionerSingleSelected() {
        return serverPractitionerSingleSelected;
    }

    public void setServerPractitionerSingleSelected(String serverPractitionerSingleSelected) {
        this.serverPractitionerSingleSelected = serverPractitionerSingleSelected;
    }

    public String getServerPractitionerBundleSelected() {
        return serverPractitionerBundleSelected;
    }

    public void setServerPractitionerBundleSelected(String serverPractitionerBundleSelected) {
        this.serverPractitionerBundleSelected = serverPractitionerBundleSelected;
    }

    public List<String> getServerPractitionerOptions() {
        return serverPractitionerOptions;
    }

    public void setServerPractitionerOptions(List<String> serverPractitionerOptions) {
        this.serverPractitionerOptions = serverPractitionerOptions;
    }

    public String getServerPrivateMessageContentSelected() {
        return serverPrivateMessageContentSelected;
    }

    public void setServerPrivateMessageContentSelected(String serverPrivateMessageContentSelected) {
        this.serverPrivateMessageContentSelected = serverPrivateMessageContentSelected;
    }

    public List<String> getServerPrivateMessageContentOptions() {
        return serverPrivateMessageContentOptions;
    }

    public void setServerPrivateMessageContentOptions(List<String> serverPrivateMessageContentOptions) {
        this.serverPrivateMessageContentOptions = serverPrivateMessageContentOptions;
    }

    public String getServerPrivateMessageStatusSelected() {
        return serverPrivateMessageStatusSelected;
    }

    public void setServerPrivateMessageStatusSelected(String serverPrivateMessageStatusSelected) {
        this.serverPrivateMessageStatusSelected = serverPrivateMessageStatusSelected;
    }

    public List<String> getServerPrivateMessageStatusOptions() {
        return serverPrivateMessageStatusOptions;
    }

    public void setServerPrivateMessageStatusOptions(List<String> serverPrivateMessageStatusOptions) {
        this.serverPrivateMessageStatusOptions = serverPrivateMessageStatusOptions;
    }

    public String getServerAppointmentSingleSelected() {
        return serverAppointmentSingleSelected;
    }

    public void setServerAppointmentSingleSelected(String serverAppointmentSingleSelected) {
        this.serverAppointmentSingleSelected = serverAppointmentSingleSelected;
    }

    public String getServerAppointmentBundleSelected() {
        return serverAppointmentBundleSelected;
    }

    public void setServerAppointmentBundleSelected(String serverAppointmentBundleSelected) {
        this.serverAppointmentBundleSelected = serverAppointmentBundleSelected;
    }

    public List<String> getServerAppointmentOptions() {
        return serverAppointmentOptions;
    }

    public void setServerAppointmentOptions(List<String> serverAppointmentOptions) {
        this.serverAppointmentOptions = serverAppointmentOptions;
    }

    public String getServerAppointmentDispositionSelected() {
        return serverAppointmentDispositionSelected;
    }

    public void setServerAppointmentDispositionSelected(String serverAppointmentDispositionSelected) {
        this.serverAppointmentDispositionSelected = serverAppointmentDispositionSelected;
    }

    public List<String> getServerAppointmentDispositionsOptions() {
        return serverAppointmentDispositionsOptions;
    }

    public void setServerAppointmentDispositionsOptions(List<String> serverAppointmentDispositionsOptions) {
        this.serverAppointmentDispositionsOptions = serverAppointmentDispositionsOptions;
    }

    public String getServerLogs() {
        return serverLogs.toString();
    }

    public synchronized void addLogEntry(ServerLogEntry logEntry) {
        this.serverLogs.insert(0, String.format("[%-23s] %-4s %-40s -> %s%s",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(logEntry.getTimestamp()),
                logEntry.getMethod(),
                logEntry.getEndpoint(),
                logEntry.getOption(),
                System.lineSeparator()));

        if(logEntryCount < MAX_LOG_ENTRIES) logEntryCount++;
        if(logEntryCount >= MAX_LOG_ENTRIES) {
            this.serverLogs.delete(serverLogs.length()-System.lineSeparator().length(), serverLogs.length());
            this.serverLogs.delete(serverLogs.lastIndexOf(System.lineSeparator())+System.lineSeparator().length(), serverLogs.length());
        }
    }

    // Client
    public String getClientJWTAuthToken() {
        return clientJWTAuthToken;
    }

    public void setClientJWTAuthToken(String clientJWTAuthToken) {
        this.clientJWTAuthToken = clientJWTAuthToken;
    }

    public List<String> getClientLoginOptions() {
        return clientLoginOptions;
    }

    public void setClientLoginOptions(List<String> clientLoginOptions) {
        this.clientLoginOptions = clientLoginOptions;
    }

    public List<String> getClientPatientReachabilityOptions() {
        return clientPatientReachabilityOptions;
    }

    public void setClientPatientReachabilityOptions(List<String> clientPatientReachabilityOptions) {
        this.clientPatientReachabilityOptions = clientPatientReachabilityOptions;
    }

    public List<String> getClientPrivateMessageNotificationOptions() {
        return clientPrivateMessageNotificationOptions;
    }

    public void setClientPrivateMessageNotificationOptions(List<String> clientPrivateMessageNotificationOptions) {
        this.clientPrivateMessageNotificationOptions = clientPrivateMessageNotificationOptions;
    }

    public List<String> getClientIdIssuersOptions() {
        return clientIdIssuersOptions;
    }

    public void setClientIdIssuersOptions(List<String> clientIdIssuersOptions) {
        this.clientIdIssuersOptions = clientIdIssuersOptions;
    }
}
