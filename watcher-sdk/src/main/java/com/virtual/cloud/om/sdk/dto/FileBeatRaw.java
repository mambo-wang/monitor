package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

/**
 * @Author: w22798
 * @Date: 2022/5/4 12:25
 */
@Data
public class FileBeatRaw {

    private FileBeatRawLog log;

    private String message;

    private FileBeatRawHost host;

    private FileBeatRawFields fields;
}
