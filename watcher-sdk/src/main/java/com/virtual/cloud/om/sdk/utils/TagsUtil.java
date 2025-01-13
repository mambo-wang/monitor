package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.constant.Constant;

import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/5/24 10:53
 */
public final class TagsUtil {
    /**
     *
     * @param resouceId
     * @param type id的类型 例 hostids
     * @param id
     * @param strings
     * @return
     */

    public static String buildTags(String resouceId,String type,String id,String...strings){
        StringBuffer sb=new StringBuffer();
        if (Objects.nonNull(resouceId)){
            sb.append("resourceId=").append(resouceId);
            if (Objects.equals(Constant.Tags.HOST_ID,type)){
                sb.append(";").append(Constant.Tags.HOST_ID +"=").append(id);
            }else if (Objects.equals(Constant.Tags.CLUSTER_ID,type)){
                sb.append(";").append(Constant.Tags.CLUSTER_ID +"=").append(id);
            }else if (Objects.equals(Constant.Tags.DOMAIN_ID,type)){
                sb.append(";").append(Constant.Tags.DOMAIN_ID +"=").append(id);
            }
            if (strings.length>0){
                for (int i = 0; i < strings.length; i++) {
                    sb.append(";").append(strings[i]);
                }
            }
        }
        return sb.toString();
    }


}
