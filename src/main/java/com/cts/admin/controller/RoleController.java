package com.cts.admin.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.cts.admin.model.Role;
import com.cts.admin.service.RoleService;
import com.cts.admin.service.RoleServiceImpl;

public class RoleController extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    private Listbox roleListbox;

    private RoleService roleService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        System.out.println("RoleController loaded");

        roleService = new RoleServiceImpl();

        loadRoles();
    }

    private void loadRoles() {

        try {

            List<Role> roles = roleService.getAllRoles();

            roleListbox.getItems().clear();

            if (roles == null || roles.isEmpty()) {
                return;
            }

            for (Role role : roles) {

                Listitem item = new Listitem();

                /*
                 * Role Name
                 */
                Listcell roleNameCell = new Listcell();

                Label roleName =
                        new Label(role.getRoleName());

                roleName.setSclass("role-name");

                roleNameCell.appendChild(roleName);
                item.appendChild(roleNameCell);

                /*
                 * Description
                 */
                Listcell descriptionCell = new Listcell();

                Label description =
                        new Label(
                            role.getDescription() == null
                                ? ""
                                : role.getDescription()
                        );

                description.setSclass("role-description");

                descriptionCell.appendChild(description);
                item.appendChild(descriptionCell);

                /*
                 * Status
                 */
                Listcell statusCell = new Listcell();

                Label status =
                        new Label(
                            role.getStatus() == null
                                ? ""
                                : role.getStatus()
                        );

                status.setSclass("role-status");

                statusCell.appendChild(status);
                item.appendChild(statusCell);

                /*
                 * Created Date
                 */
                Listcell createdDateCell = new Listcell();

                String formattedDate = "";

                if (role.getCreatedAt() != null) {

                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("dd-MM-yyyy");

                    formattedDate =
                            dateFormat.format(role.getCreatedAt());
                }

                Label createdDate =
                        new Label(formattedDate);

                createdDate.setSclass("role-created-date");

                createdDateCell.appendChild(createdDate);
                item.appendChild(createdDateCell);

                /*
                 * Keep the complete Role object
                 * attached to this row.
                 */
                item.setValue(role);

                roleListbox.appendChild(item);
            }

        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to load system roles.",
                    "Role Management",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }
}