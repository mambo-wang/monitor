package com.virtual.cloud.om.uis.dto;

import lombok.Data;

@Data
public class UisRsTaskMsg {
    private String address;
    private String detail;
    private String id;
    private Integer result;
    private String name;
    private Integer progress;
    private String complete;
    private String reqStart;
    private String start;
    private String target;
    private String user;
}
