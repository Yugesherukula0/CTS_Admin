package com.cts.admin.service;

import java.util.List;

import com.cts.admin.dao.UserDAO;
import com.cts.admin.dao.UserDAOImpl;
import com.cts.admin.model.User;

public class UserServiceImpl implements UserService {

	private final UserDAO userDAO;

	public UserServiceImpl() {
		userDAO = new UserDAOImpl();
	}

	public List<User> getAllUsers() {
		return userDAO.getAllUsers();
	}

	public User getUserById(Long userId) {
		return userDAO.getUserById(userId);
	}

	public boolean usernameExists(String username) {
		return userDAO.usernameExists(username);
	}

	public boolean createUser(User user) {

		if (user == null) {
			return false;
		}

		if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
			return false;
		}

		if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
			return false;
		}

		if (user.getRoleId() == null) {
			return false;
		}

		if (usernameExists(user.getUsername().trim())) {
			return false;
		}

		user.setUsername(user.getUsername().trim());
		user.setFullName(user.getFullName().trim());

		if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
			user.setStatus("ACTIVE");
		}

		return userDAO.createUser(user);
	}

	public boolean updateUser(User user) {

		if (user == null || user.getUserId() == null) {
			return false;
		}

		return userDAO.updateUser(user);
	}

	public boolean updateUserStatus(Long userId, String status) {

		if (userId == null || status == null || status.trim().isEmpty()) {
			return false;
		}

		return userDAO.updateUserStatus(userId, status);
	}
	@Override
	public void deleteUser(Long userId) {

	    if (userId == null) {
	        throw new IllegalArgumentException("User ID cannot be null.");
	    }

	    User user = userDAO.getUserById(userId);

	    if (user == null) {
	        throw new IllegalArgumentException("User not found.");
	    }

	    if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
	        throw new IllegalStateException(
	            "Active user cannot be deleted. Deactivate the user first."
	        );
	    }

	    userDAO.deleteUser(userId);
	}

}
