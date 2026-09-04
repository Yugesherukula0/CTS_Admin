package com.cts.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.admin.model.AuditLog;
import com.cts.admin.util.ConnectionPool;

public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public boolean createAuditLog(AuditLog auditLog) {

        String sql =
                "INSERT INTO audit_logs "
                + "(event_time, event_date, user_id, module, action, "
                + "related_batch_id, related_session_id) "
                + "VALUES (CURRENT_TIMESTAMP, CURRENT_DATE, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, auditLog.getUserId());
            statement.setString(2, auditLog.getModule());
            statement.setString(3, auditLog.getAction());

            if (auditLog.getRelatedBatchId() != null) {
                statement.setLong(4, auditLog.getRelatedBatchId());
            } else {
                statement.setNull(4, java.sql.Types.BIGINT);
            }

            if (auditLog.getRelatedSessionId() != null) {
                statement.setLong(5, auditLog.getRelatedSessionId());
            } else {
                statement.setNull(5, java.sql.Types.BIGINT);
            }

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to create audit log.",
                    e
            );
        }
    }

    @Override
    public List<AuditLog> getAuditLogs(int page, int pageSize) {

        List<AuditLog> auditLogs = new ArrayList<>();

        int offset = (page - 1) * pageSize;

        String sql =
                "SELECT "
                + "a.audit_id, "
                + "a.event_time, "
                + "a.event_date, "
                + "a.user_id, "
                + "u.full_name AS user_name, "
                + "a.module, "
                + "a.action, "
                + "a.related_batch_id, "
                + "a.related_session_id "
                + "FROM audit_logs a "
                + "LEFT JOIN users u "
                + "ON a.user_id = u.user_id "
                + "ORDER BY a.event_time DESC, a.audit_id DESC "
                + "LIMIT ? OFFSET ?";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, pageSize);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    auditLogs.add(mapAuditLog(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to fetch audit logs.",
                    e
            );
        }

        return auditLogs;
    }

    @Override
    public int getTotalAuditLogCount() {

        String sql =
                "SELECT COUNT(*) "
                + "FROM audit_logs";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to count audit logs.",
                    e
            );
        }

        return 0;
    }

    private AuditLog mapAuditLog(ResultSet resultSet)
            throws SQLException {

        AuditLog auditLog = new AuditLog();

        auditLog.setAuditId(
                resultSet.getLong("audit_id")
        );

        auditLog.setEventTime(
                resultSet.getTimestamp("event_time")
        );

        auditLog.setEventDate(
                resultSet.getDate("event_date")
        );

        long userIdValue =
                resultSet.getLong("user_id");

        if (!resultSet.wasNull()) {
            auditLog.setUserId(userIdValue);
        }

        auditLog.setUserName(
                resultSet.getString("user_name")
        );

        auditLog.setModule(
                resultSet.getString("module")
        );

        auditLog.setAction(
                resultSet.getString("action")
        );

        long batchIdValue =
                resultSet.getLong("related_batch_id");

        if (!resultSet.wasNull()) {
            auditLog.setRelatedBatchId(batchIdValue);
        }

        long sessionIdValue =
                resultSet.getLong("related_session_id");

        if (!resultSet.wasNull()) {
            auditLog.setRelatedSessionId(sessionIdValue);
        }

        return auditLog;
    }
}