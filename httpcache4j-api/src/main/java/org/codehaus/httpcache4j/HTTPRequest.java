/*
 * Copyright (c) 2008, The Codehaus. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.codehaus.httpcache4j;

import org.apache.commons.lang.Validate;

import org.codehaus.httpcache4j.payload.Payload;
import org.codehaus.httpcache4j.preference.Preferences;
import org.joda.time.DateTime;

import java.net.URI;

/**
 * Represents a HTTP request. You can use this in a couple of ways: <br/>
 * either manipulating the headers directly, or by using the convenience objects.
 * If you manipulate the headers, and use the convenience objects afterwards, the
 * headers produced by the convenience objects takes precedence.
 *
 * @author <a href="mailto:hamnis@codehaus.org">Erlend Hamnaberg</a>
 */
public class HTTPRequest {
    private final URI requestURI;
    private final HTTPMethod method;
    private final Conditionals conditionals;
    private final Preferences preferences;
    private final Headers headers;
    private final Challenge challenge;
    private final Payload payload;
    private final DateTime requestTime;

    public HTTPRequest(URI requestURI, HTTPMethod method) {
        this(requestURI, method, new Headers(), new Conditionals(), new Preferences(), null, null, new DateTime());
    }

    public HTTPRequest(HTTPRequest request) {
        this(request.getRequestURI(),
             request.getMethod(),
             request.getHeaders(),
             request.getConditionals(),
             request.getPreferences(),
             request.getChallenge(),
             request.getPayload(),
             request.getRequestTime()
        );
    }

    private HTTPRequest(URI requestURI,
                       HTTPMethod method,
                       Headers headers,
                       Conditionals conditionals,
                       Preferences preferences,
                       Challenge challenge,
                       Payload payload,
                       DateTime requestTime) {

        this.method = method;
        this.requestURI = requestURI;
        this.headers = headers;
        this.conditionals = conditionals;
        this.preferences = preferences;
        this.challenge = challenge;
        this.payload = payload;
        this.requestTime = requestTime;
    }

    public HTTPRequest(URI requestURI) {
        this(requestURI, HTTPMethod.GET);
    }

    public URI getRequestURI() {
        return requestURI;
    }

    public Headers getHeaders() {
        return headers;
    }

    /**
     * Returns all headers with the headers from the Conditionals, Payload and Preferences.
     * If you have explicitly set headers on the request that are the same as the Conditionals and Preferences they are overwritten.
     *
     * @return All the headers
     */
    public Headers getAllHeaders() {
        Headers requestHeaders = getHeaders();
        Headers conditionalHeaders = getConditionals().toHeaders();
        Headers preferencesHeaders = getPreferences().toHeaders();

        requestHeaders = merge(merge(requestHeaders, conditionalHeaders), preferencesHeaders);
        if (!requestHeaders.hasHeader(HeaderConstants.CONTENT_TYPE) && hasPayload()) {
            requestHeaders.add(HeaderConstants.CONTENT_TYPE, getPayload().getMimeType().toString());
        }

        //We don't want to add headers more than once.
        return requestHeaders;
    }

    private Headers merge(final Headers base, final Headers toMerge) {
        return new Headers(base).add(toMerge);        
    }

    public HTTPRequest addHeader(Header header) {
        Headers headers = new Headers(this.headers).add(header);
        return new HTTPRequest(requestURI, method, headers, conditionals, preferences, challenge, payload, requestTime);
    }

    public HTTPRequest addHeader(String name, String value) {
        return addHeader(new Header(name, value));
    }

    public Conditionals getConditionals() {
        return conditionals;
    }

    public HTTPRequest conditionals(Conditionals conditionals) {
        Validate.notNull(conditionals, "You may not set null conditionals");
        return new HTTPRequest(requestURI, method, headers, conditionals, preferences, challenge, payload, requestTime);
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public HTTPRequest preferences(Preferences preferences) {
        return new HTTPRequest(requestURI, method, headers, conditionals, preferences, challenge, payload, requestTime);
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public HTTPRequest challenge(Challenge challenge) {
        return new HTTPRequest(requestURI, method, headers, conditionals, preferences, challenge, payload, requestTime);
    }

    public Payload getPayload() {
        return payload;
    }

    public HTTPRequest payload(Payload payload) {
        return new HTTPRequest(requestURI, method, headers, conditionals, preferences, challenge, payload, requestTime);
    }

    public HTTPRequest headers(final Headers headers) {
        Validate.notNull(headers, "You may not set null headers");
        return new HTTPRequest(requestURI, method, headers, conditionals, preferences, challenge, payload, requestTime);
    }

    public boolean hasPayload() {
        return payload != null;
    }

    public DateTime getRequestTime() {
        return requestTime;
    }
}