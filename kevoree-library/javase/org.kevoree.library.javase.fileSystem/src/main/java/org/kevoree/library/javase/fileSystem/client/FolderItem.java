package org.kevoree.library.javase.fileSystem.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FolderItem extends AbstractItem implements IsSerializable, Serializable {

    private List<AbstractItem> listeItem;

    public FolderItem(){};

        public FolderItem(String name){
            this.name = name;
            this.listeItem = new ArrayList<AbstractItem>();
        }

    public void add(AbstractItem itemToAdd){
        this.listeItem.add(itemToAdd);
    }

    public List<AbstractItem> getChilds(){
        return this.listeItem;
    }

}
