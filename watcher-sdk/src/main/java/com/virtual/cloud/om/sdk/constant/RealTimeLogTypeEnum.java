package com.virtual.cloud.om.sdk.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 日志类型以及路径
 * @Author: w22798
 * @Date: 2022/5/21 16:27
 */
@Getter
public enum RealTimeLogTypeEnum {

    onestor_ceph,
    onestor_message,
    onestor_storage,
    onsestor_calamari,
    workspace_server,
    workspace_controller,
    workspace_grpc,
    workspace_grpc_client,
    cas_server,
    uis_server,
    cas_ovsshell,
    cas_nactl,
    cas_vswitchd,
    cas_viragent,
    cas_message,
    cas_fsmcore,
    cas_libvirt,
    cas_catalina,
    cas_casserver,
    cas_qemu,
    default_,
    others_;

    public static boolean exist(String name) {
        Set<String> names = Arrays.stream(RealTimeLogTypeEnum.values()).map(Enum::name).collect(Collectors.toSet());
        return names.contains(name);
    }
}
