package org.kevoree.library.javase.BouchonAPP;

import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;

@Library(name = "JavaSE")
@Requires({
        @RequiredPort(name = "response", type = PortType.SERVICE, className = IBouchon.class, optional = true)
})
@ComponentType
public class BouchonTest extends AbstractComponentType {

    private MyFrame frame = null;

    @Start
    public void start() {
        frame = new MyFrame();
        frame.setVisible(true);
    }
    @Update
    public void update () {
        stop();
        start();
    }



    @Stop
    public void stop() {
        frame.dispose();
        frame = null;
    }

    private class MyFrame extends JFrame {

        TextField request = new TextField();
        JButton sendRequest, btnGetMelodie;
        Label response = new Label("here");
        Label melodie = new Label("Melodie");


        public MyFrame() {

            // isCorrect
            request.setText("Insert true to get true");
            sendRequest = new JButton("send");
            sendRequest.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(isPortBinded("response")){
                        Boolean bool = getPortByName("response", IBouchon.class).isCorrect(request.getText());
                        response.setText(bool.toString());

                    }
                }
            });

            // get Melodie
            btnGetMelodie = new JButton("Get Melodie");
            btnGetMelodie.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(isPortBinded("response")){
                        String strMelodie = getPortByName("response", IBouchon.class).getMelodie();
                        melodie.setText(strMelodie);

                    }
                }
            });


            setLayout(new FlowLayout());
            add(request);
            add(sendRequest);
            add(response);

            add(btnGetMelodie);
            add(melodie);

            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            pack();
            setVisible(true);
        }
    }
}

