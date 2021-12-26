/*
 * The MIT License
 *
 * Copyright 2017 Isaac Aymerich <isaac.aymerich@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.segator.proxylive.stream;

import com.github.segator.proxylive.config.ProxyLiveConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/**
 *
 * @author Isaac Aymerich <isaac.aymerich@gmail.com>
 */
public class WebInputStream extends VideoInputStream{
    private final Logger logger = LoggerFactory.getLogger(WebInputStream.class);
    private final URL url;
    private HttpURLConnection connection;
    private InputStream httpInputStream;
    private ProxyLiveConfiguration config;

    public WebInputStream(URL url, ProxyLiveConfiguration config) throws MalformedURLException, IOException {
        this.url = url;
        this.config = config;

    }

    private void initializeConnection() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Connection","keep-alive");
        connection.setRequestProperty("User-Agent",config.getUserAgent());
        connection.setReadTimeout(config.getSource().getReconnectTimeout()*1000);
        if (url.getUserInfo() != null) {
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(url.getUserInfo().getBytes()));
            connection.setRequestProperty("Authorization", basicAuth);
        }
        connection.setRequestMethod("GET");

    }

    public boolean connect() throws IOException {
        initializeConnection();
        connection.connect();
        boolean connected = connection.getResponseCode() == 200 || connection.getResponseCode() == 204;
        httpInputStream = new WithoutBlockingInputStream(connection.getInputStream());
        return connected;
    }

    public boolean isConnected() {
        return httpInputStream != null;
    }

    @Override
    public int read() throws IOException {
        return httpInputStream.read();
    }

    @Override
    public int read(byte b[]) throws IOException {
        return httpInputStream.read(b);
    }

    public void close() throws IOException {
        if (isConnected()) {
            httpInputStream.close();
        } else {
            throw new IOException("The Stream of " + url.toString() + " is not connected");
        }
    }
}
