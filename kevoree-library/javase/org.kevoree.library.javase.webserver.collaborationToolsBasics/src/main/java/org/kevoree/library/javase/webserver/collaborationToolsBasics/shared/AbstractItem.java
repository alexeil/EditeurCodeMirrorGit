package org.kevoree.library.javase.webserver.collaborationToolsBasics.shared;

import com.google.gwt.user.client.rpc.IsSerializable;



public abstract class AbstractItem implements IsSerializable

{
    protected String name;
    protected String path;
    protected AbstractItem parent = null;

    public AbstractItem(){}

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPath(){
        return this.path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public AbstractItem getParent() {
        return parent;
    }

    public void setParent(AbstractItem parent) {
        this.parent = parent;
    }
}
