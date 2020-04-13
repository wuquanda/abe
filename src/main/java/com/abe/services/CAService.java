package com.abe.services;

import com.abe.config.PyConfig;
import com.abe.entity.UserKeys;
import com.abe.res.UserData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@Data
public class CAService {

    private String GPP;
    private String GMK;
    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();
    private String cmdStup;
    private String cmdRegister;

    @Autowired
    private pythonService pyService;

    @Autowired
    private PyConfig config;

    CAService(PyConfig config){
        this.cmdStup=config.getCmdCAstup();
        this.cmdRegister=config.getCmdUserRegister();
    }

    public UserData register() throws UnsupportedEncodingException {
        String id= UUID.randomUUID().toString().replaceAll("-","");
        String b64GPP = encoder.encodeToString(getGPP().getBytes("UTF-8"));
        String cmd=config.addParam(this.cmdRegister,b64GPP,id);

        String execRes= pyService.exec(cmd);
        JSONObject execResObj=JSON.parseObject(execRes);
        UserKeys userKeys=JSON.parseObject(execResObj.getString("privateData"),UserKeys.class);
        Map<String,String> publicData=JSON.parseObject(execResObj.getString("publicData"),new TypeReference<Map<String,String>>(){});
        UserData res=new UserData();
        res.setPrivateData(userKeys);
        res.setPublicData(publicData);
        return res;
    }

    public String getGPP(){
        if(StringUtils.isEmpty(this.GPP)){
            String res = pyService.exec(cmdStup);
            JSONObject resObj = JSON.parseObject(res);
            this.GPP = resObj.getString("GPP");
            this.GMK = resObj.getString("GMK");
        }
        return this.GPP;
    }



}
