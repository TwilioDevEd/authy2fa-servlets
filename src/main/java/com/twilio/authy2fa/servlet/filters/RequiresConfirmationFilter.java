package com.twilio.authy2fa.servlet.filters;

import com.twilio.authy2fa.lib.SessionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("unused")
@WebFilter(urlPatterns = {"/authy/status", "/authy/request-token"})
public class RequiresConfirmationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isAuthorized(request)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isAuthorized(HttpServletRequest request) {
        return new SessionManager().isPartiallyAuthenticated(request);
    }
}
