package com.cts.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.admin.model.Session;
import com.cts.admin.util.ConnectionPool;

public class SessionDAOImpl implements SessionDAO {

    @Override
    public boolean startSession(Long userId) {

        String sql =
                "INSERT INTO sessions "
                + "(status, started_at, started_by) "
                + "VALUES ('ACTIVE', CURRENT_TIMESTAMP, ?)";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, userId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to start internal processing session.",
                    e
            );
        }
    }


    @Override
    public boolean endSession(Long sessionId, Long userId) {

        String sql =
                "UPDATE sessions "
                + "SET status = 'ENDED', "
                + "    ended_at = CURRENT_TIMESTAMP, "
                + "    ended_by = ? "
                + "WHERE session_id = ? "
                + "AND status = 'ACTIVE'";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, userId);
            statement.setLong(2, sessionId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to end internal processing session.",
                    e
            );
        }
    }


    @Override
    public Session getActiveSession() {

        String sql =
                "SELECT session_id, "
                + "       status, "
                + "       started_at, "
                + "       ended_at, "
                + "       started_by, "
                + "       ended_by "
                + "FROM sessions "
                + "WHERE status = 'ACTIVE' "
                + "ORDER BY session_id DESC "
                + "LIMIT 1";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return mapSession(resultSet);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to fetch active internal processing session.",
                    e
            );
        }

        return null;
    }


    @Override
    public List<Session> getAllSessions() {

        List<Session> sessions = new ArrayList<>();

        String sql =
                "SELECT session_id, "
                + "       status, "
                + "       started_at, "
                + "       ended_at, "
                + "       started_by, "
                + "       ended_by "
                + "FROM sessions "
                + "ORDER BY session_id DESC";

        try (
                Connection connection =
                        ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                sessions.add(mapSession(resultSet));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to fetch session history.",
                    e
            );
        }

        return sessions;
    }


    private Session mapSession(ResultSet resultSet)
            throws SQLException {

        Session session = new Session();

        session.setSessionId(
                resultSet.getLong("session_id")
        );

        session.setStatus(
                resultSet.getString("status")
        );

        session.setStartedAt(
                resultSet.getTimestamp("started_at")
        );

        session.setEndedAt(
                resultSet.getTimestamp("ended_at")
        );


        /*
         * c3p0 compatibility:
         *
         * Do NOT use:
         *
         * resultSet.getObject("started_by", Long.class)
         */

        long startedByValue =
                resultSet.getLong("started_by");

        if (!resultSet.wasNull()) {

            session.setStartedBy(startedByValue);
        }


        long endedByValue =
                resultSet.getLong("ended_by");

        if (!resultSet.wasNull()) {

            session.setEndedBy(endedByValue);
        }

        return session;
    }
}