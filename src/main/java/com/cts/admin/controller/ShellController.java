package com.cts.admin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

/**
 * ShellController
 *
 * Manages the top-level shell layout:
 *   - Populates header date
 *   - Handles sidebar nav item clicks
 *   - Swaps the <include> content area to the selected page
 *
 * Default page on load: Role Management
 */
public class ShellController extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    /*
     * Page sources
     */
    private static final String PAGE_DASHBOARD         = "/admin/dashboard.zul";
    private static final String PAGE_ROLES             = "/admin/roles.zul";
    private static final String PAGE_USERS             = "/admin/user.zul";
    private static final String PAGE_BATCH_MONITORING  = "/admin/batch-monitoring.zul";
    private static final String PAGE_SESSION_MGMT      = "/admin/session-management.zul";
    private static final String PAGE_AUDIT_LOGS        = "/admin/audit-logs.zul";

    /*
     * Header components — auto-wired by id
     */
    private Label   headerDate;
    private Label   headerLastLogin;

    /*
     * Sidebar nav items — auto-wired by id
     */
    private Hbox navDashboard;
    private Hbox navRoles;
    private Hbox navUsers;
    private Hbox navBatch;
    private Hbox navSession;
    private Hbox navAudit;

    /*
     * Dynamic content area — auto-wired by id
     */
    private Include contentArea;

    /*
     * Track the currently active nav item
     * so we can remove its active style when switching
     */
    private Hbox activeNavItem;

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        /*
         * Set today's date in the header
         */
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        headerDate.setValue("Date : " + today);

        /*
         * Wire click listeners to every nav item
         */
        wireNavItem(navDashboard,  PAGE_DASHBOARD);
        wireNavItem(navRoles,      PAGE_ROLES);
        wireNavItem(navUsers,      PAGE_USERS);
        wireNavItem(navBatch,      PAGE_BATCH_MONITORING);
        wireNavItem(navSession,    PAGE_SESSION_MGMT);
        wireNavItem(navAudit,      PAGE_AUDIT_LOGS);

        /*
         * Default active item on load = Role Management
         * (matches the default include src in index.zul)
         */
        setActiveNav(navRoles);
    }

    /**
     * Attaches an onClick listener to a nav item hbox.
     * On click: marks it active, loads the target page into contentArea.
     */
    private void wireNavItem(Hbox navItem, String pageSrc) {

        navItem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {

                setActiveNav(navItem);
                contentArea.setSrc(pageSrc);
            }
        });
    }

    /**
     * Applies the active CSS class to the selected nav item
     * and removes it from the previously active one.
     */
    private void setActiveNav(Hbox selected) {

        /*
         * Remove active style from previous item
         */
        if (activeNavItem != null) {
            activeNavItem.setSclass("nav-item");
        }

        /*
         * Apply active style to new item
         */
        selected.setSclass("nav-item nav-item-active");

        activeNavItem = selected;
    }
}
