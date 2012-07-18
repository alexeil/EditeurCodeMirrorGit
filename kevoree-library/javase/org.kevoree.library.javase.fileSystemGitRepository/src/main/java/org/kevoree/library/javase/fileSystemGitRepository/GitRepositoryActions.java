package org.kevoree.library.javase.fileSystemGitRepository;

import org.kevoree.library.javase.fileSystem.client.AbstractItem;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/23/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GitRepositoryActions {

    void createRepository(String login, String password, String nameRepository);
     //TODO
    void updateContentFileAndCommit(String file, byte [] editorText, String login);

    void commitRepository(String message, String nom, String email);

    void pushRepository(String login, String password);
    //TODO
    void createFileIntoLocalRepository(AbstractItem item);
    //TODO
    void createFolderIntoLocalRepository(AbstractItem item);
    //TODO
    void ChangeFileOrFolderName(AbstractItem oldItem, AbstractItem newItem);
    //TODO
    void addFiletoRepositoryAfterUpload(AbstractItem item);

    void importRepository(String login, String password, String url, String nameRepository, String path);

    void createFileToInitRepository(String url, String nomRepo, String directoryPath);

    void cloneRepository(String url, String nameRepository, String pathRepository);

    AbstractItem initRepository(String login, String password, String nameRepository, String pathRepository);
}