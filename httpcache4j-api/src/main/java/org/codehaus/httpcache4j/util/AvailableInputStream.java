package org.codehaus.httpcache4j.util;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.httpcache4j.payload.DelegatingInputStream;

/**
* @author Erlend Hamnaberg<erlend@hamnaberg.net>
*/
public final class AvailableInputStream extends DelegatingInputStream {
    private boolean available;

    public AvailableInputStream(InputStream delegate) {
        super(delegate);
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public void close() throws IOException {
        available = false;
        super.close();
    }
}
