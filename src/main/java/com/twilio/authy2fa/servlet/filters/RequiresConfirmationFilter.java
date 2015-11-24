package com.twilio.authy2fa.servlet.filters;

import com.twilio.authy2fa.lib.SessionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("unused")
@WebFilter(urlPatterns = {"/authy/callback", "/authy/status"})
public class RequiresConfirmationFilter implements Filter {

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
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isAuthorized(HttpServletRequest request, String uri) {
        return new SessionManager().isPartiallyAuthenticated(request);
    }
}
