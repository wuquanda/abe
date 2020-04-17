package com.abe.services;


import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.Base64;

@Service
public class PythonService {

    public static final Base64.Encoder encoder = Base64.getEncoder();

    public  String exec(String command){
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(command);

            // 标准输入流（必须写在 waitFor 之前）
            String inStr = consumeInputStream(process.getInputStream());
            // 标准错误流（必须写在 waitFor 之前）
            String errStr = consumeInputStream(process.getErrorStream());

            int retCode = process.waitFor();
            if(retCode == 0){
                System.out.println("程序正常执行结束");
            }else {
                System.out.println("错误信息：\n"+errStr);
            }
            result =inStr;
        } catch (IOException | InterruptedException e) {
            System.out.print("调用python脚本命令错误:"+command);
        }
        return result;
    }


    /**
     *   消费inputstream，并返回
     */
    private String consumeInputStream(InputStream is) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(is));
        String s ;
        StringBuilder res = new StringBuilder();
        while((s=input.readLine())!=null){
            res.append(s);
        }
        input.close();
        return res.toString();
    }

    public String Object2Base64Str(Object obj) throws UnsupportedEncodingException {
        if(ObjectUtils.isEmpty(obj))
            return "";
        String jsonStr= JSON.toJSONString(obj);
        String b64Str=encoder.encodeToString(jsonStr.getBytes("UTF-8"));
        return b64Str;
    }


}
