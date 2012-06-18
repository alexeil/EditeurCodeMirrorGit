package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface StructureServiceAsync {

	void getStructure(String url, AsyncCallback<Set<String>> callback);

}
