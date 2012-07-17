package org.kevoree.library.javase.BouchonAPP;

import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;

import java.util.ArrayList;
import java.util.List;

@Library(name = "JavaSE")
@ComponentType
@Provides({
        @ProvidedPort(name = "request", type = PortType.SERVICE, className = IBouchon.class)

})
public class Bouchon extends AbstractComponentType{
    List<String> melodies;

    @Start
    public void start(){
       melodies = new ArrayList<String>();
       melodies.add("*Derick*");
       melodies.add("*Rick Hunter*");
       melodies.add("*Mike Giver*");
    }

    @Update
    public void update () {
        stop();
        start();
    }

    @Stop
    public void stop(){

    }

    @Port(name = "request", method = "isCorrect")
    public Boolean isCorrect(String message) {
        return message.equals("true");
    }


    @Port(name = "request", method = "getMelodie")
    public String getMelodie() {
        int lower = 0;
        int higher = 2;

        int random = (int)(Math.random() * (higher-lower)) + lower;

        return melodies.get(random);
    }
}
