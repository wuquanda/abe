package com.abe.entity;

import lombok.Data;

@Data
public class UserKeys {
    private String id;
    private String authoritySecretKeys;
    private String keys;
    private String contentKey;
}
