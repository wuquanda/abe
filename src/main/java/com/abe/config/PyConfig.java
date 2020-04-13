package com.abe.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Data
public class PyConfig {
    private String file=System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"com"+File.separator+"abe"+File.separator+"services"+File.separator+"abeFuctions.py";
    private String cmdCAstup="python3 " + file + " --method=CAstup";
    private String cmdUserRegister="python3 " + file +" --method=userRegister";
    private String cmdAAstup="python3 " + file + " --method=AAstup";
    private String cmdKeyGen="python3 " + file + " --method=keyGen";
    private String cmdEncrypt="python3 " + file + " --method=encrypt";
    private String cmdDecrypt="python3 " + file + " --method=decrypt";
    private String cmdUKeyGen="python3 " + file + " --method=uKeyGen";
    private String cmdSKUpdate="python3 " + file + " --method=skUpdate";
    private String cmdCTUpdate="python3 " + file + " --method=ctUpdate";


    public String addParam(String method,String...param){
        String res=method;
        for(String p:param){
            res+=" "+p;
        }
        return res;
    }
}
