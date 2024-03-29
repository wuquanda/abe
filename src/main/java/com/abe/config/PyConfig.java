package com.abe.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

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
    private String cmdUKeyGen="python3 " + file + " --method=ukyeGen";
    private String cmdSKUpdate="python3 " + file + " --method=skUpdate";
    private String cmdCTUpdate="python3 " + file + " --method=ctUpdate";
    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();


    public String addParam(String method,String...param){
        String res=method;
        for(String p:param){
            res+=" "+p;
        }
        return res;
    }

    public String Object2Base64Str(Object obj) throws UnsupportedEncodingException {
        if(ObjectUtils.isEmpty(obj))
            return "";
        String jsonStr= JSON.toJSONString(obj);
        String b64Str=encoder.encodeToString(jsonStr.getBytes("UTF-8"));
        return b64Str;
    }
}
