package com.ctrlshift.commons;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrlshift.client.ClientFactoryBuilder;
import com.ctrlshift.commons.util.EventLoopGroups;
import com.ctrlshift.server.ServerBuilder;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * Provides the common shared thread pools and {@link EventLoopGroup}s which is used when not overridden.
 */
public final class CommonPools {

    private static final Executor BLOCKING_TASK_EXECUTOR;
    private static final EventLoopGroup WORKER_GROUP;

    static {
        // Threads spawned as needed and reused, with a 60s timeout and unbounded work queue.
        final ThreadPoolExecutor blockingTaskExecutor = new ThreadPoolExecutor(
                Flags.numCommonBlockingTaskThreads(), Flags.numCommonBlockingTaskThreads(),
                60, TimeUnit.SECONDS, new LinkedTransferQueue<>(),
                new DefaultThreadFactory("armeria-common-blocking-tasks", true));

        blockingTaskExecutor.allowCoreThreadTimeOut(true);
        BLOCKING_TASK_EXECUTOR = blockingTaskExecutor;

        WORKER_GROUP = EventLoopGroups.newEventLoopGroup(Flags.numCommonWorkers(),
                                                         "armeria-common-worker", true);
    }

    /**
     * Returns the common blocking task {@link Executor} which is used when
     * {@link ServerBuilder#blockingTaskExecutor(Executor)} is not specified.
     */
    public static Executor blockingTaskExecutor() {
        return BLOCKING_TASK_EXECUTOR;
    }

    /**
     * Returns the common worker {@link EventLoopGroup} which is used when
     * {@link ServerBuilder#workerGroup(EventLoopGroup, boolean)} or
     * {@link ClientFactoryBuilder#workerGroup(EventLoopGroup, boolean)} is not specified.
     */
    public static EventLoopGroup workerGroup() {
        return WORKER_GROUP;
    }

    private CommonPools() {}
}
