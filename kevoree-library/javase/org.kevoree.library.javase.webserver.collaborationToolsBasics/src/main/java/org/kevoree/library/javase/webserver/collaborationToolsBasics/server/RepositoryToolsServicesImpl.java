package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kevoree.library.javase.fileSystemGit.GitFileSystem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/31/12
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */




public class RepositoryToolsServicesImpl extends RemoteServiceServlet implements org.kevoree.library.javase.webserver.collaborationToolsBasics.client.RepositoryToolsServices {

    private Logger logger = LoggerFactory.getLogger(GitFileSystem.class);
    private Git git;
    private File file, baseDir;
    AbstractItem baseFolder;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    public void createRepository(String login, String password, String url) {
        String nameRepository = getNameRepositoryFromUrl(url);
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
            } catch (IOException e2) {
                logger.debug("Could not create repository: ", e2);
            }
        }

        this.cloneRepository(url, nameRepository);
        this.createFileAndAddToClonedRepository(url, nameRepository);


    }

    @Override
    public void createFileAndAddToClonedRepository(String url, String nomRepo) {
        file = new File(baseFolder+nomRepo+"/monFichier.txt");
        try {
            file.createNewFile();
            System.err.print(file.getAbsolutePath());
        } catch (IOException e) {
            logger.debug("Cannot create the file "+e);
        }
    }

    @Override
    public boolean updateContentFileAndCommit(byte[] editorText, String login) {
        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(editorText);
            fos.close();
            addFileToRepository();
            commitRepository("update" + System.currentTimeMillis(), login, "");
            result = true;
        } catch (IOException e) {
            logger.debug("Cannot write into the file "+e);
        }
        return result;
    }

    @Override
    public void cloneRepository(String url, String nameRepository) {
        CloneCommand clone = new CloneCommand();
        clone.setURI(url);
        baseFolder.setName(baseFolder.getName()+nameRepository);
        File fileClonedRepository = new File(baseFolder.getName());
        fileClonedRepository.mkdir();
        clone.setDirectory(fileClonedRepository);
        clone.setBare(false);
        git = clone.call();
    }

    @Override
    public boolean commitRepository(String message, String nom, String email) {
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
    public boolean addFileToRepository() {
        Boolean result = false;
        try {
            baseDir =  git.getRepository().getWorkTree();
            git.add().addFilepattern(getFilePattern()).call();
            result = true;
        } catch (NoFilepatternException e) {
            logger.debug("Cannot add file to repository " + e);
        }
        return result;
    }

    @Override
    public boolean pushRepository(String login, String password) {
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

    public String getNameRepositoryFromUrl(String url){
        String nameRepository = "";
        String[] urlAsArray = url.split("/");
        nameRepository = urlAsArray[urlAsArray.length-1];
        nameRepository = nameRepository.substring(0,(nameRepository.length())-(".git".length()));
        return nameRepository;
    }

    @Override
    public String getFilePattern() {
        String baseDirStr = baseDir.getAbsolutePath();
        String fileStr = file.getAbsolutePath();
        if (fileStr.startsWith(baseDirStr + File.separatorChar)) {
            return fileStr.substring(baseDirStr.length() + 1);
        } else {
            throw new RuntimeException("Impossible to make pattern from file '" + fileStr + "' based on '"
                    + baseDirStr + "'");
        }
    }

    @Override
    public AbstractItem initRepository(String login, String password, String url) {
        baseFolder = new FolderItem("/tmp/");
        createRepository(login,password,url);
        commitRepository("commit init", login, login);
        pushRepository(login, password);
        System.out.println(baseFolder);
        return baseFolder;
    }
}
