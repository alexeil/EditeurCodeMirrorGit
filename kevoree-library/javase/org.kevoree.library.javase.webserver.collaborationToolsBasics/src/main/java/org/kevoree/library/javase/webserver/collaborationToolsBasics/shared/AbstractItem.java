package org.kevoree.library.javase.webserver.collaborationToolsBasics.shared;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 6/14/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
import com.google.gwt.user.client.rpc.IsSerializable;



public abstract class AbstractItem implements IsSerializable

{
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    protected String name;

    public AbstractItem(){}

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
    }
}
