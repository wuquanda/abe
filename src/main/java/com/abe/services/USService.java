package com.abe.services;

import com.abe.config.PyConfig;
import com.abe.res.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class USService {

    private String GPP;
    private UserData userData;

    @Autowired
    private PyConfig config;
    @Autowired
    private PythonService pythonService;

    public String encrypt(){
        return "";
    }

}
