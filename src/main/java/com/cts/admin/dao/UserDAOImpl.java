package com.cts.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.admin.model.User;
import com.cts.admin.util.ConnectionPool;

public class UserDAOImpl implements UserDAO{

    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT user_id, username, full_name, password_hash, "
                   + "role_id, status, created_at, updated_at, last_login_at "
                   + "FROM users "
                   + "ORDER BY user_id";

        try (Connection connection =
                     ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public User getUserById(Long userId) {

        String sql = "SELECT user_id, username, full_name, password_hash, "
                   + "role_id, status, created_at, updated_at, last_login_at "
                   + "FROM users "
                   + "WHERE user_id = ?";

        try (Connection connection =
                     ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean usernameExists(String username) {

        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection =
                     ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createUser(User user) {

        String sql = "INSERT INTO users "
                   + "(username, full_name, password_hash, role_id, status) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection =
                     ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getPasswordHash());
            statement.setLong(4, user.getRoleId());
            statement.setString(5, user.getStatus());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {

        String sql = "UPDATE users "
                   + "SET username = ?, "
                   + "full_name = ?, "
                   + "role_id = ?, "
                   + "updated_at = CURRENT_TIMESTAMP "
                   + "WHERE user_id = ?";

        try (Connection connection =
                     ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFullName());
            statement.setLong(3, user.getRoleId());
            statement.setLong(4, user.getUserId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserStatus(Long userId, String status) {

        String sql = "UPDATE users "
                   + "SET status = ?, "
                   + "updated_at = CURRENT_TIMESTAMP "
                   + "WHERE user_id = ?";

        try (Connection connection =
                     ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapUser(ResultSet resultSet) throws Exception {

        User user = new User();

        user.setUserId(resultSet.getLong("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setFullName(resultSet.getString("full_name"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRoleId(resultSet.getLong("role_id"));
        user.setStatus(resultSet.getString("status"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        user.setLastLoginAt(resultSet.getTimestamp("last_login_at"));

        return user;
    }
    
    @Override
    public void deleteUser(Long userId) {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (
            Connection connection = ConnectionPool.getDataSource().getConnection();
            PreparedStatement preparedStatement =
                connection.prepareStatement(sql)
        ) {

            preparedStatement.setLong(1, userId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to delete user.", e);
        }
    }
}