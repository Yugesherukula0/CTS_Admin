package com.cts.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.admin.model.Batch;
import com.cts.admin.util.ConnectionPool;

public class BatchDAOImpl implements BatchDAO {

    /* ------------------------------------------------------------------ */
    /* BATCH CAPTURE
     * Columns: Batch ID | Branch | Cheque Count | Created By | Created At
     * ------------------------------------------------------------------ */

    @Override
    public List<Batch> getBatchCaptureBatches() {

        String sql =
                "SELECT b.batch_id, "
                + "       b.batch_type, "
                + "       b.branch, "
                + "       b.cheque_count, "
                + "       captured.username AS captured_by_username, "
                + "       b.created_at "
                + "FROM   batches b "
                + "LEFT   JOIN users captured ON b.captured_by = captured.user_id "
                + "WHERE  b.batch_type = 'BATCH_CAPTURE' "
                + "ORDER  BY b.batch_id DESC";

        return fetch(sql);
    }

    /* ------------------------------------------------------------------ */
    /* INWARD BATCHES
     * Columns: Batch ID | Cheque Count | Status | Maker | Checker |
     *          Maker Receive Time | Checker Receive Time | Batch Completion Time
     * ------------------------------------------------------------------ */

    @Override
    public List<Batch> getInwardBatches() {

        String sql =
                "SELECT b.batch_id, "
                + "       b.batch_type, "
                + "       b.cheque_count, "
                + "       b.status, "
                + "       maker.username    AS maker_username, "
                + "       checker.username  AS checker_username, "
                + "       b.maker_receive_time, "
                + "       b.checker_receive_time, "
                + "       b.batch_completion_time "
                + "FROM   batches b "
                + "LEFT   JOIN users maker   ON b.maker_id   = maker.user_id "
                + "LEFT   JOIN users checker ON b.checker_id = checker.user_id "
                + "WHERE  b.batch_type = 'INWARD' "
                + "ORDER  BY b.batch_id DESC";

        return fetch(sql);
    }

    /* ------------------------------------------------------------------ */
    /* OUTWARD BATCHES — same columns as Inward
     * ------------------------------------------------------------------ */

    @Override
    public List<Batch> getOutwardBatches() {

        String sql =
                "SELECT b.batch_id, "
                + "       b.batch_type, "
                + "       b.cheque_count, "
                + "       b.status, "
                + "       maker.username    AS maker_username, "
                + "       checker.username  AS checker_username, "
                + "       b.maker_receive_time, "
                + "       b.checker_receive_time, "
                + "       b.batch_completion_time "
                + "FROM   batches b "
                + "LEFT   JOIN users maker   ON b.maker_id   = maker.user_id "
                + "LEFT   JOIN users checker ON b.checker_id = checker.user_id "
                + "WHERE  b.batch_type = 'OUTWARD' "
                + "ORDER  BY b.batch_id DESC";

        return fetch(sql);
    }

    /* ------------------------------------------------------------------ */

    private List<Batch> fetch(String sql) {

        List<Batch> list = new ArrayList<>();

        try (
            Connection conn       = ConnectionPool.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs           = stmt.executeQuery()
        ) {
            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch batch data.", e);
        }

        return list;
    }

    /* ------------------------------------------------------------------ */

    /**
     * Maps a ResultSet row to a Batch.
     * Uses try/catch per optional column so a missing column
     * in a specific query silently returns null rather than crashing.
     */
    private Batch map(ResultSet rs) throws SQLException {

        Batch b = new Batch();

        b.setBatchId(rs.getLong("batch_id"));
        b.setBatchType(rs.getString("batch_type"));

        /* Common to all */
        b.setChequeCount(rs.getInt("cheque_count"));

        /* Batch Capture only */
        trySet(b, rs, "branch",                 (batch, val) -> batch.setBranch(val));
        trySet(b, rs, "captured_by_username",   (batch, val) -> batch.setCapturedBy(val));
        trySetTimestamp(b, rs, "created_at",    (batch, val) -> batch.setCreatedAt(val));

        /* Inward / Outward only */
        trySet(b, rs, "status",                 (batch, val) -> batch.setStatus(val));
        trySet(b, rs, "maker_username",         (batch, val) -> batch.setMaker(val));
        trySet(b, rs, "checker_username",       (batch, val) -> batch.setChecker(val));
        trySetTimestamp(b, rs, "maker_receive_time",    (batch, val) -> batch.setMakerReceiveTime(val));
        trySetTimestamp(b, rs, "checker_receive_time",  (batch, val) -> batch.setCheckerReceiveTime(val));
        trySetTimestamp(b, rs, "batch_completion_time", (batch, val) -> batch.setBatchCompletionTime(val));

        return b;
    }

    /* ------------------------------------------------------------------ */
    /* HELPERS — silently skip columns not present in this query           */
    /* ------------------------------------------------------------------ */

    @FunctionalInterface
    private interface StringSetter  { void set(Batch b, String v); }

    @FunctionalInterface
    private interface TimestampSetter { void set(Batch b, java.sql.Timestamp v); }

    private void trySet(Batch b, ResultSet rs, String col, StringSetter setter) {
        try { setter.set(b, rs.getString(col)); } catch (SQLException ignored) {}
    }

    private void trySetTimestamp(Batch b, ResultSet rs, String col, TimestampSetter setter) {
        try { setter.set(b, rs.getTimestamp(col)); } catch (SQLException ignored) {}
    }
}
