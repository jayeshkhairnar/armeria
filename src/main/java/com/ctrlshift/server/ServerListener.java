package com.ctrlshift.server;

public interface ServerListener {
    /**
     * Invoked when a {@link Server} begins its startup procedure. Note that the {@link Server} will abort
     * its startup when a {@link ServerListener#serverStarting(Server)} throws an exception.
     */
    void serverStarting(Server server) throws Exception;

    /**
     * Invoked when a {@link Server} finished its startup procedure successfully and it started to serve
     * incoming requests.
     */
    void serverStarted(Server server) throws Exception;

    /**
     * Invoked when a {@link Server} begins its shutdown procedure.
     */
    void serverStopping(Server server) throws Exception;

    /**
     * Invoked when a {@link Server} finished its shutdown procedure and it stopped to serve incoming requests.
     */
    void serverStopped(Server server) throws Exception;
}
