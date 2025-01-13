package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/23 22:15
 */
@Data
public class StorClusterMonitorDTO implements Serializable {
    private static final long serialVersionUID = -5840946597497913308L;
    /**
     *  {
     *           "fs_id": "ceph集群 fs ID",
     *           "mon_warn": 100,
     *           "mon_critical": 100,
     *           "mon_ok": 100
     */
    private  String fs_id;
    private  String mon_warn;
    private  String mon_critical;
    private  String mon_ok;
}
