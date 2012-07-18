package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;

import java.io.IOException;


@RemoteServiceRelativePath(value = "htmleditor")
public interface RepositoryToolsServices extends RemoteService {


    boolean updateContentFileAndCommit(String file, byte [] editorText, String login);

    boolean pushRepository(String login, String password);

    String getFileContent(String filePath) throws IOException;

    AbstractItem initRepository(String login, String password, String nameRepository);

    AbstractItem importRepository(String login, String password, String url);

    boolean createFolderIntoLocalRepository(AbstractItem item);

    boolean move(AbstractItem oldItem, AbstractItem newItem);

    boolean newFileIntoRepository(AbstractItem item);
}
