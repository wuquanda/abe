package com.abe.services;

import com.abe.config.PyConfig;
import com.abe.entity.UserKeys;
import com.abe.res.UserData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AAService {
    private String GPP;
    private String authorityId;
    private String[] attributes;
    private ConcurrentHashMap<String,String> publicDataMap=new ConcurrentHashMap<>();
    private HashMap<String,String> authorities=new HashMap<>();
    private String CAwebsite="http://localhost:8090/CA";
    private String cmdStup;
    private String cmdKeyGen;

    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    private pythonService pyService;

    @Autowired
    private PyConfig pyConfig;

    AAService(PyConfig pyConfig){
        this.cmdStup=pyConfig.getCmdAAstup();
        this.cmdKeyGen=pyConfig.getCmdKeyGen();
        this.authorityId="AA1";
        this.attributes=new String[]{"A","B","C","D"};
    }

    public Boolean stup(String authorityId, String[] attributes) throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        String GPP = restTemplate.getForObject(CAwebsite+"/GPP",String.class);
        this.GPP=GPP;
        String b64GPP = encoder.encodeToString(GPP.getBytes("UTF-8"));
        String b64Auttributes = Object2Base64Str(attributes);
        String cmd =pyConfig.addParam(this.cmdStup,b64GPP,authorityId,b64Auttributes);
        String execRes=pyService.exec(cmd);
        JSONObject execResObj=JSON.parseObject(execRes);
        authorities.put(authorityId,execResObj.getString(authorityId));
        return true;
    }

    public Boolean defaultStup() throws UnsupportedEncodingException {
        return stup(this.authorityId,this.attributes);
    }


    public UserData keyGen(UserData userData, List<String> attributes) throws UnsupportedEncodingException {
        String b64GPP=encoder.encodeToString(this.GPP.getBytes("UTF-8"));
        String b64Authorities=Object2Base64Str(this.authorities);
        String b64Attributes=Object2Base64Str(attributes.toArray());
        String b64privateData=Object2Base64Str(userData.getPrivateData());
        String b64PublicData=Object2Base64Str(userData.getPublicData());
        String cmd=pyConfig.addParam(this.cmdKeyGen,b64GPP,b64Authorities,authorityId,b64Attributes,b64privateData,b64PublicData);
        String execRes=pyService.exec(cmd);
        UserData updateUser=JSON.parseObject(execRes,UserData.class);
        return updateUser;
    }


    public String Object2Base64Str(Object obj) throws UnsupportedEncodingException {
        if(ObjectUtils.isEmpty(obj))
            return "";
        String jsonStr=JSON.toJSONString(obj);
        String b64Str=encoder.encodeToString(jsonStr.getBytes("UTF-8"));
        return b64Str;
    }






}
