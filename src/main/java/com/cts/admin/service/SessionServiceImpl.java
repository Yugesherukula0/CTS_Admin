package com.cts.admin.service;

import java.util.List;

import com.cts.admin.dao.SessionDAO;
import com.cts.admin.dao.SessionDAOImpl;
import com.cts.admin.model.Session;

public class SessionServiceImpl implements SessionService {

    private final SessionDAO sessionDAO;

    public SessionServiceImpl() {

        sessionDAO = new SessionDAOImpl();
    }


    @Override
    public boolean startSession(Long userId) {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID cannot be null."
            );
        }


        /*
         * GLOBAL SESSION RULE:
         *
         * Only one ACTIVE session can exist.
         */

        Session activeSession =
                sessionDAO.getActiveSession();


        if (activeSession != null) {

            throw new IllegalStateException(
                    "An internal processing session is already active."
            );
        }


        return sessionDAO.startSession(userId);
    }


    @Override
    public boolean endSession(
            Long sessionId,
            Long userId) {

        if (sessionId == null) {

            throw new IllegalArgumentException(
                    "Session ID cannot be null."
            );
        }


        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID cannot be null."
            );
        }


        /*
         * Get the GLOBAL active session.
         */

        Session activeSession =
                sessionDAO.getActiveSession();


        if (activeSession == null) {

            throw new IllegalStateException(
                    "No active internal processing session exists."
            );
        }


        /*
         * Make sure the requested session is
         * actually the current active session.
         */

        if (!activeSession.getSessionId().equals(sessionId)) {

            throw new IllegalStateException(
                    "The session is not the active processing session."
            );
        }


        /*
         * End the global session.
         *
         * userId is only recorded as ended_by.
         */
        return sessionDAO.endSession(
                sessionId,
                userId
        );
    }


    @Override
    public Session getActiveSession() {

        return sessionDAO.getActiveSession();
    }


    @Override
    public List<Session> getAllSessions() {

        return sessionDAO.getAllSessions();
    }
}