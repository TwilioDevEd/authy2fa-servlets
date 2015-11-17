package com.twilio.authy2fa.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {

    private static final int MAX_INACTIVE_INTERVAL = 30 * 60;

    public void LogIn(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("authenticated", true);
        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
    }

    public void LogOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
