package com.cts.admin.service;

import java.util.List;

import com.cts.admin.dao.RoleDAO;
import com.cts.admin.dao.RoleDAOImpl;
import com.cts.admin.model.Role;

public class RoleServiceImpl implements RoleService{

    private final RoleDAO roleDAO;

    public RoleServiceImpl() {
        roleDAO = new RoleDAOImpl();
    }
    
    @Override
    public List<Role> getAllRoles() {
        return roleDAO.getAllRoles();
    }
    
    @Override
    public Role getRoleById(Long roleId) {
        return roleDAO.getRoleById(roleId);
    }
    
    @Override
    public boolean createRole(Role role) {
        return roleDAO.createRole(role);
    }
    
    @Override
    public boolean updateRole(Role role) {
        return roleDAO.updateRole(role);
    }
    
    @Override
    public boolean updateRoleStatus(Long roleId, String status) {
        return roleDAO.updateRoleStatus(roleId, status);
    }
}