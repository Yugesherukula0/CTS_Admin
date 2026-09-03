package com.cts.admin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.cts.admin.model.Session;
import com.cts.admin.service.SessionService;
import com.cts.admin.service.SessionServiceImpl;

public class SessionManagementController
        extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // PAGE COMPONENTS
    // =========================================================

    private Vlayout currentSessionCard;

    private Label sessionStatusBadge;

    private Vlayout closedSessionContent;

    private Vlayout activeSessionContent;

    private Label activeSessionName;

    private Label activeSessionId;

    private Label activeSessionStartedAt;

    private Label activeSessionStartedBy;

    private Button beginSessionButton;

    private Button endSessionButton;

    private Listbox sessionHistoryListbox;

    // =========================================================
    // END SESSION MODAL
    // =========================================================

    private Window endSessionModal;

    private Button modalCloseButton;

    private Button modalCancelButton;

    private Button modalConfirmButton;

    // =========================================================
    // SERVICE
    // =========================================================

    private SessionService sessionService;

    // Temporary admin ID for testing
    private Long currentUserId = 1L;

    // =========================================================
    // COMPOSE
    // =========================================================

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        System.out.println("================================");
        System.out.println("SESSION CONTROLLER LOADED");
        System.out.println("================================");

        sessionService = new SessionServiceImpl();

        // =====================================================
        // PAGE COMPONENTS
        // =====================================================

        currentSessionCard =
                (Vlayout) comp.getFellow("currentSessionCard");

        sessionStatusBadge =
                (Label) comp.getFellow("sessionStatusBadge");

        closedSessionContent =
                (Vlayout) comp.getFellow("closedSessionContent");

        activeSessionContent =
                (Vlayout) comp.getFellow("activeSessionContent");

        activeSessionName =
                (Label) comp.getFellow("activeSessionName");

        activeSessionId =
                (Label) comp.getFellow("activeSessionId");

        activeSessionStartedAt =
                (Label) comp.getFellow("activeSessionStartedAt");

        activeSessionStartedBy =
                (Label) comp.getFellow("activeSessionStartedBy");

        beginSessionButton =
                (Button) comp.getFellow("beginSessionButton");

        endSessionButton =
                (Button) comp.getFellow("endSessionButton");

        sessionHistoryListbox =
                (Listbox) comp.getFellow("sessionHistoryListbox");

        // =====================================================
        // END SESSION MODAL
        // =====================================================

        endSessionModal =
                (Window) comp.getFellow("endSessionModal");

        modalCloseButton =
                (Button) endSessionModal
                        .getFellow("modalCloseButton");

        modalCancelButton =
                (Button) endSessionModal
                        .getFellow("modalCancelButton");

        modalConfirmButton =
                (Button) endSessionModal
                        .getFellow("modalConfirmButton");

        System.out.println(
                "SUCCESS: endSessionModal found."
        );

        // =====================================================
        // REGISTER EVENTS
        // =====================================================

        registerEvents();

        // =====================================================
        // LOAD SESSION
        // =====================================================

        loadSessionState();

        loadSessionHistory();
    }

    // =========================================================
    // REGISTER EVENTS
    // =========================================================

    private void registerEvents() {

        // =====================================================
        // BEGIN SESSION
        // =====================================================

        beginSessionButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        startSession();
                    }
                });

        // =====================================================
        // END SESSION
        // =====================================================

        endSessionButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        openEndSessionModal();
                    }
                });

        // =====================================================
        // MODAL CLOSE
        // =====================================================

        modalCloseButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        closeEndSessionModal();
                    }
                });

        // =====================================================
        // MODAL CANCEL
        // =====================================================

        modalCancelButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        closeEndSessionModal();
                    }
                });

        // =====================================================
        // MODAL CONFIRM
        // =====================================================

        modalConfirmButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        endSession();
                    }
                });
    }

    // =========================================================
    // START SESSION
    // =========================================================

    private void startSession() {

        try {

            sessionService.startSession(currentUserId);

            Messagebox.show(
                    "Internal processing session started successfully.",
                    "Session Started",
                    Messagebox.OK,
                    Messagebox.INFORMATION
            );

            loadSessionState();

            loadSessionHistory();

        } catch (IllegalStateException e) {

            Messagebox.show(
                    e.getMessage(),
                    "Session Already Active",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            loadSessionState();

        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to start the internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    // =========================================================
    // OPEN END SESSION MODAL
    // =========================================================

    private void openEndSessionModal() {

        Session activeSession =
                sessionService.getActiveSession();

        if (activeSession == null) {

            Messagebox.show(
                    "There is no active internal processing session.",
                    "No Active Session",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            loadSessionState();

            return;
        }

        System.out.println(
                "Opening End Session Modal"
        );

        System.out.println(
                "Active Session ID: "
                + activeSession.getSessionId()
        );

        endSessionModal.setVisible(true);

        endSessionModal.doModal();
    }

    // =========================================================
    // CLOSE END SESSION MODAL
    // =========================================================

    private void closeEndSessionModal() {

        if (endSessionModal != null) {

            endSessionModal.setVisible(false);
        }
    }

    // =========================================================
    // END SESSION
    // =========================================================

    private void endSession() {

        System.out.println("================================");
        System.out.println("CONFIRM END SESSION CLICKED");
        System.out.println("================================");

        try {

            // =================================================
            // GET GLOBAL ACTIVE SESSION
            // =================================================

            Session activeSession =
                    sessionService.getActiveSession();

            if (activeSession == null) {

                closeEndSessionModal();

                Messagebox.show(
                        "There is no active internal processing session.",
                        "No Active Session",
                        Messagebox.OK,
                        Messagebox.EXCLAMATION
                );

                loadSessionState();

                loadSessionHistory();

                return;
            }

            System.out.println(
                    "Active Session ID: "
                    + activeSession.getSessionId()
            );

            // =================================================
            // END SESSION
            // =================================================

            boolean ended =
                    sessionService.endSession(
                            activeSession.getSessionId(),
                            currentUserId
                    );

            // =================================================
            // CLOSE MODAL
            // =================================================

            closeEndSessionModal();

            // =================================================
            // RESULT
            // =================================================

            if (ended) {

                System.out.println(
                        "SESSION ENDED SUCCESSFULLY"
                );

                Messagebox.show(
                        "Internal processing session ended successfully.",
                        "Session Ended",
                        Messagebox.OK,
                        Messagebox.INFORMATION
                );

                // Refresh current state
                loadSessionState();

                // Refresh history
                loadSessionHistory();

            } else {

                System.out.println(
                        "SESSION WAS NOT ENDED"
                );

                Messagebox.show(
                        "Unable to end the internal processing session.",
                        "Error",
                        Messagebox.OK,
                        Messagebox.ERROR
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "ERROR WHILE ENDING SESSION"
            );

            e.printStackTrace();

            closeEndSessionModal();

            Messagebox.show(
                    "Unable to end the internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    // =========================================================
    // LOAD CURRENT SESSION
    // =========================================================

    private void loadSessionState() {

        Session activeSession =
                sessionService.getActiveSession();

        // =====================================================
        // NO ACTIVE SESSION
        // =====================================================

        if (activeSession == null) {

            closedSessionContent.setVisible(true);

            activeSessionContent.setVisible(false);

            sessionStatusBadge.setValue(
                    "NO ACTIVE SESSION"
            );

            sessionStatusBadge.setSclass(
                    "status-badge status-inactive"
            );

            return;
        }

        // =====================================================
        // ACTIVE SESSION
        // =====================================================

        closedSessionContent.setVisible(false);

        activeSessionContent.setVisible(true);

        sessionStatusBadge.setValue(
                "ACTIVE SESSION"
        );

        sessionStatusBadge.setSclass(
                "status-badge status-active"
        );

        activeSessionName.setValue(
                "Clearing Session"
        );

        activeSessionId.setValue(
                "Session ID : "
                + activeSession.getSessionId()
        );

        // =====================================================
        // STARTED AT
        // =====================================================

        if (activeSession.getStartedAt() != null) {

            activeSessionStartedAt.setValue(
                    "Started At : "
                    + formatDateTime(
                            activeSession.getStartedAt()
                    )
            );

        } else {

            activeSessionStartedAt.setValue(
                    "Started At : -"
            );
        }

        // =====================================================
        // STARTED BY
        // =====================================================

        if (activeSession.getStartedBy() != null) {

            activeSessionStartedBy.setValue(
                    "Started By : Admin "
                    + activeSession.getStartedBy()
            );

        } else {

            activeSessionStartedBy.setValue(
                    "Started By : -"
            );
        }
    }

    // =========================================================
    // LOAD SESSION HISTORY
    // =========================================================

    private void loadSessionHistory() {

        sessionHistoryListbox.getItems().clear();

        List<Session> sessions =
                sessionService.getAllSessions();

        if (sessions == null
                || sessions.isEmpty()) {

            return;
        }

        for (Session session : sessions) {

            Listitem item =
                    new Listitem();

            // Session ID
            item.appendChild(
                    createCell(
                            session.getSessionId() != null
                                    ? String.valueOf(
                                            session.getSessionId())
                                    : "-"
                    )
            );

            // Start Date
            item.appendChild(
                    createCell(
                            session.getStartedAt() != null
                                    ? formatDate(
                                            session.getStartedAt())
                                    : "-"
                    )
            );

            // Start Time
            item.appendChild(
                    createCell(
                            session.getStartedAt() != null
                                    ? formatTime(
                                            session.getStartedAt())
                                    : "-"
                    )
            );

            // End Time
            item.appendChild(
                    createCell(
                            session.getEndedAt() != null
                                    ? formatTime(
                                            session.getEndedAt())
                                    : "-"
                    )
            );

            // Status
            item.appendChild(
                    createCell(
                            session.getStatus() != null
                                    ? session.getStatus()
                                    : "-"
                    )
            );

            // Started By
            item.appendChild(
                    createCell(
                            session.getStartedBy() != null
                                    ? "Admin "
                                      + session.getStartedBy()
                                    : "-"
                    )
            );

            // Ended By
            item.appendChild(
                    createCell(
                            session.getEndedBy() != null
                                    ? "Admin "
                                      + session.getEndedBy()
                                    : "-"
                    )
            );

            sessionHistoryListbox.appendChild(item);
        }
    }

    // =========================================================
    // CREATE CELL
    // =========================================================

    private Listcell createCell(String value) {

        Listcell cell =
                new Listcell();

        Label label =
                new Label();

        label.setValue(value);

        cell.appendChild(label);

        return cell;
    }

    // =========================================================
    // DATE FORMAT
    // =========================================================

    private String formatDate(Date date) {

        return new SimpleDateFormat(
                "dd MMM yyyy"
        ).format(date);
    }

    // =========================================================
    // TIME FORMAT
    // =========================================================

    private String formatTime(Date date) {

        return new SimpleDateFormat(
                "hh:mm a"
        ).format(date);
    }

    // =========================================================
    // DATE + TIME FORMAT
    // =========================================================

    private String formatDateTime(Date date) {

        return new SimpleDateFormat(
                "dd MMM yyyy hh:mm a"
        ).format(date);
    }
}