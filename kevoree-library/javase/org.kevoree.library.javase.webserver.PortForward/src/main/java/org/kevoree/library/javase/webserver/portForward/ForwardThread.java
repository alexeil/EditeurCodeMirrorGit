package org.kevoree.library.javase.webserver.portForward;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/25/12
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ForwardThread handles the TCP forwarding between a socket
 * input stream (source) and a socket output stream (dest).
 * It reads the input stream and forwards everything to the
 * output stream. If some of the streams fails, the forwarding
 * stops and the parent is notified to close all its sockets.
 */

class ForwardThread extends Thread {
    private static final int BUFFER_SIZE = 8192;

    InputStream mInputStream;
    OutputStream mOutputStream;
    ClientThread mParent;

    /**
     * Creates a new traffic redirection thread specifying
     * its parent, input stream and output stream.
     */
    public ForwardThread(ClientThread aParent, InputStream aInputStream, OutputStream aOutputStream) {
        mParent = aParent;
        mInputStream = aInputStream;
        mOutputStream = aOutputStream;
    }

    /**
     * Runs the thread. Continuously reads the input stream and
     * writes the read data to the output stream. If reading or
     * writing fail, exits the thread and notifies the parent
     * about the failure.
     */

    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            while (true) {
                int bytesRead = mInputStream.read(buffer);
                if (bytesRead == -1)
                    break; // End of stream is reached --> exit
                mOutputStream.write(buffer, 0, bytesRead);
                mOutputStream.flush();
            }
        } catch (IOException e) {
            // Read/write failed --> connection is broken
        }

        // Notify parent thread that the connection is broken
        mParent.connectionBroken();
    }
}
