package org.kevoree.library.javase.fileSystemGitRepository;

import org.eclipse.jgit.api.Git;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/23/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GitRepositoryActions {
    public boolean createRepository(String login, String password, String nameRepository);

    public File createFileAndAddToClonedRepository(String url, Git git, String nomRepo);

    public boolean updateContentFileAndCommit(byte [] editorText, File file, Git git, String login);

    public Git cloneRepository(String url, String foldToPutRepo);

    public boolean commitRepository(Git git, String message, String nom, String email);

    public boolean addFileToRepository(Git git, File file);

    public boolean pushRepository(Git git, String login, String password);

    public String getFilePattern(File baseDir, File file);
}
