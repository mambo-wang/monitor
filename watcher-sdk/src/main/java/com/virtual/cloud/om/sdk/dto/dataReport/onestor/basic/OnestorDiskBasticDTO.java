package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import lombok.Data;

/**
 * @author:XK
 * @Date:2022/12/12 16:23
 */
@Data
public class OnestorDiskBasticDTO {
//    "diskpool_name": "odisk",
    /**
     * "diskpool_name" -> "rgw1"
     * "status" -> "OK"
     * "physical_disk_size" -> {Double@15420} 2.147483648E11
     * "used_pct" -> {Double@15422} 0.523564
     * "encrypt_config" -> "off"
     * "used_bytes" -> {Double@15426} 1.123790848E9
     * "logical_disk_type" -> "data"
     * "logical_disk_size" -> {Double@15430} 2.147483648E11
     * "logical_disk_name" -> "sdb"
     * "id" -> {Integer@15434} 10
     */
//            "status": "OK",
//            "physical_disk_size": 107374182400.0,
//            "used_pct": 1.579542,
//            "encrypt_config": "off",
//            "used_bytes": 1694347264.0,
//            "logical_disk_type": "data",
//            "logical_disk_size": 107374182400.0,
//            "logical_disk_name": "sdd",
//            "id": 7
    /**
     * "used_pct" -> {Double@14344} 8.647865295410156
     * "interface_type" -> "None"
     * "logical_disk_type" -> "sys"
     * "physical_disk_size" -> {Long@14348} 322122547200
     * "tbl_ver" -> "1.0.1"
     * "raid_level" -> "None"
     * "sn" -> "hostone_sda"
     * "status" -> "OK"
     * "logical_disk_size" -> {Long@14356} 322122547200
     * "reserve1" -> "None"
     * "reserve2" -> "None"
     * "reserve3" -> "None"
     * "reserve4" -> "None"
     * "physical_disk_status" -> "online"
     * "disk_slot" -> {Integer@14368} 0
     * "diskpool_name" -> ""
     * "raid_slot" -> {Integer@14368} 0
     * "used_bytes" -> {Long@14371} 27856723968
     * "host_name" -> "hostone"
     * "logical_disk_name" -> "sda"
     * "physical_disk_wcache" -> "None"
     * "rotational_speed" -> "None"
     * "physical_disk_type" -> "None"
     */
    private String diskpool_name;
    private String status;
    private Double physical_disk_size;
    private Double used_pct;
    private String encrypt_config;
    private Long used_bytes;
    private String logical_disk_type;
    private Long logical_disk_size;
    private String logical_disk_name;
    private Double used_partition_num;
    private String host_name;
    private String interface_type;
    private String tbl_ver;
    private String raid_level;
    private String sn;
    private String reserve1;
    private String reserve2;
    private String reserve3;
    private String reserve4;
    private String physical_disk_status;
    private String disk_slot;
    private Integer raid_slot;
    private String physical_disk_wcache;
    private String rotational_speed;
    private String physical_disk_type;
    private Double total_partition_num;
    private Integer id;
}
