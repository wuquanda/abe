package com.abe.req;

import com.abe.res.UserData;
import lombok.Data;

import java.util.List;

@Data
public class KeyGenReq {
    private UserData userData;
    private List<String> attributes;
}
