package com.virtual.cloud.om.sdk.config.token.onestor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by l19767 on 2019/12/10.
 */
@NoArgsConstructor
@Data
@ToString
public class OnestorTokenResult implements Serializable {
    private static final long serialVersionUID = 586805828652414127L;
    @JsonProperty("user_type")
    private String user_type;
    @JsonProperty("password_expired_warn")
    private boolean password_expired_warn;
    @JsonProperty("password_origin_warn")
    private boolean password_origin_warn;
    @JsonProperty("redirect_token")
    private String redirect_token;
}
