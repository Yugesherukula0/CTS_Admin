package com.cts.admin.controller;

import java.text.SimpleDateFormat;
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

    private Window endSessionModal;

    private Button modalCloseButton;
    private Button modalCancelButton;
    private Button modalConfirmButton;

    private Label modalQuestion;

    private final SessionService sessionService;

    /*
     * Login is not implemented.
     * Temporary admin ID for testing.
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

        System.out.println(
                "SessionManagementController loaded."
        );

        /*
         * Explicit event wiring.
         *
         * This avoids depending on
         * onClick$componentId naming.
         */

        beginSessionButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {
                	

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        startSession();
                    }
                }
        );


        endSessionButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        showEndSessionConfirmation();
                    }
                }
        );


        modalCloseButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        closeEndSessionModal();
                    }
                }
        );


        modalCancelButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        closeEndSessionModal();
                    }
                }
        );


        modalConfirmButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        endSession();
                    }
                }
        );


        /*
         * Load current GLOBAL session from DB.
         */
        loadSessionState();


        /*
         * Load session history.
         */
        loadSessionHistory();
    }


    /*
     * ============================================================
     * LOAD SESSION STATE
     * ============================================================
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


    /*
     * ============================================================
     * CLOSED STATE
     * ============================================================
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


    /*
     * ============================================================
     * ACTIVE STATE
     * ============================================================
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


    /*
     * ============================================================
     * START SESSION
     * ============================================================
     */

    private void startSession() {

        System.out.println(
                "Begin Session button clicked."
        );

        try {

            /*
             * Always check the database.
             *
             * The session is GLOBAL.
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


            /*
             * Create GLOBAL ACTIVE session.
             */

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

            e.printStackTrace();

            Messagebox.show(
                    "Unable to start internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }


    /*
     * ============================================================
     * SHOW END SESSION CONFIRMATION
     * ============================================================
     */

    private void showEndSessionConfirmation() {

        System.out.println(
                "End Session button clicked."
        );


        /*
         * Always get the latest active session.
         */

        activeSession =
                sessionService.getActiveSession();


        if (activeSession == null) {

            loadSessionState();

            Messagebox.show(
                    "No active internal processing session exists.",
                    "Session",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        modalQuestion.setValue(
                "Are you sure you want to end Session "
                + activeSession.getSessionId()
                + "?"
        );


        endSessionModal.setVisible(true);
    }


    /*
     * ============================================================
     * CLOSE MODAL
     * ============================================================
     */

    private void closeEndSessionModal() {

        endSessionModal.setVisible(false);
    }


    /*
     * ============================================================
     * END SESSION
     * ============================================================
     */

    private void endSession() {

        System.out.println(
                "Confirm End Session button clicked."
        );


        /*
         * Get the current GLOBAL active session again.
         */

        activeSession =
                sessionService.getActiveSession();


        if (activeSession == null) {

            endSessionModal.setVisible(false);

            loadSessionState();

            Messagebox.show(
                    "No active internal processing session exists.",
                    "Session",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        try {

            Long sessionId =
                    activeSession.getSessionId();


            boolean ended =
                    sessionService.endSession(
                            sessionId,
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
                 * Database should now contain:
                 *
                 * status = ENDED
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

            e.printStackTrace();

            endSessionModal.setVisible(false);

            Messagebox.show(
                    "Unable to end internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }


    /*
     * ============================================================
     * SESSION HISTORY
     * ============================================================
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

            statusCell.appendChild(
                    new Label(
                            session.getStatus()
                    )
            );

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