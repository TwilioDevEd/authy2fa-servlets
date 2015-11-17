package com.twilio.authy2fa.servlet.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        HttpSession session = request.getSession(false);

        System.out.println("onFilter");

        if (isAuthorized(session, uri)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            request.setAttribute("data", "You're not authenticated");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isAuthorized(HttpSession session, String uri) {
        return session != null && session.getAttribute("authenticated") != null ||
                uri.endsWith("login");
    }
}
