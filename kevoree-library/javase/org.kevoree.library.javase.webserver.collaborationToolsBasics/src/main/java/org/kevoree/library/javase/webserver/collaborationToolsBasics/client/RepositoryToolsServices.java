package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/31/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */

@RemoteServiceRelativePath(value = "repositoryToolsServices")
public interface RepositoryToolsServices extends RemoteService {
     void createRepository(String login, String password, String nameRepository);

     void createFileAndAddToClonedRepository(String url, String nomRepo);

     boolean updateContentFileAndCommit(byte [] editorText, String login);

     void cloneRepository(String url, String nameRepository);

     boolean commitRepository(String message, String nom, String email);

     boolean addFileToRepository();

     boolean pushRepository(String login, String password);

     String getFilePattern();

    AbstractItem initRepository(String login, String password, String url);
}
