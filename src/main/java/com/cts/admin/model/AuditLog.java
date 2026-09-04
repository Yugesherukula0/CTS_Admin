package com.cts.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long auditId;
    private Timestamp eventTime;
    private java.sql.Date eventDate;

    private Long userId;
    private String userName;

    private String module;
    private String action;

    private Long relatedBatchId;
    private Long relatedSessionId;

    public AuditLog() {
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public java.sql.Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(java.sql.Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getRelatedBatchId() {
        return relatedBatchId;
    }

    public void setRelatedBatchId(Long relatedBatchId) {
        this.relatedBatchId = relatedBatchId;
    }

    public Long getRelatedSessionId() {
        return relatedSessionId;
    }

    public void setRelatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
    }
}	