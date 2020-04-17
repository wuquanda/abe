package com.abe.controllers;

import com.abe.req.CTUpdateReq;
import com.abe.services.CAService;
import com.abe.services.CSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/CS")
public class CSController {
    @Autowired
    private CSService csService;

    @RequestMapping("/CTUpdate")
    public Boolean CTUpdate(@RequestBody CTUpdateReq req) throws IOException {
            return csService.CTUpdate(req);
    }

    @RequestMapping("/updateFailFilesByNo")
    public Boolean updateFailFilesByNo(Long n) throws UnsupportedEncodingException {
        return csService.updateFailFilesByNo(n);
    }
}
