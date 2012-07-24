package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.LockFilesService;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.StructureService;
import org.kevoree.library.javase.webserver.servlet.FakeServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StructureServiceImpl extends RemoteServiceServlet implements StructureService{

    private Logger logger = LoggerFactory.getLogger(StructureServiceImpl.class);
    private  RepositoryToolsComponent repositoryToolsComponent;

    public StructureServiceImpl(RepositoryToolsComponent repoToolsComponent){
        this.repositoryToolsComponent = repoToolsComponent;
    }

    public AbstractItem getArborescence(AbstractItem folder) {
        return repositoryToolsComponent.getPortByName("files", LockFilesService.class).getArborecence(folder);
    }

    public javax.servlet.ServletContext getServletContext() {
        return new FakeServletContext();
    }
}