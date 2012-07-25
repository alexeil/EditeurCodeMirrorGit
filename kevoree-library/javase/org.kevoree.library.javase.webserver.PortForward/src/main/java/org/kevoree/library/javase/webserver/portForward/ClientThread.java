package org.kevoree.library.javase.webserver.portForward;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/25/12
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ClientThread is responsible for starting forwarding between
 * the client and the server. It keeps track of the client and
 * servers sockets that are both closed on input/output error
 * during the forwarding. The forwarding is bidirectional and
 * is performed by two ForwardThread instances.

 */

class ClientThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(ClientThread.class);

    private Socket mClientSocket;
    private Socket mServerSocket;
    private boolean mForwardingActive = false;

    private String destination_host;
    private int destination_port;


    public ClientThread(Socket aClientSocket, String destination_host,int destination_port ) {
        mClientSocket = aClientSocket;
        this.destination_host = destination_host;
        this.destination_port = destination_port;
    }

    /**
     * Establishes connection to the destination server and
     * starts bidirectional forwarding ot data between the
     * client and the server.
     */
    public void run() {

        InputStream clientIn;
        OutputStream clientOut;
        InputStream serverIn;
        OutputStream serverOut;

        try {
            // Connect to the destination server
            mServerSocket = new Socket(destination_host,destination_port);

            // Turn on keep-alive for both the sockets
            mServerSocket.setKeepAlive(true);
            mClientSocket.setKeepAlive(true);

            // Obtain client & server input & output streams
            clientIn = mClientSocket.getInputStream();
            clientOut = mClientSocket.getOutputStream();
            serverIn = mServerSocket.getInputStream();
            serverOut = mServerSocket.getOutputStream();

        } catch (IOException ioe) {
            logger.debug("Can not connect to {} : {} " ,destination_host,destination_port);
            connectionBroken();
            return;
        }

        // Start forwarding data between server and client
        mForwardingActive = true;
        ForwardThread clientForward = new ForwardThread(this, clientIn, serverOut);
        clientForward.start();
        ForwardThread serverForward =new ForwardThread(this, serverIn, clientOut);
        serverForward.start();

        logger.debug("TCP Forwarding {} <--> {} started.",
                mClientSocket.getInetAddress().getHostAddress() + ":" + mClientSocket.getPort(),
                mServerSocket.getInetAddress().getHostAddress() + ":" + mServerSocket.getPort());
    }

    /**
     * Called by some of the forwarding threads to indicate
     * that its socket connection is broken and both client
     * and server sockets should be closed. Closing the client
     * and server sockets causes all threads blocked on reading
     * or writing to these sockets to get an exception and to
     * finish their execution.
     */

    public synchronized void connectionBroken() {
        try {
            mServerSocket.close();
        } catch (Exception e) {}
        try {
            mClientSocket.close(); }
        catch (Exception e) {}

        if (mForwardingActive) {
            logger.debug("TCP Forwarding  {} <--> {}   stopped." +
                    mClientSocket.getInetAddress().getHostAddress() + ":" + mClientSocket.getPort(),
                    mServerSocket.getInetAddress().getHostAddress() + ":" + mServerSocket.getPort());
            mForwardingActive = false;
        }
    }
}

