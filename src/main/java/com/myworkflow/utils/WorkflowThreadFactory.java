package com.myworkflow.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkflowThreadFactory implements ThreadFactory {
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;

    public WorkflowThreadFactory(String groupName) {
        group = new ThreadGroup(groupName);
    	namePrefix = groupName+"-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, 
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
