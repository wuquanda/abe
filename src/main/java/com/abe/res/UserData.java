package com.abe.res;

import com.abe.entity.UserKeys;
import lombok.Data;

import java.util.Map;

@Data
public class UserData {
    private UserKeys privateData;
    private String GPP;
    private Map<String,String> authorities;
    private String authorityId;
    private Map<String,String> publicData;
}
