package com.virtual.cloud.om.sdk.constant.onestor;

import lombok.AllArgsConstructor;

import java.util.*;

/**
 * Onestor 接收用 主机类别
 * OneStor接口返回的role值是所查询主机的所有角色的和，如role为6则说明角色为ROLE_STOR、ROLE_MON；
 * 可以使用16进制使role和角色对应的值进行 与 运算。结果为0则说明不含有此角色，不为0则说明含有此角色。
 * */
@AllArgsConstructor
public enum OneStorRoleEnum {
    ROLE_MANAGER(1,0x1),
    ROLE_STOR(2, 0x2),
    ROLE_MON(4, 0x4),
    ROLE_TGT(8, 0x8),
    ROLE_RGW(16, 0x10),
    ROLE_MDS(32, 0x20),
    ROLE_NAS(64, 0x40),
    ;
    public final Integer value;
    public final Integer valueHex;

    public static Set<OneStorRoleEnum> getRoleList(Integer value){
        List<OneStorRoleEnum> roleList = new ArrayList<>();
        List<OneStorRoleEnum> roleEnumList = Arrays.asList(OneStorRoleEnum.values());
        roleEnumList.forEach(currentEunm -> {
            if((value & currentEunm.valueHex) != 0){
                roleList.add(currentEunm);
            }
        });
        return new HashSet<>(roleList);
    }
    }
