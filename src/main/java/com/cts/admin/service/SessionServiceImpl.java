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
                    "User ID cannot be null.");
        }

        // Check whether an internal session is already active
        Session activeSession =
                sessionDAO.getActiveSession();

        if (activeSession != null) {
            throw new IllegalStateException(
                    "An internal processing session is already active.");
        }

        return sessionDAO.startSession(userId);
    }

    @Override
    public boolean endSession(Long sessionId, Long userId) {

        if (sessionId == null) {
            throw new IllegalArgumentException(
                    "Session ID cannot be null.");
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null.");
        }

        // Get the currently active internal session
        Session activeSession =
                sessionDAO.getActiveSession();

        if (activeSession == null) {
            throw new IllegalStateException(
                    "No active internal processing session exists.");
        }

        // Make sure the requested session is the active session
        if (!activeSession.getSessionId().equals(sessionId)) {
            throw new IllegalStateException(
                    "The session is not the active processing session.");
        }

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