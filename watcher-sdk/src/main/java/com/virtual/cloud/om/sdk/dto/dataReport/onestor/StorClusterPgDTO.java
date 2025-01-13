package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/8/24 16:10
 */
@Data
public class StorClusterPgDTO implements Serializable {
    private static final long serialVersionUID = -5540812969448732880L;
    /**
     *   "fs_id": "ceph集群 fs ID",
     *           "pg_warn": 100,
     *           "pg_critical": 100,
     *           "pg_ok": 100,
     *           "pg_num": 100
     */

    private  String fs_id;
    private  String pg_warn;
    private  String pg_critical;
    private  String pg_ok;
    private  Integer pg_num;

}
