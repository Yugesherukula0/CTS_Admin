package com.cts.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.admin.model.Role;
import com.cts.admin.model.User;
import com.cts.admin.util.ConnectionPool;

public class UserDAOImpl implements UserDAO {

	@Override
	public List<User> getUsers(int limit, int offset, String searchText, Long roleId, String status) {

		List<User> users = new ArrayList<>();

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT u.user_id, " + "       u.username, " + "       u.full_name, " + "       u.role_id, "
				+ "       r.role_name, " + "       r.description AS role_description, "
				+ "       r.status AS role_status, " + "       u.status, " + "       u.last_login_at " + "FROM users u "
				+ "LEFT JOIN roles r ON u.role_id = r.role_id " + "WHERE 1=1 ");

		List<Object> parameters = new ArrayList<>();

		/*
		 * SEARCH FILTER
		 *
		 * Searches by: 1. Username 2. Full name 3. User ID
		 */
		if (searchText != null && !searchText.trim().isEmpty()) {

			sql.append("AND (" + "LOWER(u.username) LIKE ? " + "OR LOWER(u.full_name) LIKE ? "
					+ "OR CAST(u.user_id AS TEXT) LIKE ?" + ") ");

			String searchValue = "%" + searchText.trim().toLowerCase() + "%";

			parameters.add(searchValue);
			parameters.add(searchValue);
			parameters.add(searchValue);
		}

		/*
		 * ROLE FILTER
		 */
		if (roleId != null) {

			sql.append("AND u.role_id = ? ");

			parameters.add(roleId);
		}

		/*
		 * STATUS FILTER
		 */
		if (status != null && !status.trim().isEmpty()) {

			sql.append("AND u.status = ? ");

			parameters.add(status);
		}

		/*
		 * PAGINATION
		 */
		sql.append("ORDER BY u.user_id " + "LIMIT ? OFFSET ?");

		parameters.add(limit);
		parameters.add(offset);

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql.toString())) {

			/*
			 * Set all filter parameters followed by LIMIT and OFFSET.
			 */
			for (int i = 0; i < parameters.size(); i++) {

				statement.setObject(i + 1, parameters.get(i));
			}

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					User user = mapUser(resultSet);

					users.add(user);
				}
			}

		} catch (SQLException e) {

			throw new RuntimeException("Unable to fetch users.", e);
		}

		return users;
	}

	@Override
	public int getUserCount() {

		String sql = "SELECT COUNT(*) FROM users";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql);

				ResultSet resultSet = statement.executeQuery()) {

			if (resultSet.next()) {

				return resultSet.getInt(1);
			}

		} catch (SQLException e) {

			throw new RuntimeException("Unable to count users.", e);
		}

		return 0;
	}

	@Override
	public User getUserById(Long userId) {

		String sql = "SELECT u.user_id, " + "       u.username, " + "       u.full_name, " + "       u.password_hash, "
				+ "       u.role_id, " + "       r.role_name, " + "       r.description AS role_description, "
				+ "       r.status AS role_status, " + "       r.created_at AS role_created_at, "
				+ "       r.updated_at AS role_updated_at, " + "       u.status, " + "       u.created_at, "
				+ "       u.updated_at, " + "       u.last_login_at " + "FROM users u "
				+ "LEFT JOIN roles r ON u.role_id = r.role_id " + "WHERE u.user_id = ?";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setLong(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return mapUser(resultSet);
				}
			}

		} catch (SQLException e) {

			throw new RuntimeException("Unable to fetch user.", e);
		}

		return null;
	}

	@Override
	public boolean usernameExists(String username) {

		String sql = "SELECT COUNT(*) " + "FROM users " + "WHERE username = ?";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, username);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return resultSet.getInt(1) > 0;
				}
			}

		} catch (SQLException e) {

			throw new RuntimeException("Unable to check username.", e);
		}

		return false;
	}

	@Override
	public boolean createUser(User user) {

		String sql = "INSERT INTO users " + "(username, full_name, password_hash, role_id, status) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, user.getUsername());

			statement.setString(2, user.getFullName());

			statement.setString(3, user.getPasswordHash());

			statement.setLong(4, user.getRole().getRoleId());

			statement.setString(5, user.getStatus());

			return statement.executeUpdate() > 0;

		} catch (SQLException e) {

			throw new RuntimeException("Unable to create user.", e);
		}
	}

	@Override
	public boolean updateUser(User user) {

		String sql = "UPDATE users " + "SET username = ?, " + "    full_name = ?, " + "    role_id = ?, "
				+ "    updated_at = CURRENT_TIMESTAMP " + "WHERE user_id = ?";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, user.getUsername());

			statement.setString(2, user.getFullName());

			statement.setLong(3, user.getRole().getRoleId());

			statement.setLong(4, user.getUserId());

			return statement.executeUpdate() > 0;

		} catch (SQLException e) {

			throw new RuntimeException("Unable to update user.", e);
		}
	}

	@Override
	public boolean updateUserStatus(Long userId, String status) {

		String sql = "UPDATE users " + "SET status = ?, " + "    updated_at = CURRENT_TIMESTAMP " + "WHERE user_id = ?";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, status);

			statement.setLong(2, userId);

			return statement.executeUpdate() > 0;

		} catch (SQLException e) {

			throw new RuntimeException("Unable to update user status.", e);
		}
	}

	@Override
	public void deleteUser(Long userId) {

		String sql = "DELETE FROM users " + "WHERE user_id = ?";

		try (Connection connection = ConnectionPool.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setLong(1, userId);

			statement.executeUpdate();

		} catch (SQLException e) {

			throw new RuntimeException("Unable to delete user.", e);
		}
	}

	private User mapUser(ResultSet resultSet) throws SQLException {

		User user = new User();

		user.setUserId(resultSet.getLong("user_id"));

		user.setUsername(resultSet.getString("username"));

		user.setFullName(resultSet.getString("full_name"));

		/*
		 * password_hash is not present in getUsers(). It is present in getUserById().
		 */
		if (hasColumn(resultSet, "password_hash")) {

			user.setPasswordHash(resultSet.getString("password_hash"));
		}

		/*
		 * Build Role object
		 */
		long roleIdValue = resultSet.getLong("role_id");

		Long roleId = null;

		if (!resultSet.wasNull()) {
			roleId = roleIdValue;
		}

		if (roleId != null) {

			Role role = new Role();

			role.setRoleId(roleId);

			role.setRoleName(resultSet.getString("role_name"));

			role.setDescription(resultSet.getString("role_description"));

			role.setStatus(resultSet.getString("role_status"));

			if (hasColumn(resultSet, "role_created_at")) {

				role.setCreatedAt(resultSet.getTimestamp("role_created_at"));
			}

			if (hasColumn(resultSet, "role_updated_at")) {

				role.setUpdatedAt(resultSet.getTimestamp("role_updated_at"));
			}

			user.setRole(role);
		}

		user.setStatus(resultSet.getString("status"));

		if (hasColumn(resultSet, "created_at")) {

			if (resultSet.getTimestamp("created_at") != null) {

				user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime().toLocalDate());
			}
		}

		if (hasColumn(resultSet, "updated_at")) {

			if (resultSet.getTimestamp("updated_at") != null) {

				user.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime().toLocalDate());
			}
		}

		user.setLastLoginAt(resultSet.getTimestamp("last_login_at"));

		return user;
	}

	private boolean hasColumn(ResultSet resultSet, String columnName) {

		try {

			resultSet.findColumn(columnName);

			return true;

		} catch (SQLException e) {

			return false;
		}
	}
}