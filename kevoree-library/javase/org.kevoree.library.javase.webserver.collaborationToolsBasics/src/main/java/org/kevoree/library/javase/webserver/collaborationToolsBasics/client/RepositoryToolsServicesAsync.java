package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;


public interface RepositoryToolsServicesAsync {

    void initRepository(String login, String password, String nameRepository,AsyncCallback<AbstractItem> callback) ;

    void importRepository(String login, String password, String url,AsyncCallback<AbstractItem> callback) throws Exception;

    void updateContentFileAndCommit(String file, byte [] editorText,  String login, AsyncCallback<Boolean> callback);

    void pushRepository(String login, String password, AsyncCallback<Boolean> callback);

    void getFileContent(String filePath, AsyncCallback<String> callback);

    void newFileIntoRepository(AbstractItem item, AsyncCallback<Boolean> callback);

    void createFolderIntoLocalRepository(AbstractItem folder, AsyncCallback<Boolean> callback);

    void move(AbstractItem oldItem, AbstractItem newItem, AsyncCallback<Boolean> callback);

    void saveFileAfterUpload(AbstractItem item, AsyncCallback<Boolean> callback);
}
