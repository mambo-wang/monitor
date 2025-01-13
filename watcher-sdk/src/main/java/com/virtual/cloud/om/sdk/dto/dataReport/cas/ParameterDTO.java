package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/5/21 14:46
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterDTO implements Serializable {

    private static final long serialVersionUID = 2809103931824195368L;

    private List<KeyValue> keyValueList;
}
