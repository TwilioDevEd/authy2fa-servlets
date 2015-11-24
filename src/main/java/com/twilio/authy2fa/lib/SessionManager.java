package com.twilio.authy2fa.lib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {

    public static final String AUTHENTICATED = "authenticated";
    public static final String USER_ID = "user-id";

    private static final int MAX_INACTIVE_INTERVAL = 30 * 60;

    public void logIn(HttpServletRequest request, long userId) {
        HttpSession session = request.getSession();
        session.setAttribute(AUTHENTICATED, true);
        session.setAttribute(USER_ID, userId);
        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
    }

    public void logOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public long getLoggedUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (long) session.getAttribute(USER_ID);
        }

        return 0;
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (boolean) session.getAttribute(AUTHENTICATED);
        }

        return false;
    }
}
