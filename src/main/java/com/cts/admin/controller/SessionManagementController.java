package com.cts.admin.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.cts.admin.model.Session;
import com.cts.admin.service.SessionService;
import com.cts.admin.service.SessionServiceImpl;

public class SessionManagementController
        extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    private Label sessionStatusBadge;

    private Component closedSessionContent;
    private Component activeSessionContent;

    private Label activeSessionName;
    private Label activeSessionId;
    private Label activeSessionStartedAt;
    private Label activeSessionStartedBy;

    private Button beginSessionButton;
    private Button endSessionButton;

    private Listbox sessionHistoryListbox;

    private Window endSessionModal;
    private Button modalCloseButton;
    private Button modalCancelButton;
    private Button modalConfirmButton;

    private Label modalQuestion;

    private final SessionService sessionService;

    /*
     * Current logged-in admin.
     *
     * Replace this later with your actual
     * logged-in user/admin ID source.
     */
    private Long currentUserId = 1L;

    private Session activeSession;

    public SessionManagementController() {

        sessionService = new SessionServiceImpl();
    }

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        loadSessionState();

        loadSessionHistory();
    }

    /**
     * Loads the current global internal session
     * from the database.
     */
    private void loadSessionState() {

        activeSession =
                sessionService.getActiveSession();

        if (activeSession == null) {

            showClosedState();

        } else {

            showActiveState();
        }
    }

    /**
     * Shows Start Session state.
     */
    private void showClosedState() {

        sessionStatusBadge.setValue(
                "NO ACTIVE SESSION"
        );

        sessionStatusBadge.setSclass(
                "status-badge status-inactive"
        );

        closedSessionContent.setVisible(true);

        activeSessionContent.setVisible(false);
    }

    /**
     * Shows End Session state.
     */
    private void showActiveState() {

        sessionStatusBadge.setValue(
                "ACTIVE"
        );

        sessionStatusBadge.setSclass(
                "status-badge status-active"
        );

        closedSessionContent.setVisible(false);

        activeSessionContent.setVisible(true);

        activeSessionName.setValue(
                "Clearing Session Active"
        );

        activeSessionId.setValue(
                "Session ID: "
                        + activeSession.getSessionId()
        );

        activeSessionStartedAt.setValue(
                "Started At: "
                        + formatTimestamp(
                                activeSession.getStartedAt()
                        )
        );

        activeSessionStartedBy.setValue(
                "Started By: Admin "
                        + activeSession.getStartedBy()
        );
    }

    /**
     * Start internal processing session.
     */
    public void onClick$beginSessionButton() {

        try {

            /*
             * Double-check from database.
             *
             * This prevents starting another session
             * if one became active after page load.
             */
            Session existingSession =
                    sessionService.getActiveSession();

            if (existingSession != null) {

                Messagebox.show(
                        "An internal processing session is already active.",
                        "Session Already Active",
                        Messagebox.OK,
                        Messagebox.EXCLAMATION
                );

                loadSessionState();

                return;
            }

            boolean started =
                    sessionService.startSession(
                            currentUserId
                    );

            if (started) {

                Messagebox.show(
                        "Internal processing session started successfully.",
                        "Session Started",
                        Messagebox.OK,
                        Messagebox.INFORMATION
                );

                loadSessionState();

                loadSessionHistory();
            }

        } catch (IllegalStateException e) {

            Messagebox.show(
                    e.getMessage(),
                    "Session",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            loadSessionState();

        } catch (Exception e) {

            Messagebox.show(
                    "Unable to start internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    /**
     * Open End Session confirmation modal.
     */
    public void onClick$endSessionButton() {

        if (activeSession == null) {

            loadSessionState();

            return;
        }

        modalQuestion.setValue(
                "Are you sure you want to end Session "
                        + activeSession.getSessionId()
                        + "?"
        );

        endSessionModal.setVisible(true);
    }

    /**
     * Close confirmation modal.
     */
    public void onClick$modalCloseButton() {

        endSessionModal.setVisible(false);
    }

    /**
     * Cancel ending session.
     */
    public void onClick$modalCancelButton() {

        endSessionModal.setVisible(false);
    }

    /**
     * Confirm and end current internal session.
     */
    public void onClick$modalConfirmButton() {

        if (activeSession == null) {

            endSessionModal.setVisible(false);

            loadSessionState();

            return;
        }

        try {

            boolean ended =
                    sessionService.endSession(
                            activeSession.getSessionId(),
                            currentUserId
                    );

            if (ended) {

                endSessionModal.setVisible(false);

                Messagebox.show(
                        "Internal processing session ended successfully.",
                        "Session Ended",
                        Messagebox.OK,
                        Messagebox.INFORMATION
                );

                /*
                 * Reload from DB.
                 */
                loadSessionState();

                loadSessionHistory();
            }

        } catch (IllegalStateException e) {

            endSessionModal.setVisible(false);

            Messagebox.show(
                    e.getMessage(),
                    "Session",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            loadSessionState();

        } catch (Exception e) {

            endSessionModal.setVisible(false);

            Messagebox.show(
                    "Unable to end internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    /**
     * Loads complete session history.
     */
    private void loadSessionHistory() {

        sessionHistoryListbox.getItems().clear();

        List<Session> sessions =
                sessionService.getAllSessions();

        for (Session session : sessions) {

            Listitem item =
                    new Listitem();

            /*
             * Session ID
             */
            Listcell sessionIdCell =
                    new Listcell();

            sessionIdCell.appendChild(
                    new Label(
                            String.valueOf(
                                    session.getSessionId()
                            )
                    )
            );

            item.appendChild(sessionIdCell);

            /*
             * Session Name
             */
            Listcell sessionNameCell =
                    new Listcell();

            sessionNameCell.appendChild(
                    new Label(
                            "Clearing Session"
                    )
            );

            item.appendChild(sessionNameCell);

            /*
             * Session Type
             */
            Listcell sessionTypeCell =
                    new Listcell();

            sessionTypeCell.appendChild(
                    new Label(
                            "Internal Processing"
                    )
            );

            item.appendChild(sessionTypeCell);

            /*
             * Start Date
             */
            Listcell startDateCell =
                    new Listcell();

            startDateCell.appendChild(
                    new Label(
                            formatDate(
                                    session.getStartedAt()
                            )
                    )
            );

            item.appendChild(startDateCell);

            /*
             * Start Time
             */
            Listcell startTimeCell =
                    new Listcell();

            startTimeCell.appendChild(
                    new Label(
                            formatTime(
                                    session.getStartedAt()
                            )
                    )
            );

            item.appendChild(startTimeCell);

            /*
             * End Time
             */
            Listcell endTimeCell =
                    new Listcell();

            endTimeCell.appendChild(
                    new Label(
                            session.getEndedAt() == null
                                    ? "-"
                                    : formatTime(
                                            session.getEndedAt()
                                    )
                    )
            );

            item.appendChild(endTimeCell);

            /*
             * Status
             */
            Listcell statusCell =
                    new Listcell();

            Label statusLabel =
                    new Label(
                            session.getStatus()
                    );

            statusCell.appendChild(statusLabel);

            item.appendChild(statusCell);

            /*
             * Started By
             */
            Listcell startedByCell =
                    new Listcell();

            startedByCell.appendChild(
                    new Label(
                            session.getStartedBy() == null
                                    ? "-"
                                    : "Admin "
                                    + session.getStartedBy()
                    )
            );

            item.appendChild(startedByCell);

            /*
             * Ended By
             */
            Listcell endedByCell =
                    new Listcell();

            endedByCell.appendChild(
                    new Label(
                            session.getEndedBy() == null
                                    ? "-"
                                    : "Admin "
                                    + session.getEndedBy()
                    )
            );

            item.appendChild(endedByCell);

            sessionHistoryListbox.appendChild(item);
        }
    }

    private String formatTimestamp(
            java.sql.Timestamp timestamp) {

        if (timestamp == null) {
            return "-";
        }

        return new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a"
        ).format(timestamp);
    }

    private String formatDate(
            java.sql.Timestamp timestamp) {

        if (timestamp == null) {
            return "-";
        }

        return new SimpleDateFormat(
                "dd MMM yyyy"
        ).format(timestamp);
    }

    private String formatTime(
            java.sql.Timestamp timestamp) {

        if (timestamp == null) {
            return "-";
        }

        return new SimpleDateFormat(
                "hh:mm a"
        ).format(timestamp);
    }
}