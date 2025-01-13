package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Onestor硬盘池基本信息DTO
 * */
@Data
@ApiModel(value = "硬盘池基本信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorDiskPoolBasicDTO {
    private int safe_domain_num;

    private String all_flash_disk_raid_level;

    private int wal_part_size;

    private String safe_level;

    private String scache_disk_raid_level;

    private String encrypt_config;

    private int flashcache_size;

    private int lun_total_size;

    private String cache_disk_type;

    private String flashcache_disk_type;

    private String flashcache_mode;

    private int cache_disk_num;

    private boolean all_flash;

    private int scache_part_size;

    private String scache_disk_type;

    private int scache_meta_part_ratio;

    private int all_flash_row_size;

    private double used_capacity;

    private String diskpool_service_type;

    private String stor_strategy;

    private Status status;

    private boolean support_dedup_compress;

    private String description;

    private double available_capacity;

    private int data_meta_disk_ratio;

    private int cache_meta_disk_ratio;

    private String lun_provision;

    private int all_flash_disk_ratio;

    private String cache_meta_disk_type;

    private String scache_scheme;

    private String recovery_policy;

    private List<String> hosts_list;

    private int journal_size;

    private String diskpool_name;

    private String data_disk_raid_level;

    private String safe_domain_flag;

    private String all_flash_disk_type;

    private double total_capacity;

    private String journal_disk_type;

    private Map<String, Boolean> nodepool_dict;

    private String data_disk_type;

    private String data_meta_disk_type;

    private int scache_size;

    private String cache_disk_raid_level;

    private int db_part_size;

    private int data_disk_num;

    private String rotational_speed;

    private int data_health;

    private int deploy_capacity;

    private boolean is_tiered_storage;

    private String tiered_info;

    @Data
    public static class Status {
        private String status;

        private String reason;

        private String reason_eng;
    }


}
