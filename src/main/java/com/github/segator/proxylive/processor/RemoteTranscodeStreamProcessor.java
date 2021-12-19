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
package com.github.segator.proxylive.processor;

import com.github.segator.proxylive.config.RemoteTranscoder;
import com.github.segator.proxylive.stream.ClientBroadcastedInputStream;
import com.github.segator.proxylive.tasks.IStreamTask;
import com.github.segator.proxylive.tasks.ProcessorTasks;
import com.github.segator.proxylive.tasks.RemoteTranscodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author Isaac Aymerich <isaac.aymerich@gmail.com>
 */
public class RemoteTranscodeStreamProcessor implements IStreamMultiplexerProcessor, ISourceStream {

    @Autowired
    private ProcessorTasks tasks;
    @Autowired
    private ApplicationContext context;


    private final RemoteTranscoder transcoder;
    private final String channelID;
    private ClientBroadcastedInputStream pip;
    private RemoteTranscodeTask streamingDownloaderRunningTask;

    public RemoteTranscodeStreamProcessor(String channelID ,RemoteTranscoder transcoder) {
        this.transcoder = transcoder;
        this.channelID = channelID;
    }

    @Override
    public void start() throws Exception {
        RemoteTranscodeTask streamingRemoteTranscoderTask = (RemoteTranscodeTask) context.getBean("RemoteTranscodeTask", channelID,transcoder);
        synchronized (tasks) {
            streamingDownloaderRunningTask = (RemoteTranscodeTask) tasks.getTask(streamingRemoteTranscoderTask);
            if (streamingDownloaderRunningTask == null) {
                tasks.runTask(streamingRemoteTranscoderTask);
                streamingDownloaderRunningTask = streamingRemoteTranscoderTask;
            }
            pip = streamingDownloaderRunningTask.getMultiplexer().getConsumer("http cli");
        }
    }


    @Override
    public void stop(boolean force) throws IOException {
        synchronized (tasks) {
            streamingDownloaderRunningTask.getMultiplexer().flush();

            streamingDownloaderRunningTask.getMultiplexer().removeClientConsumer(pip);

            if (force || streamingDownloaderRunningTask.getMultiplexer().getClientsList().isEmpty()) {
                tasks.killTask(streamingDownloaderRunningTask);
            }
            try {
                pip.close();
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public ClientBroadcastedInputStream getMultiplexedInputStream() {
        synchronized (tasks) {
            return pip;
        }
    }

    @Override
    public boolean isConnected() {
        synchronized (tasks) {
            boolean connected = isTaskRunning();
            if (!connected) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    return false;
                }
                return isTaskRunning();
            }
            return true;
        }
    }

    private boolean isTaskRunning() {
        return streamingDownloaderRunningTask != null && !streamingDownloaderRunningTask.isTerminated() && !streamingDownloaderRunningTask.isCrashed();
    }

    @Override
    public IStreamTask getTask() {
        return streamingDownloaderRunningTask;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.streamingDownloaderRunningTask);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RemoteTranscodeStreamProcessor other = (RemoteTranscodeStreamProcessor) obj;
        if (!Objects.equals(this.streamingDownloaderRunningTask, other.streamingDownloaderRunningTask)) {
            return false;
        }
        return true;
    }

    @Override
    public String getIdentifier() {
        return "remote_" +channelID + "_"+ transcoder.getEndpoint() ;
    }

    @Override
    public String toString() {
        return "RemoteTranscodeStreamProcessor{" +
                "transcoder=" + transcoder +
                ", channelID='" + channelID + '\'' +
                '}';
    }
}
