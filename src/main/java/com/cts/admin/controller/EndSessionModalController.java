package com.cts.admin.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.cts.admin.model.Session;
import com.cts.admin.service.SessionService;
import com.cts.admin.service.SessionServiceImpl;

public class EndSessionModalController
        extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    private Window endSessionModal;

    private Button modalCloseButton;
    private Button modalCancelButton;
    private Button modalConfirmButton;

    private Label modalQuestion;

    private SessionService sessionService;

    // Temporary admin ID for testing
    private Long currentUserId = 1L;

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        System.out.println("================================");
        System.out.println("END SESSION MODAL CONTROLLER LOADED");
        System.out.println("================================");

        endSessionModal =
                (Window) comp;

        modalCloseButton =
                (Button) comp.getFellow("modalCloseButton");

        modalCancelButton =
                (Button) comp.getFellow("modalCancelButton");

        modalConfirmButton =
                (Button) comp.getFellow("modalConfirmButton");

        modalQuestion =
                (Label) comp.getFellow("modalQuestion");

        sessionService =
                new SessionServiceImpl();

        registerEvents();

        endSessionModal.setVisible(false);
    }

    private void registerEvents() {

        modalCloseButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        closeModal();
                    }
                });

        modalCancelButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
                            throws Exception {

                        closeModal();
                    }
                });

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
    // CLOSE MODAL
    // =========================================================

    private void closeModal() {

        endSessionModal.setVisible(false);
    }

    // =========================================================
    // END SESSION
    // =========================================================

    private void endSession() {

        try {

            Session activeSession =
                    sessionService.getActiveSession();

            if (activeSession == null) {

                closeModal();

                Messagebox.show(
                        "There is no active internal processing session.",
                        "No Active Session",
                        Messagebox.OK,
                        Messagebox.EXCLAMATION);

                return;
            }

            boolean ended =
                    sessionService.endSession(
                            activeSession.getSessionId(),
                            currentUserId);

            closeModal();

            if (ended) {

                Messagebox.show(
                        "Internal processing session ended successfully.",
                        "Session Ended",
                        Messagebox.OK,
                        Messagebox.INFORMATION);

            } else {

                Messagebox.show(
                        "Unable to end the internal processing session.",
                        "Error",
                        Messagebox.OK,
                        Messagebox.ERROR);
            }

        } catch (Exception e) {

            e.printStackTrace();

            closeModal();

            Messagebox.show(
                    "Unable to end the internal processing session.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }
}