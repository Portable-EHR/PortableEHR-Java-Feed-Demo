/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.controller;

import com.portableehr.service.ClientMockService;
import com.portableehr.service.ServerMockService;
import com.portableehr.service.State;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsoleController {

    private final ServerMockService serverMockService;

    private final ClientMockService clientMockService;

    private final State appState;

    public ConsoleController(ServerMockService serverMockService, ClientMockService clientMockService, State appState) {
        this.serverMockService = serverMockService;
        this.clientMockService = clientMockService;
        this.appState = appState;
    }

    @GetMapping("/")
    public String index(Model model){
        serverMockService.reloadOptions();
        clientMockService.reloadOptions();
        model.addAttribute("state", appState);
        return "console";
    }
}
