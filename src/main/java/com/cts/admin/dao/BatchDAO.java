package com.cts.admin.dao;

import java.util.List;

import com.cts.admin.model.Batch;

public interface BatchDAO {

    /**
     * Returns all BATCH_CAPTURE type batches.
     */
    List<Batch> getBatchCaptureBatches();

    /**
     * Returns all INWARD type batches.
     */
    List<Batch> getInwardBatches();

    /**
     * Returns all OUTWARD type batches.
     */
    List<Batch> getOutwardBatches();
}
