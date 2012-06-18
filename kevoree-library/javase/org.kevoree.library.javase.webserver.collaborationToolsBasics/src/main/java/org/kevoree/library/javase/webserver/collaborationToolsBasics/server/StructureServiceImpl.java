package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import java.io.File;
import java.util.Set;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

import org.kevoree.library.javase.client.StructureService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class StructureServiceImpl extends RemoteServiceServlet implements StructureService{
	
	Git git;

	public Set<String> getStructure(String url) {
		File file = new File("test");
		file.mkdir();
		CloneCommand clone = new CloneCommand();
        clone.setURI(url);
        clone.setDirectory(file);
        clone.setBare(false);
        git = clone.call();
        return null;
	}

}
