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

    /*
     * Shared SELECT columns — same fields for all three batch types.
     * Joins users table three times for maker, checker, capturedBy.
     */
    private static final String SELECT =
            "SELECT b.batch_id, "
            + "       b.batch_type, "
            + "       b.branch, "
            + "       b.cheque_count, "
            + "       b.total_amount, "
            + "       b.current_module, "
            + "       b.status, "
            + "       maker.username    AS maker_username, "
            + "       checker.username  AS checker_username, "
            + "       captured.username AS captured_by_username, "
            + "       b.created_at "
            + "FROM   batches b "
            + "LEFT   JOIN users maker    ON b.maker_id      = maker.user_id "
            + "LEFT   JOIN users checker  ON b.checker_id    = checker.user_id "
            + "LEFT   JOIN users captured ON b.captured_by   = captured.user_id ";

    /* ------------------------------------------------------------------ */

    @Override
    public List<Batch> getBatchCaptureBatches() {
        return fetch(SELECT
                + "WHERE b.batch_type = 'BATCH_CAPTURE' "
                + "ORDER BY b.batch_id DESC");
    }

    @Override
    public List<Batch> getInwardBatches() {
        return fetch(SELECT
                + "WHERE b.batch_type = 'INWARD' "
                + "ORDER BY b.batch_id DESC");
    }

    @Override
    public List<Batch> getOutwardBatches() {
        return fetch(SELECT
                + "WHERE b.batch_type = 'OUTWARD' "
                + "ORDER BY b.batch_id DESC");
    }

    /* ------------------------------------------------------------------ */

    private List<Batch> fetch(String sql) {

        List<Batch> list = new ArrayList<>();

        try (
            Connection conn  = ConnectionPool.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs     = stmt.executeQuery()
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

    private Batch map(ResultSet rs) throws SQLException {

        Batch b = new Batch();

        b.setBatchId(rs.getLong("batch_id"));
        b.setBatchType(rs.getString("batch_type"));
        b.setBranch(rs.getString("branch"));
        b.setChequeCount(rs.getInt("cheque_count"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setCurrentModule(rs.getString("current_module"));
        b.setStatus(rs.getString("status"));
        b.setMaker(rs.getString("maker_username"));
        b.setChecker(rs.getString("checker_username"));
        b.setCapturedBy(rs.getString("captured_by_username"));
        b.setCreatedAt(rs.getTimestamp("created_at"));

        return b;
    }
}
