package com.cts.admin.dao;

import java.util.List;

import com.cts.admin.model.Role;

public interface RoleDAO {
	
	 public List<Role> getAllRoles();
	 public Role getRoleById(Long roleId);
	 public boolean createRole(Role role);
	 public boolean updateRole(Role role);
	 public boolean updateRoleStatus(Long roleId, String status);
}
