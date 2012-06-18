package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("structure")
public interface StructureService extends RemoteService {
	Set<String> getStructure(String url);
}
