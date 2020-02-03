package com.oracle.meetup.repository.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@Table(name = "MEETUP_HANDSON_ENV")
public class HandsOnEnv implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3423961242822117508L;

    @Column(name = "NUM", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "MEETUP_HANDSON_ENV_SEQ")
    @SequenceGenerator(sequenceName = "MEETUP_HANDSON_ENV_SEQ", allocationSize = 1, name = "MEETUP_HANDSON_ENV_SEQ")
    @Id
    private int num;

    @Column(name = "OS_USER")
    private String osuser;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TENANT")
    private String tenant;

    @Column(name = "REGION")
    private String region;

    @Column(name = "CLOUD_ACCOUNT")
    private String cloud_account;

    @Column(name = "CLOUD_ACCOUNT_PW")
    private String cloud_account_pw;

    @Column(name = "HANDSON_DOC_URL")
    private String handson_doc_url;

    @Column(name = "KEY_DOWNLOAD_URL")
    private String key_download_url;

    @Column(name = "ENV_IP")
    private String env_ip;

    @Column(name = "ENV_NAME")
    private String env_name;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getOsuser() {
        return osuser;
    }

    public void setOsuser(String osuser) {
        this.osuser = osuser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCloud_account() {
        return cloud_account;
    }

    public void setCloud_account(String cloud_account) {
        this.cloud_account = cloud_account;
    }

    public String getCloud_account_pw() {
        return cloud_account_pw;
    }

    public void setCloud_account_pw(String cloud_account_pw) {
        this.cloud_account_pw = cloud_account_pw;
    }

    public String getHandson_doc_url() {
        return handson_doc_url;
    }

    public void setHandson_doc_url(String handson_doc_url) {
        this.handson_doc_url = handson_doc_url;
    }

    public String getKey_download_url() {
        return key_download_url;
    }

    public void setKey_download_url(String key_download_url) {
        this.key_download_url = key_download_url;
    }

    public String getEnv_ip() {
        return env_ip;
    }

    public void setEnv_ip(String env_ip) {
        this.env_ip = env_ip;
    }

    public String getEnv_name() {
        return env_name;
    }

    public void setEnv_name(String env_name) {
        this.env_name = env_name;
    }
}