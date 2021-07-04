package org.vuffy.o2o.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAccessToken {

    // @JsonProperty 赋值给实体类

    // 凭证
    @JsonProperty("access_token")
    private String accessToken;
    // 凭证有效时间
    @JsonProperty("expires_in")
    private String expiresIn;
    // 更新令牌，获取下一次的访问令牌
    @JsonProperty("refresh_token")
    private String refreshToken;
    // 用户在此公众号下的身份标识，具有唯一性
    @JsonProperty("open_id")
    private String openId;
    // 权限范围
    @JsonProperty("scope")
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
