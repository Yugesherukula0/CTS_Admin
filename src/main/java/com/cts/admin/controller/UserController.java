package com.cts.admin.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.cts.admin.model.Role;
import com.cts.admin.model.User;
import com.cts.admin.service.RoleService;
import com.cts.admin.service.RoleServiceImpl;
import com.cts.admin.service.UserService;
import com.cts.admin.service.UserServiceImpl;

public class UserController extends GenericForwardComposer<Component> {

	private static final long serialVersionUID = 1L;

	private Listbox userListbox;
	private Paging userPaging;
	private Button createUserButton;

	private UserService userService;
	private RoleService roleService;

	private static final int PAGE_SIZE = 10;

	@Override
	public void doAfterCompose(Component component) throws Exception {

		super.doAfterCompose(component);

		userService = new UserServiceImpl();
		roleService = new RoleServiceImpl();

		/*
		 * Configure database pagination
		 */
		userPaging.setPageSize(PAGE_SIZE);

		userPaging.setTotalSize(userService.getUserCount());

		/*
		 * Load first page
		 *
		 * Page 0 OFFSET = 0
		 */
		loadUsers(0);

		/*
		 * Paging event
		 */
		userPaging.addEventListener("onPaging", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {

				PagingEvent pagingEvent = (PagingEvent) event;

				int pageNo = pagingEvent.getActivePage();

				int offset = pageNo * PAGE_SIZE;

				loadUsers(offset);
			}
		});
	}

	/*
	 * LOAD USERS
	 *
	 * Data is fetched from database using:
	 *
	 * LIMIT 10 OFFSET x
	 */
	private void loadUsers(int offset) {
		int serialNumber=offset+1;

		try {

			List<User> users = userService.getUsers(PAGE_SIZE, offset);

			/*
			 * Clear current rows
			 */
			userListbox.getItems().clear();

			/*
			 * Create rows
			 */
			for (User user : users) {

				Listitem item = new Listitem();

				/*
				 * USER ID
				 */
				Listcell userIdCell = new Listcell();

				userIdCell.appendChild(new Label(String.valueOf(serialNumber)));

				item.appendChild(userIdCell);

				/*
				 * USERNAME
				 */
				Listcell usernameCell = new Listcell();

				usernameCell.appendChild(new Label(user.getUsername()));

				item.appendChild(usernameCell);

				/*
				 * FULL NAME
				 */
				Listcell fullNameCell = new Listcell();

				fullNameCell.appendChild(new Label(user.getFullName()));

				item.appendChild(fullNameCell);

				/*
				 * ROLE
				 */
				Listcell roleCell = new Listcell();

				String roleName = "-";

				if (user.getRole() != null && user.getRole().getRoleName() != null) {

					roleName = user.getRole().getRoleName();
				}

				roleCell.appendChild(new Label(roleName));

				item.appendChild(roleCell);

				/*
				 * STATUS
				 */
				Listcell statusCell = new Listcell();

				String status = user.getStatus();

				if (status == null || status.trim().isEmpty()) {

					status = "-";
				}

				statusCell.appendChild(new Label(status));

				item.appendChild(statusCell);

				/*
				 * LAST LOGIN
				 */
				Listcell lastLoginCell = new Listcell();

				String lastLogin = "-";

				if (user.getLastLoginAt() != null) {

					lastLogin = user.getLastLoginAt().toString();
				}

				lastLoginCell.appendChild(new Label(lastLogin));

				item.appendChild(lastLoginCell);

				/*
				 * ACTIONS
				 */
				Listcell actionCell = new Listcell();

				Hbox actionBox = new Hbox();

				actionBox.setSpacing("8px");
				actionBox.setAlign("center");

				actionBox.setSclass("actions-container");

				/*
				 * EDIT BUTTON
				 */
				Button editButton = new Button();

				editButton.setIconSclass("z-icon-pencil");

				editButton.setSclass("action-button edit-button");

				editButton.setTooltiptext("Edit User");

				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {

						openUserModal(user);
					}
				});

				/*
				 * STATUS BUTTON
				 */
				Button statusButton = new Button();

				statusButton.setIconSclass("z-icon-power-off");

				statusButton.setSclass("action-button power-button");

				if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {

					statusButton.setTooltiptext("Deactivate User");

				} else {

					statusButton.setTooltiptext("Activate User");
				}

				statusButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {

						changeUserStatus(user);
					}
				});

				/*
				 * DELETE BUTTON
				 */
				Button deleteButton = new Button();

				deleteButton.setIconSclass("z-icon-trash");

				deleteButton.setSclass("action-button delete-button");

				deleteButton.setTooltiptext("Delete User");

				deleteButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {

						confirmDeleteUser(user);
					}
				});

				/*
				 * Add buttons
				 */
				actionBox.appendChild(editButton);

				actionBox.appendChild(statusButton);

				actionBox.appendChild(deleteButton);

				actionCell.appendChild(actionBox);

				item.appendChild(actionCell);

				/*
				 * Store User object
				 */
				item.setValue(user);

				userListbox.appendChild(item);
				serialNumber++;
			}

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to load users.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	/*
	 * CREATE USER
	 */
	public void onClick$createUserButton(Event event) throws Exception {

		openUserModal(null);
	}

	/*
	 * CREATE / EDIT USER
	 */
	private void openUserModal(User existingUser) {

		boolean editMode = existingUser != null;

		Window window = new Window();

		if (editMode) {

			window.setTitle("Edit User");

		} else {

			window.setTitle("Create User");
		}

		window.setWidth("500px");
		window.setBorder("normal");
		window.setClosable(true);
		window.setSizable(false);

		window.setStyle("padding:20px;");

		Vbox mainBox = new Vbox();

		mainBox.setSpacing("15px");
		mainBox.setWidth("100%");

		/*
		 * USERNAME
		 */
		Label usernameLabel = new Label("Username");

		Textbox usernameBox = new Textbox();

		usernameBox.setWidth("100%");

		usernameBox.setPlaceholder("Enter username");

		if (editMode) {

			usernameBox.setValue(existingUser.getUsername());

			usernameBox.setReadonly(true);
		}

		mainBox.appendChild(usernameLabel);

		mainBox.appendChild(usernameBox);

		/*
		 * FULL NAME
		 */
		Label fullNameLabel = new Label("Full Name");

		Textbox fullNameBox = new Textbox();

		fullNameBox.setWidth("100%");

		fullNameBox.setPlaceholder("Enter full name");

		if (editMode) {

			fullNameBox.setValue(existingUser.getFullName());
		}

		mainBox.appendChild(fullNameLabel);

		mainBox.appendChild(fullNameBox);

		/*
		 * PASSWORD
		 */
		Label passwordLabel = new Label("Password");

		Textbox passwordBox = new Textbox();

		passwordBox.setType("password");
		passwordBox.setWidth("100%");

		if (editMode) {

			passwordBox.setPlaceholder("Leave blank to keep current password");

		} else {

			passwordBox.setPlaceholder("Enter password");
		}

		mainBox.appendChild(passwordLabel);

		mainBox.appendChild(passwordBox);

		/*
		 * ROLE
		 */
		Label roleLabel = new Label("Role");

		Combobox roleCombo = new Combobox();

		roleCombo.setWidth("100%");
		roleCombo.setReadonly(true);

		roleCombo.setPlaceholder("Select role");

		/*
		 * Get roles from database
		 */
		List<Role> roles = roleService.getAllRoles();

		for (Role role : roles) {

			Comboitem roleItem = new Comboitem();

			roleItem.setLabel(role.getRoleName());

			roleItem.setValue(role.getRoleId());

			/*
			 * IMPORTANT:
			 *
			 * First add Comboitem to Combobox.
			 */
			roleCombo.appendChild(roleItem);

			/*
			 * Then select existing role.
			 *
			 * Previously this was done BEFORE appendChild(), which caused:
			 *
			 * Not a child: <Comboitem null>
			 */
			if (editMode && existingUser.getRole() != null
					&& existingUser.getRole().getRoleId().equals(role.getRoleId())) {

				roleCombo.setSelectedItem(roleItem);
			}
		}

		mainBox.appendChild(roleLabel);

		mainBox.appendChild(roleCombo);

		/*
		 * BUTTONS
		 */
		Hbox buttonBox = new Hbox();

		buttonBox.setSpacing("10px");
		buttonBox.setAlign("end");
		buttonBox.setWidth("100%");

		/*
		 * CANCEL
		 */
		Button cancelButton = new Button();

		cancelButton.setLabel("Cancel");

		cancelButton.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {

				window.detach();
			}
		});

		/*
		 * SAVE
		 */
		Button saveButton = new Button();

		if (editMode) {

			saveButton.setLabel("Update User");

		} else {

			saveButton.setLabel("Create User");
		}

		saveButton.setSclass("primary-button");

		saveButton.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {

				saveUser(window, existingUser, usernameBox, fullNameBox, passwordBox, roleCombo);
			}
		});

		buttonBox.appendChild(cancelButton);

		buttonBox.appendChild(saveButton);

		mainBox.appendChild(buttonBox);

		window.appendChild(mainBox);

		/*
		 * Attach window to current page
		 */
		window.setPage(userListbox.getPage());

		window.doModal();
	}

	/*
	 * SAVE USER
	 */
	private void saveUser(Window window, User existingUser, Textbox usernameBox, Textbox fullNameBox,
			Textbox passwordBox, Combobox roleCombo) {

		String username = usernameBox.getValue().trim();

		String fullName = fullNameBox.getValue().trim();

		String password = passwordBox.getValue();

		/*
		 * USERNAME VALIDATION
		 */
		if (username.isEmpty()) {

			Messagebox.show("Username is required.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		/*
		 * FULL NAME VALIDATION
		 */
		if (fullName.isEmpty()) {

			Messagebox.show("Full name is required.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		/*
		 * PASSWORD VALIDATION
		 */
		if (existingUser == null && (password == null || password.isEmpty())) {

			Messagebox.show("Password is required.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		/*
		 * ROLE VALIDATION
		 */
		if (roleCombo.getSelectedItem() == null) {

			Messagebox.show("Please select a role.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		/*
		 * Get Role ID from Comboitem
		 */
		Long roleId = (Long) roleCombo.getSelectedItem().getValue();

		try {

			/*
			 * CREATE USER
			 */
			if (existingUser == null) {

				User user = new User();

				user.setUsername(username);

				user.setFullName(fullName);

				user.setPasswordHash(password);

				/*
				 * USER HAS ROLE
				 */
				Role role = new Role();

				role.setRoleId(roleId);

				user.setRole(role);

				user.setStatus("ACTIVE");

				boolean created = userService.createUser(user);

				if (!created) {

					Messagebox.show("Unable to create user. " + "Username may already exist.", "Error", Messagebox.OK,
							Messagebox.ERROR);

					return;
				}

				Messagebox.show("User created successfully.", "Success", Messagebox.OK, Messagebox.INFORMATION);

			} else {

				/*
				 * EDIT USER
				 */
				existingUser.setFullName(fullName);

				/*
				 * USER HAS ROLE
				 */
				Role role = new Role();

				role.setRoleId(roleId);

				existingUser.setRole(role);

				/*
				 * Password is currently accepted here.
				 */
				if (password != null && !password.isEmpty()) {

					existingUser.setPasswordHash(password);
				}

				userService.updateUser(existingUser);

				Messagebox.show("User updated successfully.", "Success", Messagebox.OK, Messagebox.INFORMATION);
			}

			window.detach();

			/*
			 * Refresh current database page
			 */
			int activePage = userPaging.getActivePage();

			int offset = activePage * PAGE_SIZE;

			/*
			 * Update total records
			 */
			userPaging.setTotalSize(userService.getUserCount());

			/*
			 * Load current page again
			 */
			loadUsers(offset);

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to save user.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	/*
	 * ACTIVATE / DEACTIVATE USER
	 */
	private void changeUserStatus(User user) {

		boolean isActive = "ACTIVE".equalsIgnoreCase(user.getStatus());

		String newStatus;
		String action;

		if (isActive) {

			newStatus = "INACTIVE";
			action = "deactivate";

		} else {

			newStatus = "ACTIVE";
			action = "activate";
		}

		Messagebox.show("Are you sure you want to " + action + " user '" + user.getUsername() + "'?",

				action.substring(0, 1).toUpperCase() + action.substring(1) + " User",

				Messagebox.YES | Messagebox.NO,

				Messagebox.QUESTION,

				new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {

						if ("onYes".equals(event.getName())) {

							try {

								userService.updateUserStatus(user.getUserId(), newStatus);

								Messagebox.show("User " + action + "d successfully.", "Success", Messagebox.OK,
										Messagebox.INFORMATION);

								/*
								 * Reload current DB page
								 */
								int activePage = userPaging.getActivePage();

								loadUsers(activePage * PAGE_SIZE);

							} catch (Exception e) {

								e.printStackTrace();

								Messagebox.show("Unable to " + action + " user.", "Error", Messagebox.OK,
										Messagebox.ERROR);
							}
						}
					}
				});
	}

	/*
	 * DELETE USER
	 */
	private void confirmDeleteUser(User user) {

		Messagebox.show("Are you sure you want to " + "permanently delete user '" + user.getUsername() + "'?",

				"Delete User",

				Messagebox.YES | Messagebox.NO,

				Messagebox.EXCLAMATION,

				new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {

						if ("onYes".equals(event.getName())) {

							try {

								userService.deleteUser(user.getUserId());

								Messagebox.show("User deleted successfully.", "Success", Messagebox.OK,
										Messagebox.INFORMATION);

								/*
								 * Recalculate total
								 */
								userPaging.setTotalSize(userService.getUserCount());

								/*
								 * Reload current page
								 */
								int activePage = userPaging.getActivePage();

								int offset = activePage * PAGE_SIZE;

								loadUsers(offset);

							} catch (Exception e) {

								e.printStackTrace();

								Messagebox.show("Unable to delete user.", "Error", Messagebox.OK, Messagebox.ERROR);
							}
						}
					}
				});
	}
}