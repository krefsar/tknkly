package io.anglehack.eso.tknkly.models;

/**
 * Created by root on 6/18/17.
 */
public class SatoriConfig {

    private String endpoint;
    private String appkey;
    private String role;
    private String roleSecretKey;
    private String channel;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleSecretKey() {
        return roleSecretKey;
    }

    public void setRoleSecretKey(String roleSecretKey) {
        this.roleSecretKey = roleSecretKey;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
