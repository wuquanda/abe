package com.abe.services;

import com.abe.config.PyConfig;
import com.abe.entity.UserKeys;
import com.abe.req.CTUpdateReq;
import com.abe.req.RevokeAttrReq;
import com.abe.res.UserData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private List<Map<String,String>> UKcVersion = new CopyOnWriteArrayList<>();
    private Map<String,List<Map<String,String>>> UKsVersion =new ConcurrentHashMap<>();
    private Map<String,Set<String>> userAttrMap=new HashMap<>();

    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    private PythonService pyService;

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
        Set<String> attrSet=new HashSet<>(attributes);
        this.userAttrMap.put(userData.getPrivateData().getId(),attrSet);
        String b64GPP=encoder.encodeToString(this.GPP.getBytes("UTF-8"));
        String userId=userData.getPrivateData().getId();
        String b64Authorities=Object2Base64Str(this.authorities);
        String b64Attributes=Object2Base64Str(attributes.toArray());
        String b64privateData=Object2Base64Str(userData.getPrivateData());
        String b64PublicData=Object2Base64Str(userData.getPublicData());
        String cmd=pyConfig.addParam(this.cmdKeyGen,b64GPP,b64Authorities,authorityId,b64Attributes,b64privateData,b64PublicData);
        String execRes=pyService.exec(cmd);
        JSONObject resObj=JSON.parseObject(execRes);
        //UserData u=JSON.parseObject(execRes,UserData.class)   why it doesn't work
        userData.setPrivateData(JSON.parseObject(resObj.getString("privateData"),UserKeys.class));
        Map<String,String> publicData=JSON.parseObject(resObj.getString("publicData"),new TypeReference<Map<String,String> >(){});
        userData.setPublicData(publicData);
        this.publicDataMap.put(userId,publicData.get(userId));
        userData.setAuthorityId(this.authorityId);
        userData.setAuthorities(this.authorities);
        return userData;
    }

    public Boolean uKeyGen(List<String> revokedAttrs, List<String> revokedUsers) throws UnsupportedEncodingException {
        Map<String,Map<String,String>> revokeMap=getRevokedMap(revokedAttrs,revokedUsers);
        String b64GPP = encoder.encodeToString(this.GPP.getBytes("UTF-8"));
        String b64RevokedMap=Object2Base64Str(revokeMap);
        String b64authorities=Object2Base64Str(this.authorities);
        String b64UKsVersion=Object2Base64Str(this.UKsVersion);
        String b64UKcVersion=Object2Base64Str(this.UKcVersion);
        String cmd=pyConfig.addParam(pyConfig.getCmdUKeyGen(),b64GPP,b64authorities,this.authorityId,b64RevokedMap,b64UKsVersion,b64UKcVersion);
        String execRes=pyService.exec(cmd);
        JSONObject execResObj=JSON.parseObject(execRes);
        String UKs=execResObj.getString("UKsVersion");
        String UKc=execResObj.getString("UKcVersion");
        String authorities=execResObj.getString("authorities");
        this.UKcVersion=JSON.parseObject(UKc, new TypeReference<List<Map<String,String>>>(){});
        this.UKsVersion=JSON.parseObject(UKs, new TypeReference<Map<String,List<Map<String,String>>> >(){});
        this.authorities=JSON.parseObject(authorities,new TypeReference<HashMap<String,String>>(){});
        return true;
    }

    public Boolean isLatestVersion(String userId){
        if(!CollectionUtils.isEmpty(this.UKsVersion)&&this.UKsVersion.containsKey(userId)){
            return true;
        }
        return false;
    }

    public UserData skUpdate(UserData userData) throws UnsupportedEncodingException {
        String userId=userData.getPrivateData().getId();
        String b64UserKeys=Object2Base64Str(userData.getPrivateData());
        String b64UKsVersion=Object2Base64Str(UKsVersion.get(userId));
        String cmd=pyConfig.addParam(pyConfig.getCmdSKUpdate(),b64UserKeys, b64UKsVersion);
        String execRes=pyService.exec(cmd);
        UserKeys res=JSON.parseObject(execRes,UserKeys.class);
        userData.setPrivateData(res);
        this.UKsVersion.remove(userId);
        return userData;
    }

    public Boolean revokeAttributes(RevokeAttrReq req) throws UnsupportedEncodingException {
        Boolean ukRes=uKeyGen(req.getAttributes(),req.getUsers());
        RestTemplate restTemplate = new RestTemplate();
        String uri="http://localhost:8090/CS/CTUpdate";
        CTUpdateReq ctReq=new CTUpdateReq();
        ctReq.setUKcVersion(this.UKcVersion);
        Boolean  ctRes= restTemplate.postForObject(uri, ctReq, Boolean.class);
        return ukRes & ctRes;

    }



    public String Object2Base64Str(Object obj) throws UnsupportedEncodingException {
        if(ObjectUtils.isEmpty(obj))
            return "";
        String jsonStr=JSON.toJSONString(obj);
        String b64Str=encoder.encodeToString(jsonStr.getBytes("UTF-8"));
        return b64Str;
    }


    public Map<String,Map<String,String>> getRevokedMap(List<String> revokedAttrs, List<String> revokedUsers){
        synchronized (this.userAttrMap){
            Map<String,Map<String,String>> res=new HashMap<>();
            for(String attr: revokedAttrs){
                res.put(attr,new HashMap<>());
            }
            for(Map.Entry<String, Set<String>> entry : this.userAttrMap.entrySet()){
                String userId = entry.getKey();
                if(!revokedUsers.contains(userId)){
                    Set<String> set=entry.getValue();
                    for(String attr:set){
                        if(revokedAttrs.contains(attr)){
                            res.get(attr).put(userId,this.publicDataMap.get(userId));
                        }
                    }
                }
            }
            return res;
        }
    }



}
