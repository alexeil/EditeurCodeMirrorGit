package org.kevoree.library.javase.webserver.collaborationToolsBasics.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

public class FileItem extends AbstractItem implements IsSerializable{

    public FileItem(){}

    public FileItem(String name){
        this.name = name;
    }


}
