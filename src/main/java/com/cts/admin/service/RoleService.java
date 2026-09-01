package com.cts.admin.service;

import java.util.List;

import com.cts.admin.model.Role;

public interface RoleService {
	
	 public List<Role> getAllRoles();
	 public Role getRoleById(Long roleId);
	 public boolean createRole(Role role);
	 public boolean updateRole(Role role);
	 public boolean updateRoleStatus(Long roleId, String status);
}
