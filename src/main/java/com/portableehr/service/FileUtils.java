/*
 * Copyright © Portable EHR inc, 2021
 * https://portableehr.com/
 */

package com.portableehr.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

@Service
public class FileUtils {

    @Value("${mocks.basePath}")
    private String MOCKS_BASE_PATH;

    public String getOptionContent(String directory, String option) throws FileNotFoundException {
        File optionFile = new File(MOCKS_BASE_PATH + directory, option);
        StringBuilder fileContents = new StringBuilder((int)optionFile.length());

        try (Scanner scanner = new Scanner(optionFile)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine());
                if(scanner.hasNextLine()){
                    fileContents.append(System.lineSeparator());
                }
            }
        }

        return fileContents.toString();
    }

    public String[] listFiles(String directory) {
        return new File(MOCKS_BASE_PATH + directory).list();
    }

}
