package org.kevoree.library.javase.fileSystem.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FolderItem extends AbstractItem implements IsSerializable, Serializable {


    private static final long serialVersionUID = 2388319784164372900L;
    private List<AbstractItem> listeItem;

    public FolderItem(){

        this.listeItem = new ArrayList<AbstractItem>();

    };

    public FolderItem(String name){
            this.name = name;
        }

    public void add(AbstractItem itemToAdd){
        this.listeItem.add(itemToAdd);
    }

    public List<AbstractItem> getChilds(){
        return this.listeItem;
    }

}
