/**
 *
 */
package io.soluble.pjb.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

final class RemoteHttpServletRequest extends HttpServletRequestWrapper {

    private SimpleServletContextFactory factory;

    public RemoteHttpServletRequest(SimpleServletContextFactory factory, HttpServletRequest req) {
        super(req);
        this.factory = factory;
    }

    /*
     * Return the session obtained from the servlet.
     */
    public HttpSession getSession() {
        return factory.getSession();
    }

    /*
     * Return the old session or give up.
     */
    public HttpSession getSession(boolean clientIsNew) {
        HttpSession session = getSession();
        if (clientIsNew && !session.isNew())
            throw new IllegalStateException("To obtain a new session call java_session(null, true) at the beginning of your PHP script.");
        return session;
    }

}