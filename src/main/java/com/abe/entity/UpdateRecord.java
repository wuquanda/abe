package com.abe.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class UpdateRecord {
    List<String> files;
    List<Map<String,String>> UKcVersion;
}
