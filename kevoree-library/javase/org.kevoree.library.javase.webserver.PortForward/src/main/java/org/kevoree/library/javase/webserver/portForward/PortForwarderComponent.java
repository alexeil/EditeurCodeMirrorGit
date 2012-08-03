package org.kevoree.library.javase.webserver.portForward;

import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/25/12
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
@DictionaryType({
        @DictionaryAttribute(name = "ForwardTo_ip"),
        @DictionaryAttribute(name = "ForwardTo_port"),
        @DictionaryAttribute(name = "local_ip"),
        @DictionaryAttribute(name = "local_port")
})
@ComponentType
public class PortForwarderComponent extends AbstractComponentType {

    private Logger logger = LoggerFactory.getLogger(PortForwarderComponent.class);

    @Start
    public void start() throws IOException {
        /*URL oracle = new URL("http://localhost:8080");
        HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();
        yc.setRequestProperty("HOST", "localzz.org");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            logger.debug(inputLine);
        in.close();
                     */
        String ForwardTo_ip = this.getDictionary().get("ForwardTo_ip").toString();
        int ForwardTo_port = Integer.parseInt(this.getDictionary().get("ForwardTo_port").toString());

        String local_ip = this.getDictionary().get("local_ip").toString();
        int local_port = Integer.parseInt(this.getDictionary().get("local_port").toString());

        ServerSocket serverSocket=null;
        try{
            serverSocket = new ServerSocket(local_port, 50, InetAddress.getByName(local_ip));

        } catch (IOException e) {
            logger.debug(" Error with the socket server ", e);
            serverSocket.close();
        }

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(clientSocket,ForwardTo_ip,ForwardTo_port);
            clientThread.start();
        }
    }

    @Stop
    public void stop(){

    }
}
