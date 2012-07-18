package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;

public interface StructureServiceAsync {
	void getArborescence(AbstractItem folder, AsyncCallback<AbstractItem> callback);

}
