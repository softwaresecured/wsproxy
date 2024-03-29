package com.wsproxy.mvc.view;

import com.wsproxy.httpproxy.HttpProxy;
import com.wsproxy.mvc.model.BreakpointModel;
import com.wsproxy.mvc.thread.TrafficLogQueueProcessorThread;
import com.wsproxy.projects.ProjectDataService;
import com.wsproxy.projects.ProjectDataServiceException;
import com.wsproxy.trafficanalysis.Analyzer;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.System.exit;

public class WsProxyHeadless {
    private String projectFile = null;

    public WsProxyHeadless(String projectFile) {
        this.projectFile = projectFile;
    }
    public void start() {
        try {
            ProjectDataService projectDataService = new ProjectDataService(projectFile);
            HttpProxy httpProxy = new HttpProxy(new BreakpointModel());
            TrafficLogQueueProcessorThread trafficLogQueueProcessorThread =  new TrafficLogQueueProcessorThread(httpProxy.getLogger(), projectDataService);
            httpProxy.startAll();
            Object lock = new Object();
            Thread headlessThread = new Thread(() -> {
                synchronized(lock) {
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        System.out.println("Shutdown caught");
                        httpProxy.stopAll();
                        trafficLogQueueProcessorThread.shutdown();
                        try {
                            trafficLogQueueProcessorThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }));
                    httpProxy.startAll();
                    trafficLogQueueProcessorThread.start();
                    while (trafficLogQueueProcessorThread.isAlive()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            headlessThread.start();
            headlessThread.join();

        } catch (ProjectDataServiceException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
