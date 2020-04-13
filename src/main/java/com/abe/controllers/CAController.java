package com.abe.controllers;

import com.abe.res.UserData;
import com.abe.services.CAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/CA")
public class CAController {

    @Autowired
    private CAService caService;

    @RequestMapping("/register")
    public UserData register() throws UnsupportedEncodingException {
         return  caService.register();
    }

    @RequestMapping("/GPP")
    public String GPP(){
        return caService.getGPP();
    }
}
