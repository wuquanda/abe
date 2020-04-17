package com.abe.services;

import com.abe.config.PyConfig;
import com.abe.entity.UpdateRecord;
import com.abe.req.CTUpdateReq;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class CSService {

    @Autowired
    private PyConfig config;
    @Autowired
    private PythonService pythonService;
    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();
    final String CAwebsite="http://localhost:8090/CA/GPP";
    final String dataPath=System.getProperty("user.dir")+File.separator+"files"+File.separator;
    Map<Long,UpdateRecord> records=new HashMap<>();
    private String GPP;


    public void updateCTFiles( List<Map<String,String>> UKcVersion,List<String> files) throws UnsupportedEncodingException {
        String b64GPP=encoder.encodeToString(getGPP().getBytes("UTF-8"));
        String b64UKcVersion=Object2Base64Str(UKcVersion);
        List<String> updateFailed=new ArrayList<>();
        for(String filePath: files){
            String cmd=config.getCmdCTUpdate()+" "+b64GPP+" "+filePath+" "+b64UKcVersion;
            String execRes=pythonService.exec(cmd);
            if(!"success".equals(execRes)){
                updateFailed.add(filePath);
            }
        }
        if(!CollectionUtils.isEmpty(updateFailed)){
            UpdateRecord record=new UpdateRecord();
            record.setFiles(updateFailed);
            record.setUKcVersion(UKcVersion);
            Long n=System.currentTimeMillis();
            records.put(n,record);
            try {
                File Info=new File(dataPath+File.separator+"FailedNo.txt");
                if(!Info.exists()){
                    Info.createNewFile();
                }
                FileWriter fileWriter=new FileWriter(Info);
                fileWriter.write(Math.toIntExact(n));
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean CTUpdate(CTUpdateReq req) throws IOException {
        List<String> files=new ArrayList<>();
        getAllFileName(dataPath,files);
        updateCTFiles(req.getUKcVersion(),files);
        return true;
    }

    public Boolean updateFailFilesByNo(Long n) throws UnsupportedEncodingException {
        UpdateRecord record=this.records.get(n);
        updateCTFiles(record.getUKcVersion(),record.getFiles());
        records.remove(n);
        return true;
    }

    public String getGPP(){
        if(StringUtils.isEmpty(this.GPP)){
            RestTemplate restTemplate = new RestTemplate();
            String GPP = restTemplate.getForObject(this.CAwebsite,String.class);
            this.GPP=GPP;
        }
        return this.GPP;
    }

    public static void getAllFileName(String path, List<String> listFileName){
        File file=new File(path);
        File[] files=file.listFiles();
        String[] names=file.list();
        if(names!=null){
            String[] completNames=new String[names.length];
            for (int i=0;i<names.length;i++){
                completNames[i]=path+names[i];
            }
            listFileName.addAll(Arrays.asList(completNames));
        }
        for(File a:files){
            if(a.isDirectory()){
                getAllFileName(a.getAbsolutePath()+File.separator,listFileName);
            }
        }
    }
    public String Object2Base64Str(Object obj) throws UnsupportedEncodingException {
        if(ObjectUtils.isEmpty(obj))
            return "";
        String jsonStr= JSON.toJSONString(obj);
        String b64Str=encoder.encodeToString(jsonStr.getBytes("UTF-8"));
        return b64Str;
    }


}
