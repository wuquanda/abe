package com.abe.req;

import lombok.Data;

import java.util.List;

@Data
public class RevokeAttrReq {
    List<String> attributes;
    List<String> users;
}
