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

	// ============================================================
	// ZUL COMPONENTS
	// ============================================================

	private Listbox userListbox;
	private Paging userPaging;

	private Button createUserButton;
	private Button searchUserButton;
	private Button clearUserFilterButton;

	private Textbox userSearchTextbox;

	// IMPORTANT:
	// These names exactly match the IDs in your ZUL
	private Combobox roleFilterCombobox;
	private Combobox statusFilterCombobox;

	// ============================================================
	// SERVICES
	// ============================================================

	private UserService userService;
	private RoleService roleService;

	private static final int PAGE_SIZE = 10;

	// ============================================================
	// INITIALIZATION
	// ============================================================

	@Override
	public void doAfterCompose(Component component) throws Exception {

		super.doAfterCompose(component);

		userService = new UserServiceImpl();
		roleService = new RoleServiceImpl();

		// Load filters
		loadRoleFilter();
		loadStatusFilter();

		// Pagination
		userPaging.setPageSize(PAGE_SIZE);

		// Initial total count
		userPaging.setTotalSize(userService.getUserCount());

		// Load first page
		loadUsers(0);

		// Pagination event
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

	// ============================================================
	// LOAD USERS
	// ============================================================

	private void loadUsers(int offset) {

		int serialNumber = offset + 1;

		try {

			String searchText = userSearchTextbox.getValue();

			if (searchText == null) {
				searchText = "";
			}

			searchText = searchText.trim();

			// ----------------------------------------------------
			// ROLE FILTER
			// ----------------------------------------------------

			Long roleId = null;

			if (roleFilterCombobox.getSelectedItem() != null) {

				Object value = roleFilterCombobox.getSelectedItem().getValue();

				if (value != null) {
					roleId = (Long) value;
				}
			}

			// ----------------------------------------------------
			// STATUS FILTER
			// ----------------------------------------------------

			String status = null;

			if (statusFilterCombobox.getSelectedItem() != null) {

				Object value = statusFilterCombobox.getSelectedItem().getValue();

				if (value != null) {
					status = value.toString();
				}
			}

			// ----------------------------------------------------
			// GET USERS
			// ----------------------------------------------------

			List<User> users = userService.getUsers(PAGE_SIZE, offset, searchText, roleId, status);

			userListbox.getItems().clear();

			// ----------------------------------------------------
			// CREATE ROWS
			// ----------------------------------------------------

			for (User user : users) {

				Listitem item = new Listitem();

				// =================================================
				// S.NO
				// =================================================

				Listcell userIdCell = new Listcell();

				Label serialLabel = new Label(String.valueOf(serialNumber));

				serialLabel.setSclass("user-id-label");

				userIdCell.appendChild(serialLabel);

				item.appendChild(userIdCell);

				// =================================================
				// USERNAME
				// =================================================

				Listcell usernameCell = new Listcell();

				Label usernameLabel = new Label(user.getUsername());

				usernameLabel.setSclass("username-label");

				usernameCell.appendChild(usernameLabel);

				item.appendChild(usernameCell);

				// =================================================
				// FULL NAME
				// =================================================

				Listcell fullNameCell = new Listcell();

				String fullName = user.getFullName();

				if (fullName == null || fullName.trim().isEmpty()) {

					fullName = "-";
				}

				Label fullNameLabel = new Label(fullName);

				fullNameLabel.setSclass("full-name-label");

				fullNameCell.appendChild(fullNameLabel);

				item.appendChild(fullNameCell);

				// =================================================
				// ASSIGNED ROLE
				// =================================================

				Listcell roleCell = new Listcell();

				String roleName = "-";

				if (user.getRole() != null && user.getRole().getRoleName() != null) {

					roleName = user.getRole().getRoleName();
				}

				Hbox roleContainer = new Hbox();

				roleContainer.setSclass("assigned-role-container");

				Label roleLabel = new Label(roleName);

				roleLabel.setSclass("assigned-role-label");

				roleContainer.appendChild(roleLabel);

				roleCell.appendChild(roleContainer);

				item.appendChild(roleCell);

				// =================================================
				// STATUS
				// =================================================

				Listcell statusCell = new Listcell();

				String userStatus = user.getStatus();

				if (userStatus == null || userStatus.trim().isEmpty()) {

					userStatus = "-";
				}

				Hbox statusContainer = new Hbox();

				statusContainer.setSclass("status-container");

				Hbox statusBadge = new Hbox();

				statusBadge.setAlign("center");

				statusBadge.setSclass("status-badge");

				Label statusIcon = new Label("●");

				statusIcon.setSclass("status-icon");

				Label statusLabel = new Label(userStatus);

				statusLabel.setSclass("status-label");

				statusBadge.appendChild(statusIcon);
				statusBadge.appendChild(statusLabel);

				statusContainer.appendChild(statusBadge);

				statusCell.appendChild(statusContainer);

				item.appendChild(statusCell);

				// =================================================
				// ACTIONS
				// =================================================

				Listcell actionCell = new Listcell();

				Hbox actionBox = new Hbox();

				actionBox.setSpacing("8px");
				actionBox.setAlign("center");
				actionBox.setSclass("actions-container");

				// -------------------------------------------------
				// EDIT BUTTON
				// -------------------------------------------------

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

				// -------------------------------------------------
				// STATUS BUTTON
				// -------------------------------------------------

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

				// -------------------------------------------------
				// DELETE BUTTON
				// -------------------------------------------------

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

				// -------------------------------------------------
				// ADD BUTTONS
				// -------------------------------------------------

				actionBox.appendChild(editButton);
				actionBox.appendChild(statusButton);
				actionBox.appendChild(deleteButton);

				actionCell.appendChild(actionBox);

				item.appendChild(actionCell);

				item.setValue(user);

				userListbox.appendChild(item);

				serialNumber++;
			}

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to load users.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	// ============================================================
	// CREATE USER BUTTON
	// ============================================================

	public void onClick$createUserButton(Event event) throws Exception {

		openUserModal(null);
	}

	// ============================================================
	// SEARCH BUTTON
	// ============================================================

	public void onClick$searchUserButton(Event event) throws Exception {

		try {

			// Always return to first page
			userPaging.setActivePage(0);

			// Reload total count based on filters
			updateFilteredTotalSize();

			// Load first page
			loadUsers(0);

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to search users.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	// ============================================================
	// CLEAR FILTER BUTTON
	// ============================================================

	public void onClick$clearUserFilterButton(Event event) throws Exception {

		try {

			// Clear search
			userSearchTextbox.setValue("");

			// Reset role
			if (roleFilterCombobox.getItemCount() > 0) {

				roleFilterCombobox.setSelectedIndex(0);
			}

			// Reset status
			if (statusFilterCombobox.getItemCount() > 0) {

				statusFilterCombobox.setSelectedIndex(0);
			}

			// Go to first page
			userPaging.setActivePage(0);

			// Restore complete count
			userPaging.setTotalSize(userService.getUserCount());

			// Reload
			loadUsers(0);

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to clear filters.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	// ============================================================
	// LOAD ROLE FILTER
	// ============================================================

	private void loadRoleFilter() {

		try {

			roleFilterCombobox.getItems().clear();

			Comboitem allItem = new Comboitem();

			allItem.setLabel("All Roles");
			allItem.setValue(null);

			roleFilterCombobox.appendChild(allItem);

			List<Role> roles = roleService.getAllRoles();

			for (Role role : roles) {

				Comboitem item = new Comboitem();

				// Skip ADMIN from filter
				if ("ADMIN".equalsIgnoreCase(role.getRoleName())) {
					continue;
				}

				item.setLabel(role.getRoleName());

				item.setValue(role.getRoleId());

				roleFilterCombobox.appendChild(item);
			}

			roleFilterCombobox.setSelectedIndex(0);

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to load roles.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	// ============================================================
	// LOAD STATUS FILTER
	// ============================================================

	private void loadStatusFilter() {

		try {

			statusFilterCombobox.getItems().clear();

			// ----------------------------------------------------
			// ALL
			// ----------------------------------------------------

			Comboitem allItem = new Comboitem();

			allItem.setLabel("All Statuses");

			allItem.setValue(null);

			statusFilterCombobox.appendChild(allItem);

			// ----------------------------------------------------
			// ACTIVE
			// ----------------------------------------------------

			Comboitem activeItem = new Comboitem();

			activeItem.setLabel("ACTIVE");
			activeItem.setValue("ACTIVE");

			statusFilterCombobox.appendChild(activeItem);

			// ----------------------------------------------------
			// INACTIVE
			// ----------------------------------------------------

			Comboitem inactiveItem = new Comboitem();

			inactiveItem.setLabel("INACTIVE");
			inactiveItem.setValue("INACTIVE");

			statusFilterCombobox.appendChild(inactiveItem);

			statusFilterCombobox.setSelectedIndex(0);

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to load status filter.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	// ============================================================
	// UPDATE FILTERED TOTAL SIZE
	// ============================================================

	private void updateFilteredTotalSize() {

		try {

			String searchText = userSearchTextbox.getValue();

			if (searchText == null) {
				searchText = "";
			}

			searchText = searchText.trim();

			Long roleId = null;

			if (roleFilterCombobox.getSelectedItem() != null) {

				Object value = roleFilterCombobox.getSelectedItem().getValue();

				if (value != null) {
					roleId = (Long) value;
				}
			}

			String status = null;

			if (statusFilterCombobox.getSelectedItem() != null) {

				Object value = statusFilterCombobox.getSelectedItem().getValue();

				if (value != null) {
					status = value.toString();
				}
			}

			/*
			 * If your UserService has a filtered count method, use it here.
			 *
			 * Since you specified that the existing service API should not be changed, this
			 * uses getUserCount() as the available count.
			 */
			userPaging.setTotalSize(userService.getUserCount());

		} catch (Exception e) {

			e.printStackTrace();

			userPaging.setTotalSize(0);
		}
	}

	// ============================================================
	// OPEN CREATE / EDIT USER MODAL
	// ============================================================

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

		// ========================================================
		// USERNAME
		// ========================================================

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

		// ========================================================
		// FULL NAME
		// ========================================================

		Label fullNameLabel = new Label("Full Name");

		Textbox fullNameBox = new Textbox();

		fullNameBox.setWidth("100%");
		fullNameBox.setPlaceholder("Enter full name");

		if (editMode) {

			fullNameBox.setValue(existingUser.getFullName());
		}

		mainBox.appendChild(fullNameLabel);
		mainBox.appendChild(fullNameBox);

		// ========================================================
		// PASSWORD
		// ========================================================

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

		// ========================================================
		// ROLE
		// ========================================================

		Label roleLabel = new Label("Role");

		Combobox roleCombo = new Combobox();

		roleCombo.setWidth("100%");
		roleCombo.setReadonly(true);
		roleCombo.setPlaceholder("Select role");

		List<Role> roles = roleService.getAllRoles();

		for (Role role : roles) {

			Comboitem roleItem = new Comboitem();

			// Skip ADMIN — cannot assign admin role via this form
			if ("ADMIN".equalsIgnoreCase(role.getRoleName())) {
				continue;
			}

			roleItem.setLabel(role.getRoleName());

			roleItem.setValue(role.getRoleId());

			roleCombo.appendChild(roleItem);

			if (editMode && existingUser.getRole() != null
					&& existingUser.getRole().getRoleId().equals(role.getRoleId())) {

				roleCombo.setSelectedItem(roleItem);
			}
		}

		mainBox.appendChild(roleLabel);
		mainBox.appendChild(roleCombo);

		// ========================================================
		// BUTTONS
		// ========================================================

		Hbox buttonBox = new Hbox();

		buttonBox.setSpacing("10px");
		buttonBox.setAlign("end");
		buttonBox.setWidth("100%");

		// --------------------------------------------------------
		// CANCEL
		// --------------------------------------------------------

		Button cancelButton = new Button();

		cancelButton.setLabel("Cancel");

		cancelButton.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {

				window.detach();
			}
		});

		// --------------------------------------------------------
		// SAVE / UPDATE
		// --------------------------------------------------------

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

		window.setPage(userListbox.getPage());

		window.doModal();
	}

	// ============================================================
	// SAVE USER
	// ============================================================

	private void saveUser(Window window, User existingUser, Textbox usernameBox, Textbox fullNameBox,
			Textbox passwordBox, Combobox roleCombo) {

		String username = usernameBox.getValue().trim();

		String fullName = fullNameBox.getValue().trim();

		String password = passwordBox.getValue();

		// ========================================================
		// VALIDATION
		// ========================================================

		if (username.isEmpty()) {

			Messagebox.show("Username is required.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		if (fullName.isEmpty()) {

			Messagebox.show("Full name is required.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		if (existingUser == null && (password == null || password.isEmpty())) {

			Messagebox.show("Password is required.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		if (roleCombo.getSelectedItem() == null) {

			Messagebox.show("Please select a role.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);

			return;
		}

		Long roleId = (Long) roleCombo.getSelectedItem().getValue();

		try {

			// ====================================================
			// CREATE
			// ====================================================

			if (existingUser == null) {

				User user = new User();

				user.setUsername(username);
				user.setFullName(fullName);
				user.setPasswordHash(password);

				Role role = new Role();

				role.setRoleId(roleId);

				user.setRole(role);

				user.setStatus("ACTIVE");

				boolean created = userService.createUser(user);

				if (!created) {

					Messagebox.show("Unable to create user. Username may already exist.", "Error", Messagebox.OK,
							Messagebox.ERROR);

					return;
				}

				Messagebox.show("User created successfully.", "Success", Messagebox.OK, Messagebox.INFORMATION);

			}

			// ====================================================
			// UPDATE
			// ====================================================

			else {

				existingUser.setFullName(fullName);

				Role role = new Role();

				role.setRoleId(roleId);

				existingUser.setRole(role);

				if (password != null && !password.isEmpty()) {

					existingUser.setPasswordHash(password);
				}

				userService.updateUser(existingUser);

				Messagebox.show("User updated successfully.", "Success", Messagebox.OK, Messagebox.INFORMATION);
			}

			// ====================================================
			// REFRESH
			// ====================================================

			window.detach();

			int activePage = userPaging.getActivePage();

			userPaging.setTotalSize(userService.getUserCount());

			loadUsers(activePage * PAGE_SIZE);

		} catch (Exception e) {

			e.printStackTrace();

			Messagebox.show("Unable to save user.", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	// ============================================================
	// CHANGE USER STATUS
	// ============================================================

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

		Messagebox.show(

				"Are you sure you want to " + action + " user '" + user.getUsername() + "'?",

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

	// ============================================================
	// DELETE USER
	// ============================================================

	private void confirmDeleteUser(User user) {

		Messagebox.show(

				"Are you sure you want to permanently delete user '" + user.getUsername() + "'?",

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

								// Update count
								userPaging.setTotalSize(userService.getUserCount());

								int activePage = userPaging.getActivePage();

								int offset = activePage * PAGE_SIZE;

								loadUsers(offset);

							} catch (Exception e) {

								e.printStackTrace();

								Messagebox.show("Unable to delete user. User must be In-active to delete", "Error",
										Messagebox.OK, Messagebox.ERROR);
							}
						}
					}
				});
	}
}