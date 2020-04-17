package com.abe;

import com.abe.entity.UserKeys;
import com.abe.services.AAService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import java.io.*;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(MainApplication.class, args);
        /**

        String filesPath=System.getProperty("user.dir")+ File.separator+"/files";
        File file =new File(filesPath+File.separator+"wu.abe");
        File file2=new File(filesPath+File.separator+"wux.abe");
        FileInputStream fileInput =new FileInputStream(file);
        FileOutputStream fileOut=new FileOutputStream(file2);
        UserKeys userKeys=new UserKeys();
        userKeys.setAuthoritySecretKeys("au");
        userKeys.setId("wu");
        userKeys.setKeys("quan");
        String x= JSON.toJSONString(userKeys);
        BufferedInputStream in=new BufferedInputStream(fileInput);
        int y=1024;
        byte[] b=new byte[y];
        in.read(b);
        String l=new String(b,"UTF-8");
        UserKeys p=JSON.parseObject(l,UserKeys.class);
        BufferedOutputStream out=new BufferedOutputStream(fileOut);
        out.write(x.getBytes());
        out.flush();
        in.close();
        out.close();
         */

    }
}
