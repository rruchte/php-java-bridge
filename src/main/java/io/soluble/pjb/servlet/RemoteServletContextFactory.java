/*-*- mode: Java; tab-width:8 -*-*/

package io.soluble.pjb.servlet;

/*
 * Copyright (C) 2003-2007 Jost Boekemeier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER(S) OR AUTHOR(S) BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.soluble.pjb.bridge.ISession;
import io.soluble.pjb.bridge.http.IContext;
import io.soluble.pjb.bridge.http.IContextFactory;

/**
 * Create session contexts for servlets.<p> In addition to the
 * standard ContextFactory this factory keeps a reference to the
 * HttpServletRequest.
 *
 * @see io.soluble.pjb.bridge.http.ContextFactory
 * @see io.soluble.pjb.bridge.http.ContextServer
 */
public class RemoteServletContextFactory extends SimpleServletContextFactory {
    protected RemoteServletContextFactory(Servlet servlet, ServletContext ctx, HttpServletRequest proxy, HttpServletRequest req, HttpServletResponse res) {
        super(servlet, ctx, proxy, req, res, false);
    }

    /**
     * Set the HttpServletRequest for session sharing.
     *
     * @param req The HttpServletRequest
     */
    protected void setSessionFactory(HttpServletRequest req) {
        this.proxy = req;
    }

    /**
     * {@inheritDoc}
     */
    public ISession getSession(String name, short clientIsNew, int timeout) {
        // if name != null return a "named" php session which is not shared with jsp
        if (name != null) return visited.getSimpleSession(name, clientIsNew, timeout);

        if (session != null) return session;

        if (proxy == null) throw new NullPointerException("This context " + getId() + " doesn't have a session proxy.");
        return session = HttpSessionFacade.getFacade(this, kontext, proxy, res, clientIsNew, timeout);
    }

    /**
     * Create and add a new ContextFactory.
     *
     * @param servlet The servlet
     * @param kontext The servlet context
     * @param proxy   The request proxy
     * @param req     The HttpServletRequest
     * @param res     The HttpServletResponse
     * @return The created ContextFactory
     */
    public static IContextFactory addNew(Servlet servlet, ServletContext kontext, HttpServletRequest proxy, HttpServletRequest req, HttpServletResponse res) {
        RemoteServletContextFactory ctx = new RemoteServletContextFactory(servlet, kontext, proxy, req, res);
        return ctx;
    }

    /**
     * Return an emulated JSR223 context.
     *
     * @return The context.
     * @see io.soluble.pjb.servlet.HttpContext
     */
    public IContext createContext() {
        IContext ctx = new HttpContext(kontext, req, res);
        ctx.setAttribute(IContext.SERVLET_CONTEXT, kontext, IContext.ENGINE_SCOPE);
        ctx.setAttribute(IContext.SERVLET_CONFIG, servlet.getServletConfig(), IContext.ENGINE_SCOPE);
        ctx.setAttribute(IContext.SERVLET, servlet, IContext.ENGINE_SCOPE);

        ctx.setAttribute(IContext.SERVLET_REQUEST, new RemoteHttpServletRequest(this, req), IContext.ENGINE_SCOPE);
        ctx.setAttribute(IContext.SERVLET_RESPONSE, new RemoteHttpServletResponse(res), IContext.ENGINE_SCOPE);

        return ctx;
    }
}
