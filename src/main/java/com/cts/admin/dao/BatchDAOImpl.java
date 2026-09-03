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

    @Override
    public List<Batch> getBatchCaptureBatches() {

        String sql =
                "SELECT b.batch_id, "
                + "       b.batch_type, "
                + "       b.total_cheques, "
                + "       b.status, "
                + "       u.username AS sent_user, "
                + "       b.created_at, "
                + "       b.updated_at "
                + "FROM   batches b "
                + "LEFT   JOIN users u "
                + "       ON b.sent_by = u.user_id "
                + "WHERE  b.batch_type = 'BATCH_CAPTURE' "
                + "ORDER  BY b.batch_id DESC";

        return fetchBatches(sql);
    }

    /* ------------------------------------------------------------------ */

    @Override
    public List<Batch> getInwardBatches() {

        String sql =
                "SELECT b.batch_id, "
                + "       b.batch_type, "
                + "       b.total_cheques, "
                + "       b.status, "
                + "       maker.username  AS maker_username, "
                + "       checker.username AS checker_username, "
                + "       b.created_at, "
                + "       b.updated_at "
                + "FROM   batches b "
                + "LEFT   JOIN users maker "
                + "       ON b.maker_id = maker.user_id "
                + "LEFT   JOIN users checker "
                + "       ON b.checker_id = checker.user_id "
                + "WHERE  b.batch_type = 'INWARD' "
                + "ORDER  BY b.batch_id DESC";

        return fetchBatches(sql);
    }

    /* ------------------------------------------------------------------ */

    @Override
    public List<Batch> getOutwardBatches() {

        String sql =
                "SELECT b.batch_id, "
                + "       b.batch_type, "
                + "       b.total_cheques, "
                + "       b.status, "
                + "       b.created_at, "
                + "       b.updated_at "
                + "FROM   batches b "
                + "WHERE  b.batch_type = 'OUTWARD' "
                + "ORDER  BY b.batch_id DESC";

        return fetchBatches(sql);
    }

    /* ------------------------------------------------------------------ */

    /**
     * Shared fetch-and-map helper.
     * Safely reads whichever columns are present in the ResultSet.
     */
    private List<Batch> fetchBatches(String sql) {

        List<Batch> batches = new ArrayList<>();

        try (
            Connection conn =
                    ConnectionPool.getDataSource().getConnection();

            PreparedStatement stmt =
                    conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                batches.add(mapBatch(rs));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to fetch batch data.", e
            );
        }

        return batches;
    }

    /* ------------------------------------------------------------------ */

    /**
     * Maps a ResultSet row to a Batch object.
     * Uses column-name lookup; missing optional columns return null safely.
     */
    private Batch mapBatch(ResultSet rs) throws SQLException {

        Batch batch = new Batch();

        batch.setBatchId(rs.getLong("batch_id"));
        batch.setBatchType(rs.getString("batch_type"));
        batch.setTotalCheques(rs.getInt("total_cheques"));
        batch.setStatus(rs.getString("status"));
        batch.setCreatedAt(rs.getTimestamp("created_at"));
        batch.setUpdatedAt(rs.getTimestamp("updated_at"));

        /*
         * Optional columns — only present for specific query types.
         * Wrap in try/catch so a missing column does not crash the mapper.
         */
        try {
            batch.setSentUser(rs.getString("sent_user"));
        } catch (SQLException ignored) {}

        try {
            batch.setMaker(rs.getString("maker_username"));
        } catch (SQLException ignored) {}

        try {
            batch.setChecker(rs.getString("checker_username"));
        } catch (SQLException ignored) {}

        return batch;
    }
}
