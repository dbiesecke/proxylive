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
package com.github.segator.proxylive.tasks;

import com.github.segator.proxylive.config.RemoteTranscoder;
import com.github.segator.proxylive.entity.Channel;
import com.github.segator.proxylive.processor.IStreamMultiplexerProcessor;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Isaac Aymerich <isaac.aymerich@gmail.com>
 */
@Configuration
public class StreamTaskFactory {

    @Bean
    @Scope(value = "prototype")
    public HttpDownloaderTask HttpDownloaderTask(Channel channel) throws IOException {
        return new HttpDownloaderTask(channel);
    }

    @Bean
    @Scope(value = "prototype")
    public DirectTranscodeTask DirectTranscodeTask(Channel channel, String profile) throws IOException {
        return new DirectTranscodeTask(channel,profile);
    }

    @Bean
    @Scope(value = "prototype")
    public RemoteTranscodeTask RemoteTranscodeTask(String channelID, RemoteTranscoder transcoder) throws IOException {
        return new RemoteTranscodeTask(channelID,transcoder);
    }

    @Bean
    @Scope(value = "prototype")
    public HLSDirectTask HLSDirectTask(Channel channel, String profile) throws IOException {
        return new HLSDirectTask(channel,profile);

    }
}
