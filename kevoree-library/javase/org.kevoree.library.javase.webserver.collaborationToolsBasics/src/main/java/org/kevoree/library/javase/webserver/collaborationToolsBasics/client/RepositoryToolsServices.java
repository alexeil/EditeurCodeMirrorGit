package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/31/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */

@RemoteServiceRelativePath(value = "htmleditor")
public interface RepositoryToolsServices extends RemoteService {
     boolean createRepository(String login, String password, String nameRepository);

     void createFileAndAddToClonedRepository(String url, String nomRepo);

     boolean updateContentFileAndCommit(byte [] editorText, String login);

     void cloneRepository(String url, String nameRepository);

     boolean commitRepository(String message, String nom, String email);

     boolean addFileToRepository();

     boolean pushRepository(String login, String password);

     String getFilePattern();

   /* void addFileToRepository(com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Boolean> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg2);
    void cloneRepository(java.lang.String url, java.lang.String foldToPutRepo, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg4);
    void commitRepository(java.lang.String message, java.lang.String nom, java.lang.String email, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Boolean> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg5);
    void createFileAndAddToClonedRepository(java.lang.String url, java.lang.String nomRepo, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg4);
    void createRepository(java.lang.String login, java.lang.String password, java.lang.String nameRepository, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Boolean> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg5);
    void getFilePattern(com.google.gwt.user.client.rpc.AsyncCallback<java.lang.String> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg2);
    void pushRepository(java.lang.String login, java.lang.String password, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Boolean> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg4);
    void updateContentFileAndCommit(byte[] editorText, java.lang.String login, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Boolean> callback, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg4);
      */
}
