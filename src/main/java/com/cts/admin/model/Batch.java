package com.cts.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Batch implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long      batchId;
    private String    batchType;            /* BATCH_CAPTURE | INWARD | OUTWARD */
    private String    branch;               /* Source branch */
    private int       chequeCount;          /* Number of cheques */
    private String    status;               /* Current processing state */
    private String    maker;                /* Maker assigned */
    private String    checker;              /* Checker assigned */
    private String    capturedBy;           /* User who captured the batch */
    private Timestamp createdAt;            /* Batch creation time */
    private Timestamp makerReceiveTime;     /* When maker received the batch */
    private Timestamp checkerReceiveTime;   /* When checker received the batch */
    private Timestamp batchCompletionTime;  /* When batch was completed */

    /* ------------------------------------------------------------------ */

    public Batch() {}

    /* ------------------------------------------------------------------ */

    public Long getBatchId()                                   { return batchId; }
    public void setBatchId(Long batchId)                       { this.batchId = batchId; }

    public String getBatchType()                               { return batchType; }
    public void setBatchType(String batchType)                 { this.batchType = batchType; }

    public String getBranch()                                  { return branch; }
    public void setBranch(String branch)                       { this.branch = branch; }

    public int getChequeCount()                                { return chequeCount; }
    public void setChequeCount(int chequeCount)                { this.chequeCount = chequeCount; }

    public String getStatus()                                  { return status; }
    public void setStatus(String status)                       { this.status = status; }

    public String getMaker()                                   { return maker; }
    public void setMaker(String maker)                         { this.maker = maker; }

    public String getChecker()                                 { return checker; }
    public void setChecker(String checker)                     { this.checker = checker; }

    public String getCapturedBy()                              { return capturedBy; }
    public void setCapturedBy(String capturedBy)               { this.capturedBy = capturedBy; }

    public Timestamp getCreatedAt()                            { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)              { this.createdAt = createdAt; }

    public Timestamp getMakerReceiveTime()                     { return makerReceiveTime; }
    public void setMakerReceiveTime(Timestamp t)               { this.makerReceiveTime = t; }

    public Timestamp getCheckerReceiveTime()                   { return checkerReceiveTime; }
    public void setCheckerReceiveTime(Timestamp t)             { this.checkerReceiveTime = t; }

    public Timestamp getBatchCompletionTime()                  { return batchCompletionTime; }
    public void setBatchCompletionTime(Timestamp t)            { this.batchCompletionTime = t; }

    @Override
    public String toString() {
        return "Batch{batchId=" + batchId
                + ", batchType=" + batchType
                + ", status=" + status + "}";
    }
}
