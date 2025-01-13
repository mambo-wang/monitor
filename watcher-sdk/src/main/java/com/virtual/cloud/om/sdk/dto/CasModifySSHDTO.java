package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/9/7 14:37
 */
@Data
public class CasModifySSHDTO implements Serializable {

    private static final long serialVersionUID = 1712535911761668798L;
  private List<CasSSHResult> casSSHResults;
}
