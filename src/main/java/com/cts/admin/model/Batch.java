package com.cts.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Batch implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long      batchId;
    private String    batchType;       /* BATCH_CAPTURE | INWARD | OUTWARD */
    private String    branch;          /* Source branch */
    private int       chequeCount;     /* Number of cheques */
    private double    totalAmount;     /* Batch value */
    private String    currentModule;   /* Where the batch currently is */
    private String    status;          /* Current processing state */
    private String    maker;           /* Maker currently assigned */
    private String    checker;         /* Checker currently assigned */
    private String    capturedBy;      /* User who created/captured the batch */
    private Timestamp createdAt;       /* Batch creation time */

    /* ------------------------------------------------------------------ */

    public Batch() {}

    /* ------------------------------------------------------------------ */

    public Long getBatchId()                         { return batchId; }
    public void setBatchId(Long batchId)             { this.batchId = batchId; }

    public String getBatchType()                     { return batchType; }
    public void setBatchType(String batchType)       { this.batchType = batchType; }

    public String getBranch()                        { return branch; }
    public void setBranch(String branch)             { this.branch = branch; }

    public int getChequeCount()                      { return chequeCount; }
    public void setChequeCount(int chequeCount)      { this.chequeCount = chequeCount; }

    public double getTotalAmount()                   { return totalAmount; }
    public void setTotalAmount(double totalAmount)   { this.totalAmount = totalAmount; }

    public String getCurrentModule()                 { return currentModule; }
    public void setCurrentModule(String currentModule){ this.currentModule = currentModule; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    public String getMaker()                         { return maker; }
    public void setMaker(String maker)               { this.maker = maker; }

    public String getChecker()                       { return checker; }
    public void setChecker(String checker)           { this.checker = checker; }

    public String getCapturedBy()                    { return capturedBy; }
    public void setCapturedBy(String capturedBy)     { this.capturedBy = capturedBy; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Batch{batchId=" + batchId
                + ", batchType=" + batchType
                + ", status=" + status + "}";
    }
}
