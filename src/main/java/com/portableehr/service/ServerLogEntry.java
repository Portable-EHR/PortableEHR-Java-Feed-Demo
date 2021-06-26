/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.service;

import java.util.Calendar;
import java.util.Date;

public class ServerLogEntry {

    private Date timestamp;
    private String method;
    private String endpoint;
    private String option;

    public ServerLogEntry(String method, String endpoint, String option) {
        this.timestamp = Calendar.getInstance().getTime();
        this.method = method;
        this.endpoint = endpoint;
        this.option = option;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
