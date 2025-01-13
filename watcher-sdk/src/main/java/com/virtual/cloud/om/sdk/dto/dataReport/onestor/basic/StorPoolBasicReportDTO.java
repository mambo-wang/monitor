package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor存储池基本信息上报DTO
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorPoolBasicReportDTO {
    private String fs_id;

    private String nodepool_name;

    private Long pool_id;

    private String pool_name;

    private String diskpool_name;

    private String fs_name;

    private String redundancy;

    private String application;

    private String stripe_width;

    private int data_health;

    /**
     * 存储池状态，”1-normal”/“0-abnormal”
     */
    private Integer status;

    private int pg_num;

    private String replicate_num;

    private int size;

    private Long allocated;

    private Long total;

    private String max_avail;

    private String max_avail_actual;

    private String valid_used;

    private String raw_used;

    private Long raw_total;

    private String min_cache_capacity;

    private Long predicted_avail;

    /**
     * 是否开启cachetier
     */
    private Integer cache_tier_enable;

    private String cache_diskpool_name;

    private String cache_available_capacity;

    private String cache_capacity;

    private String cache_ratio;

    private String read_hit_ratio;

    private String data_writing_policy;

    private int num_of_pool_in_diskpool;

    private String fault_tolerant;

    private String stor_strategy;

    private String less_min_size_to_read;

    private int min_size;

    private String update_time;

    private  String double_live_name;

    private  String recovery_policy;

    private  String background_connect_type;


    public void setStatusValue(String value) {
        int intValue = 0;
        if(value.equals("normal")){
            intValue = 1;
        }else if(value.equals("abnormal")){
            intValue = 0;
        }
        this.status = intValue;
    }

    public void setCache_tier_enable(String value) {
        int intValue = 0;
        if(value.equals("true")){
            intValue = 1;
        }else if(value.equals("false")){
            intValue = 0;
        }
        this.cache_tier_enable = intValue;
    }
}


