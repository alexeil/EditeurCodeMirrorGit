package org.kevoree.library.javase.fileSystem.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;


public class FileItem extends AbstractItem implements IsSerializable, Serializable {

    public FileItem(){}

    public FileItem(String name){
        this.name = name;
    }


}
