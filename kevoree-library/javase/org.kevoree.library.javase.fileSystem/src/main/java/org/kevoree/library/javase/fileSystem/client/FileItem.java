package org.kevoree.library.javase.fileSystem.client;


import java.io.Serializable;

public class FileItem extends AbstractItem implements Serializable {

    public FileItem(){}

    public FileItem(String name){
        this.name = name;
    }


}
