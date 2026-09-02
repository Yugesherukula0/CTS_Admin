package com.cts.admin.service;

import java.util.List;

import com.cts.admin.model.User;

public interface UserService {
	
	
	public List<User> getAllUsers() ;
	public User getUserById(Long userId);
	public boolean usernameExists(String username) ;
	public boolean createUser(User user) ;
	public boolean updateUser(User user) ;
	public boolean updateUserStatus(Long userId, String status);
	void deleteUser(Long userId);
}
