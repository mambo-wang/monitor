package com.virtual.cloud.om.sdk.config.rest.uis;

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
public class UisTokenResult implements Serializable {
    private static final long serialVersionUID = 586805828652414127L;
    /**
     * scope : all
     * access_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyYW5kb20iOiJIUUxERmJsSSIsInJvbGUiOiJcdTAwMDBcdTAwMDBcdTAwMDBcdTAwMDEiLCJpc3MiOiJIM0MgQXV0aCIsInVzZXJpZCI6MywidXNlcm5hbWUiOiJfX0ludGVybmFsX2FkbWluIn0.EY42tvPsXX1pxQgEfDfIztpXav843__dSiFfhxly9-o
     * refresh_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyYW5kb20iOiJHN3Q4MnhvWCIsInJvbGUiOiJcdTAwMDBcdTAwMDBcdTAwMDBcdTAwMDEiLCJpc3MiOiJIM0MgQXV0aCIsInVzZXJpZCI6MywidXNlcm5hbWUiOiJfX0ludGVybmFsX2FkbWluIn0.lhsf6fNf-ub7JRGaSjifofTnvEqWRkb2p-xaNarLoYo
     * expires_in : 7200
     * token_type : bearer
     */

    private String scope;

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value="refresh_token")
    private String refreshToken;

    @JsonProperty(value="expires_in")
    private int expiresIn;

    @JsonProperty(value="token_type")
    private String tokenType;
}
