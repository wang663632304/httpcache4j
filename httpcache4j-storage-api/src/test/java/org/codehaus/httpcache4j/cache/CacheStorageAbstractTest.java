package org.codehaus.httpcache4j.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

import java.net.URI;

import org.codehaus.httpcache4j.HTTPRequest;
import org.codehaus.httpcache4j.HTTPResponse;
import org.codehaus.httpcache4j.Headers;
import org.codehaus.httpcache4j.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import org.mockito.Mockito;

/** @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a> */
public abstract class CacheStorageAbstractTest {
    protected CacheStorage storage;
    protected static final HTTPRequest REQUEST = new HTTPRequest(URI.create("foo"));

    @Before
    public void setup() {
        storage = createCacheStorage();
    }

    protected abstract CacheStorage createCacheStorage();
    protected abstract void afterTest();

    @After
    public void after() {
        storage.clear();
        afterTest();
    }

    @Test
    public void testPutCacheItem() {
        HTTPResponse response = new HTTPResponse(null, Status.OK, new Headers());
        storage.insert(REQUEST, response);
        assertEquals(1, storage.size());
    }

    @Test
    public void testPutAndGetCacheItem() {
        CacheItem outItem = putAndGet(Mockito.mock(HTTPRequest.class));
        assertNotNull("OutItem was null", outItem);
        assertNotNull("OutItem response was null", outItem.getResponse());
    }

    private CacheItem putAndGet(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse(null, Status.OK, new Headers());
        URI requestURI = URI.create("foo");
        Mockito.when(request.getRequestURI()).thenReturn(requestURI);
        storage.insert(REQUEST, response);
        assertEquals(1, storage.size());
        return storage.get(request);
    }

    @Test
    public void testPutUpdatedCacheItem() {
        HTTPRequest request = Mockito.mock(HTTPRequest.class);
        CacheItem item = putAndGet(request);
        URI requestURI = URI.create("foo");
        Mockito.when(request.getRequestURI()).thenReturn(requestURI);
        HTTPResponse response = new HTTPResponse(null, Status.OK, new Headers());
        storage.update(REQUEST, response);
        final CacheItem cacheItem = storage.get(request);
        assertNotSame("Items were the same", cacheItem.getCachedTime(), item.getCachedTime());
    }

    @Test
    public void testInvalidate() {
        HTTPResponse response = new HTTPResponse(null, Status.OK, new Headers());
        URI requestURI = URI.create("foo");
        storage.insert(REQUEST, response);
        assertEquals(1, storage.size());
        storage.invalidate(requestURI);
        assertEquals(0, storage.size());
    }
}