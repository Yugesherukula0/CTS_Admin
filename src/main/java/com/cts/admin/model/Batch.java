package com.cts.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Batch implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Common fields
     */
    private Long   batchId;
    private String batchType;      /* BATCH_CAPTURE | INWARD | OUTWARD */
    private int    totalCheques;
    private String status;

    /*
     * Batch Capture specific
     */
    private String sentUser;       /* username of user who sent the batch */

    /*
     * Inward specific
     */
    private String maker;
    private String checker;

    /*
     * Audit
     */
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /* ------------------------------------------------------------------ */

    public Batch() {}

    /* ------------------------------------------------------------------ */

    public Long getBatchId()                    { return batchId; }
    public void setBatchId(Long batchId)        { this.batchId = batchId; }

    public String getBatchType()                { return batchType; }
    public void setBatchType(String batchType)  { this.batchType = batchType; }

    public int getTotalCheques()                { return totalCheques; }
    public void setTotalCheques(int totalCheques){ this.totalCheques = totalCheques; }

    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }

    public String getSentUser()                 { return sentUser; }
    public void setSentUser(String sentUser)    { this.sentUser = sentUser; }

    public String getMaker()                    { return maker; }
    public void setMaker(String maker)          { this.maker = maker; }

    public String getChecker()                  { return checker; }
    public void setChecker(String checker)      { this.checker = checker; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt){ this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Batch{batchId=" + batchId
                + ", batchType=" + batchType
                + ", status=" + status + "}";
    }
}
