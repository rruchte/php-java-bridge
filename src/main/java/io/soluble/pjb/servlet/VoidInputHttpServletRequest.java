/**
 *
 */
package io.soluble.pjb.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * A simple HTTP servlet request which is not connected to any input stream.
 *
 * @author jostb
 */
public class VoidInputHttpServletRequest extends HttpServletRequestWrapper {

    public VoidInputHttpServletRequest(HttpServletRequest req) {
        super(req);
    }

    private ServletInputStream in = null;

    public ServletInputStream getInputStream() {
        if (in != null) return in;
        return in = new ServletInputStream() {
            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {

            }

            public int read() throws IOException {
                return -1;
            }
        };
    }

    private BufferedReader reader = null;

    public BufferedReader getReaader() {
        if (reader != null) return reader;
        return reader = new BufferedReader(new InputStreamReader(getInputStream()));
    }
}