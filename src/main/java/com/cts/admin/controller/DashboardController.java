package com.cts.admin.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.cts.admin.model.AuditLog;
import com.cts.admin.service.AuditLogService;
import com.cts.admin.service.AuditLogServiceImpl;

public class DashboardController
        extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    private static final String PAGE_AUDIT_LOGS = "/admin/audit-logs.zul";
    private static final int    RECENT_LOG_COUNT = 5;

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    /* ------------------------------------------------------------------ */
    /* ZUL COMPONENTS — auto-wired by id                                   */
    /* ------------------------------------------------------------------ */

    private Button  viewAuditLogsBtn;
    private Listbox recentAuditListbox;

    /* ------------------------------------------------------------------ */
    /* SERVICES                                                            */
    /* ------------------------------------------------------------------ */

    private AuditLogService auditLogService;

    /* ------------------------------------------------------------------ */
    /* LIFECYCLE                                                           */
    /* ------------------------------------------------------------------ */

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        System.out.println("DashboardController loaded.");

        auditLogService = new AuditLogServiceImpl();

        /* Wire "View Full Audit Logs" button */
        viewAuditLogsBtn.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event e) throws Exception {
                        navigateToAuditLogs();
                    }
                });

        /* Load top 5 audit logs */
        loadRecentAuditLogs();
    }

    /* ------------------------------------------------------------------ */
    /* NAVIGATE TO AUDIT LOGS PAGE                                         */
    /* Finds the shell's contentArea Include component and swaps the src. */
    /* ------------------------------------------------------------------ */

    private void navigateToAuditLogs() {

        try {

            /*
             * The dashboard is included inside index.zul via
             *   <include id="contentArea" .../>
             * Walk up the component tree to find the Include.
             */
            Component parent = viewAuditLogsBtn.getParent();

            while (parent != null) {

                if (parent instanceof Include) {
                    ((Include) parent).setSrc(PAGE_AUDIT_LOGS);
                    return;
                }

                parent = parent.getParent();
            }

            /*
             * Fallback: search the desktop for contentArea by id.
             */
            Component contentArea =
                    viewAuditLogsBtn.getPage().getFellow("contentArea");

            if (contentArea instanceof Include) {
                ((Include) contentArea).setSrc(PAGE_AUDIT_LOGS);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(
                    "Unable to navigate to Audit Logs.",
                    "Navigation Error",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    /* ------------------------------------------------------------------ */
    /* LOAD RECENT AUDIT LOGS                                              */
    /* Top 5 entries ordered by event_time DESC                           */
    /* ------------------------------------------------------------------ */

    private void loadRecentAuditLogs() {

        try {

            List<AuditLog> logs =
                    auditLogService.getAuditLogs(1, RECENT_LOG_COUNT);

            recentAuditListbox.getItems().clear();

            for (AuditLog log : logs) {

                Listitem item = new Listitem();

                /* User ID */
                Listcell userIdCell = new Listcell();
                Label userIdLabel = new Label(
                        log.getUserId() == null ? "-" : String.valueOf(log.getUserId()));
                userIdLabel.setSclass("audit-user-label");
                userIdCell.appendChild(userIdLabel);
                item.appendChild(userIdCell);

                /* Role */
                Listcell roleCell = new Listcell();
                Label roleLabel = new Label(
                        log.getModule() == null ? "-" : log.getModule());
                roleLabel.setSclass("audit-module-label");
                roleCell.appendChild(roleLabel);
                item.appendChild(roleCell);

                /* Login */
                Listcell loginCell = new Listcell();
                Label loginLabel = new Label(
                        log.getEventTime() == null
                                ? "-"
                                : DATE_FMT.format(log.getEventTime()));
                loginLabel.setSclass("audit-datetime-label");
                loginCell.appendChild(loginLabel);
                item.appendChild(loginCell);

                /* Logout */
                Listcell logoutCell = new Listcell();
                Label logoutLabel = new Label(
                        log.getEventDate() == null
                                ? "-"
                                : log.getEventDate().toString());
                logoutLabel.setSclass("audit-datetime-label");
                logoutCell.appendChild(logoutLabel);
                item.appendChild(logoutCell);

                item.setValue(log);
                recentAuditListbox.appendChild(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(
                    "Unable to load recent audit logs.",
                    "Dashboard",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    /* ------------------------------------------------------------------ */
    /* ACTION BADGE BUILDER                                                */
    /* ------------------------------------------------------------------ */

    private Listcell buildActionBadge(String action) {

        Listcell cell = new Listcell();

        if (action == null || action.trim().isEmpty()) {
            cell.appendChild(new Label("-"));
            return cell;
        }

        Hbox badge = new Hbox();
        badge.setAlign("center");

        String upper = action.trim().toUpperCase();

        if (upper.contains("LOGIN") || upper.contains("STARTED")) {
            badge.setSclass("audit-action-badge audit-action-green");
        } else if (upper.contains("LOGOUT") || upper.contains("FAILED")
                || upper.contains("REJECTED")) {
            badge.setSclass("audit-action-badge audit-action-red");
        } else if (upper.contains("LOCKED") || upper.contains("PENDING")) {
            badge.setSclass("audit-action-badge audit-action-amber");
        } else if (upper.contains("PROCESSING") || upper.contains("COMPLETED")) {
            badge.setSclass("audit-action-badge audit-action-blue");
        } else {
            badge.setSclass("audit-action-badge audit-action-grey");
        }

        Label lbl = new Label(action);
        lbl.setSclass("audit-action-label");
        badge.appendChild(lbl);
        cell.appendChild(badge);

        return cell;
    }
}
