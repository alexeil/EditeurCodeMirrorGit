package org.kevoree.library.javase.webserver.portForward.test;

import javax.xml.ws.http.HTTPBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/27/12
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        URL oracle = new URL("http://localhost:8080");
        HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();
        yc.setRequestProperty("HOST", "localzz.org");

        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();

        yc.connect();
        while(true){}

    }
}

