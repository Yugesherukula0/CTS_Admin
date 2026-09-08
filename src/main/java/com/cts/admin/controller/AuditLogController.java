package com.cts.admin.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.cts.admin.model.AuditLog;
import com.cts.admin.service.AuditLogService;
import com.cts.admin.service.AuditLogServiceImpl;

public class AuditLogController
        extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    private Listbox auditLogListbox;

    private Button previousButton;
    private Button nextButton;

    private Label pageLabel;

    private AuditLogService auditLogService;

    private int currentPage = 1;

    private final int pageSize = 10;

    private int totalRecords;

    private int totalPages;

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        auditLogService = new AuditLogServiceImpl();

        loadAuditLogs();
    }

    private void loadAuditLogs() {

        totalRecords =
                auditLogService.getTotalAuditLogCount();

        if (totalRecords == 0) {

            totalPages = 0;

            auditLogListbox.getItems().clear();

            updatePagination();

            return;
        }

        totalPages =
                (int) Math.ceil(
                        (double) totalRecords / pageSize
                );

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        List<AuditLog> auditLogs =
                auditLogService.getAuditLogs(
                        currentPage,
                        pageSize
                );

        auditLogListbox.getItems().clear();

        for (AuditLog auditLog : auditLogs) {

            Listitem item =
                    new Listitem();

            /* User ID */
            item.appendChild(
                    createCell(
                            auditLog.getUserId() != null
                                    ? String.valueOf(auditLog.getUserId())
                                    : "-"
                    )
            );

            /* Role */
            item.appendChild(
                    createCell(
                            auditLog.getModule()
                    )
            );

            /* Login */
            item.appendChild(
                    createCell(
                            auditLog.getEventTime() != null
                                    ? auditLog.getEventTime().toString()
                                    : "-"
                    )
            );

            /* Logout */
            item.appendChild(
                    createCell(
                            auditLog.getEventDate() != null
                                    ? auditLog.getEventDate().toString()
                                    : "-"
                    )
            );

            auditLogListbox.appendChild(item);
        }

        updatePagination();
    }

    private Listcell createCell(String value) {

        Listcell cell = new Listcell();

        cell.setLabel(
                value != null ? value : "-"
        );

        return cell;
    }

    public void onClick$previousButton() {

        if (currentPage > 1) {

            currentPage--;

            loadAuditLogs();
        }
    }

    public void onClick$nextButton() {

        if (currentPage < totalPages) {

            currentPage++;

            loadAuditLogs();
        }
    }

    private void updatePagination() {

        if (totalPages == 0) {

            pageLabel.setValue(
                    "Page 0 of 0"
            );

            previousButton.setDisabled(true);
            nextButton.setDisabled(true);

            return;
        }

        pageLabel.setValue(
                "Page "
                + currentPage
                + " of "
                + totalPages
        );

        previousButton.setDisabled(
                currentPage <= 1
        );

        nextButton.setDisabled(
                currentPage >= totalPages
        );
    }
}
