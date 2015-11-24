package com.twilio.authy2fa.servlet.filters;

import com.twilio.authy2fa.lib.SessionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/welcome", "/account"})
public class AuthenticationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (isAuthorized(request, uri)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            request.setAttribute("data", "You're not authenticated");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isAuthorized(HttpServletRequest request, String uri) {
        return new SessionManager().isAuthenticated(request) || uri.endsWith("login");
    }
}
