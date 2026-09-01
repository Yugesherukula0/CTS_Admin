package com.cts.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.cts.admin.model.Role;
import com.cts.admin.util.ConnectionPool;

public class RoleDAOimpl implements RoleDAO{
	
	 @Override
    public List<Role> getAllRoles() {

        List<Role> roles = new ArrayList<>();

        String sql = "SELECT role_id, role_name, description, status, "
                   + "created_at, updated_at "
                   + "FROM roles "
                   + "ORDER BY role_id";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Role role = new Role();

                role.setRoleId(resultSet.getLong("role_id"));
                role.setRoleName(resultSet.getString("role_name"));
                role.setDescription(resultSet.getString("description"));
                role.setStatus(resultSet.getString("status"));
                role.setCreatedAt(resultSet.getTimestamp("created_at"));
                role.setUpdatedAt(resultSet.getTimestamp("updated_at"));

                roles.add(role);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }
    
    @Override
    public Role getRoleById(Long roleId) {

        String sql = "SELECT role_id, role_name, description, status, "
                   + "created_at, updated_at "
                   + "FROM roles "
                   + "WHERE role_id = ?";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, roleId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Role role = new Role();

                    role.setRoleId(resultSet.getLong("role_id"));
                    role.setRoleName(resultSet.getString("role_name"));
                    role.setDescription(resultSet.getString("description"));
                    role.setStatus(resultSet.getString("status"));
                    role.setCreatedAt(resultSet.getTimestamp("created_at"));
                    role.setUpdatedAt(resultSet.getTimestamp("updated_at"));

                    return role;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    @Override
    public boolean createRole(Role role) {

        String sql = "INSERT INTO roles "
                   + "(role_name, description, status) "
                   + "VALUES (?, ?, ?)";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role.getRoleName());
            statement.setString(2, role.getDescription());
            statement.setString(3, role.getStatus());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateRole(Role role) {

        String sql = "UPDATE roles "
                   + "SET role_name = ?, "
                   + "description = ?, "
                   + "updated_at = CURRENT_TIMESTAMP "
                   + "WHERE role_id = ?";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role.getRoleName());
            statement.setString(2, role.getDescription());
            statement.setLong(3, role.getRoleId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateRoleStatus(Long roleId, String status) {

        String sql = "UPDATE roles "
                   + "SET status = ?, "
                   + "updated_at = CURRENT_TIMESTAMP "
                   + "WHERE role_id = ?";

        try (Connection connection = ConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, roleId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
