package org.kevoree.library.javase.fileSystemGitRepository;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kevoree.annotation.*;
import org.kevoree.library.javase.fileSystemGit.GitFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/23/12
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Provides({
        @ProvidedPort(name = "createRepo", type= PortType.SERVICE, className = GitRepositoryActions.class),
})
@ComponentType
public class FileSystemGitRepositoryImpl extends GitFileSystem implements GitRepositoryActions {

    private Logger logger = LoggerFactory.getLogger(GitFileSystem.class);
    private Boolean isRepositoryExisting = false;

    @Start
    public void start() throws Exception {

    }

    @Stop
    public void stop() {
    }

    @Override
    @Port(name = "createRepo", method = "createRepository")
    public boolean createRepository(String login, String password, String nameRepository) {
        Boolean result = false;
        RepositoryService service = new RepositoryService();
        service.getClient().setCredentials(login, password);
        try {
            service.getRepository(login, nameRepository);
            logger.debug("Error the repository exists ");
        } catch (IOException e) {
            Repository repo = new Repository();
            repo.setName(nameRepository);
            try {
                service.createRepository(repo);
                result = true;
            } catch (IOException e2) {
                logger.debug("Could not create repository: ", e2);
            }
        }
        return result;
    }

    @Override
    @Port(name = "createRepo", method = "createFileAndAddToClonedRepository")
    public File createFileAndAddToClonedRepository(String url, Git git, String nomRepo) {
        File userFile = new File(nomRepo+"/monFichier.txt");
        try {
            userFile.createNewFile();
        } catch (IOException e) {
            logger.debug("Cannot create the file "+e);
        }
        return userFile;
    }


    @Override
    @Port(name = "createRepo", method = "updateContentFileAndCommit")
    public boolean updateContentFileAndCommit(byte[] editorText, File file, Git git, String login){
        boolean result = false;
        String nom = login;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(editorText);
            fos.close();
            addFileToRepository(git,file);
            commitRepository(git, "update" + System.currentTimeMillis(), "login", "");
            result = true;
        } catch (IOException e) {
            logger.debug("Cannot write into the file "+e);
        }
        return result;
    }

    @Override
    @Port(name = "createRepo", method = "cloneRepository")
    public Git cloneRepository(String url, String folderToPutRepo) {
        CloneCommand clone = new CloneCommand();
        clone.setURI(url);
        clone.setDirectory(new File(folderToPutRepo));
        clone.setBare(false);
        return clone.call();
    }

    @Override
    @Port(name = "createRepo", method = "commitRepository")
    public boolean commitRepository(Git git, String message, String nom, String email) {
        Boolean result = false;
        CommitCommand commit = git.commit();
        commit.setMessage(message);
        commit.setAuthor(new PersonIdent(nom, email));
        try {
            commit.call();
            result = true;
        } catch (Exception e) {
            logger.debug("Cannot commit repository "+e);
        }
        return result;
    }

    @Override
    @Port(name = "createRepo", method = "addFileToRepository")
    public boolean addFileToRepository(Git git, File file) {
        Boolean result = false;
        try {
            git.add().addFilepattern(getFilePattern(git.getRepository().getWorkTree(),file)).call();
            result = true;
        } catch (NoFilepatternException e) {
            logger.debug("Cannot add file to repository " + e);
        }
        return result;
    }



    @Override
    @Port(name = "createRepo", method = "pushRepository")
    public boolean pushRepository(Git git, String login, String password) {
        Boolean result = false;
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(login, password);
        try {
            git.push().setCredentialsProvider(user).call();
            result = true;
        } catch (InvalidRemoteException e) {
            logger.debug("Cannot push repository "+e);
        }
        return result;
    }

    @Override
    @Port(name = "createRepo", method = "getFilePattern")
    public String getFilePattern(File baseDir, File file) {
        String baseDirStr = baseDir.getAbsolutePath();
        String fileStr = file.getAbsolutePath();
        if (fileStr.startsWith(baseDirStr + File.separatorChar)) {
            return fileStr.substring(baseDirStr.length() + 1);
        } else {
            throw new RuntimeException("Impossible to make pattern from file '" + fileStr + "' based on '"
                    + baseDirStr + "'");
        }
    }

}
