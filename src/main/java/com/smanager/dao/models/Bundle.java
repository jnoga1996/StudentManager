package com.smanager.dao.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "BUNDLES")
public class Bundle {
    @Column(name = "bundle_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String bundle;

    @NotNull
    private String keyValue;

    @NotNull
    private String message;

    @NotNull
    private String lang;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Bundle() {}

    public Bundle(String lang, String bundle, String keyValue) {
        this.lang = lang;
        this.bundle = bundle;
        this.keyValue = keyValue;
    }

    public Bundle(String lang, String bundle, String keyValue, String message) {
        this(lang, bundle, keyValue);
        this.message = message;
    }
}
