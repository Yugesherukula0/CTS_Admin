package com.cts.admin.dao;

import java.util.List;

import com.cts.admin.model.Session;

public interface SessionDAO {

    boolean startSession(Long userId);

    boolean endSession(Long sessionId, Long userId);

    Session getActiveSession();

    List<Session> getAllSessions();
}
//Admin clicks Start Session
//→ Session is created as ACTIVE.
//Admin switches to another module/tab
//→ It must still show Stop/End Session, not Start Session.
//Admin logs out
//→ The internal session must remain ACTIVE in the database.
//Admin logs in again
//→ System checks the database.
//→ Finds Session 25 is still ACTIVE.
//→ Button shows End Session.
//Another admin logs in
//→ They also see that an internal session is already active.
//→ They cannot start another session.
//Admin ends the session
//→ Session 25 becomes ENDED.
//→ Only then can a new internal session be started.