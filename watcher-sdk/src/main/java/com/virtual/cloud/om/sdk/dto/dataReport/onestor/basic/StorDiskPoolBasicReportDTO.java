package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Onestor硬盘池基本信息上报DTO
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorDiskPoolBasicReportDTO {

    private String nodepool_name;

    private String fs_id;

    /**
     * 硬盘池名称
     */
    private String diskpool_name;

    /**
     * 硬盘池用途，取值：rbd/rgw/rgw-data/rgw-metadata/cephfs/cephfs-data/cephfs-metadata
     */
    private String diskpool_service_type;

    /**
     * 硬盘池所属的节点池列表（返回值是数组，应该只有1个）多个的话逗号分隔
     */
    private List<String> nodepool_list;

    /**
     * 硬盘池状态：normal/abnormal/subhealth
     */
//    private String status;

    /**
     * 硬盘池总容量
     */
    private float total_capacity;

    /**
     * 故障域级别（host/rack）
     */
    private String safe_level;

    /**
     * 块存储卷特性（thick/thin）
     */
    private String lun_provision;

    /**
     * 是否为加密硬盘池（1-”on”/0-”off”）（可选参数）
     */
    private Integer encrypt_config;

    /**
     * 硬盘池内的数据盘类型：HDD-SATA/ HDD-SAS/HDD-MIX/SSD
     */
    private String data_disk_type;

    /**
     * 读缓存盘类型，None/SSD，”None”为字符串
     */
    private String flashcache_disk_type;

    /**
     * 读缓存大小：50-200
     */
    private int flashcache_size;

    /**
     * 写缓存盘类型，None/SSD，”None”为字符串
     */
    private String journal_disk_type;

    /**
     * 读缓存大小：30-200
     */
    private int journal_size;

    /**
     * 硬盘池的描述信息
     */
    private String description;

    /**
     * 数据盘个数
     */
    private int data_disk_num;

    /**
     * 硬盘池部署磁盘的总容量
     */
    private int deploy_capacity;

    /**
     * 硬盘池可用容量
     */
    private float available_capacity;

    /**
     * 硬盘池已用容量
     */
    private float used_capacity;

    /**
     * 数据健康情况
     */
    private int data_health;

    /**
     * 是否开启保护域
     */
    private Integer safe_domain_flag;

    /**
     * 保护域下故障域数目
     */
    private int safe_domain_num;

    /**
     * 块缓存盘数量，包含缓存盘和分离部署盘(正整数)
     */
    private int cache_disk_num;

    /**
     * 缓存盘的元数据保护级别(None/0/1)
     */
    private String cache_disk_raid_level;

    /**
     * 缓存盘的类型(None/SSD)
     */
    private String cache_disk_type;

    /**
     * 缓存盘的元数据缓存比（1-20）
     */
    private int cache_meta_disk_ratio;

    /**
     * 缓存盘的元数据盘类型(None/SSD)
     */
    private String cache_meta_disk_type;

    private String data_disk_raid_level;

    private int data_meta_disk_ratio;

    private String data_meta_disk_type;

    private String flashcache_mode;

    private String rotational_speed;

    private String scache_scheme;

    private int scache_size;

    private Integer scache_meta_part_ratio;

    private String scache_disk_type;

    private String scache_disk_raid_level;

    private Integer is_tiered_storage;

    private String tiered_info;
    private String recovery_policy;
    private Object status;
    private String status_reason;
    private String status_reason_eng;

    public void setEncrypt_config(String value) {
        Integer intValue = 0;
        if (value.equals("off")) {
            intValue = 0;
        } else if (value.equals("on")) {
            intValue = 1;
        }
        this.encrypt_config = intValue;
    }

    public void setIs_tiered_storage(Boolean value) {
        this.is_tiered_storage = value ? 1 : 0;
    }

    public void setSafe_domain_flag(String value) {
        Integer intValue = 0;
        if (value.equals("false")) {
            intValue = 0;
        } else if (value.equals("true")) {
            intValue = 1;
        }
        this.safe_domain_flag = intValue;
    }
}
