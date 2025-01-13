package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor存储池基本信息DTO
 * */
@Data
@ApiModel(value = "存储池基本信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorPoolBasicDTO {
    private Long raw_total;

    private Status status;

    private String cache_tier_enable;

    private String replicate_num;

    private Long stripe_width;

    private boolean support_dedup_compress;

    private String data_writing_policy;

    private int num_of_pool_in_diskpool;

    private boolean dedup_switch;

    private Long compress_saved_capacity;

    private Long allocated;

    private Long total;

    private Long id;

    private Long size;

    private String redundancy;

    private String tbl_ver;

    private Long dedup_saved_capacity;

    private String application;

    private String fault_tolerant;

    private Long pg_num;

    private boolean all_flash;

    private Long compress_ratio;

    private String stor_strategy;

    private Long valid_used;

    /**
     * 存储池名称
     * */
    private String pool_name;

    private Long max_avail_actual;

    private String less_min_size_to_read;

    private boolean compress_switch;

    private String cache_diskpool_name;

    private Long min_size;

    private Long predicted_avail;

    private String read_hit_ratio;

    private String cache_available_capacity;

    private String cache_capacity;

    private String min_cache_capacity;

    private String diskpool_name;

    private String fs_name;

    private Long raw_used;

    private String total_capacity;

    private Long max_avail;

    /**
     * 所属节点池名
     * */
    private String nodepool_name;

    private String software_compress_strategy;

    private String cache_ratio;

    private String hardware_compress_strategy;

    private int data_health;

    private Boolean double_live_flag;
    private String double_live_name;

    @Data
    public static class Status {
        private String status;

        private String reason;

        private String reason_eng;
    }
    }


