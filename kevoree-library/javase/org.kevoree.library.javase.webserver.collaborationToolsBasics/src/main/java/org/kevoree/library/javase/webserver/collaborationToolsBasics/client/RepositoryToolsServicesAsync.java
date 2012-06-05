package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/31/12
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public interface RepositoryToolsServicesAsync {

    void createRepository(String login, String password, String nameRepository, AsyncCallback<Boolean> callback);

    void createFileAndAddToClonedRepository(String url,  String nomRepo, AsyncCallback<Void> callback);

    void updateContentFileAndCommit(byte [] editorText,  String login, AsyncCallback<Boolean> callback);

    void cloneRepository(String url, String foldToPutRepo,AsyncCallback<Void> callback);

    void commitRepository(String message, String nom, String email, AsyncCallback<Boolean> callback);

    void addFileToRepository( AsyncCallback<Boolean> callback);

    void pushRepository( String login, String password, AsyncCallback<Boolean> callback);

    void getFilePattern(AsyncCallback<String> callback);
}
