package com.abe.controllers;

import com.abe.req.KeyGenReq;
import com.abe.res.UserData;
import com.abe.services.AAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/AA")
public class AAController {

    @Autowired
    private AAService aaService;

    @RequestMapping("/stup")
    public Boolean stup(String authorityId, String[] attributes) throws UnsupportedEncodingException {
        return aaService.stup(authorityId,attributes);
    }

    @RequestMapping("/defaultStup")
    public Boolean defaultStup() throws UnsupportedEncodingException {
        return aaService.defaultStup();
    }

    @RequestMapping("/keyGen")
    public UserData keyGen(@RequestBody KeyGenReq req) throws UnsupportedEncodingException {
        return aaService.keyGen(req.getUserData(),req.getAttributes());

    }



}
