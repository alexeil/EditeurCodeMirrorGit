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
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.RepositoryToolsServices;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class RepositoryToolsServicesImpl extends RemoteServiceServlet implements RepositoryToolsServices {

    private Logger logger = LoggerFactory.getLogger(RepositoryToolsServicesImpl.class);
    AbstractItem baseFolder;
    private String directoryPath;
    private Git git;

    public RepositoryToolsServicesImpl(String directoryPath){
        baseFolder = new FolderItem(directoryPath);
        this.directoryPath = directoryPath;
    }

   @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
    }

    public AbstractItem importRepository(String login, String password, String url) {
        String nameRepository =  getNameRepositoryFromUrl(url);
        if(isRepoExist(login,password, nameRepository)){
            deleteDir(new File(directoryPath+nameRepository));
            cloneRepository(url, nameRepository);
            baseFolder.setName(directoryPath+nameRepository);

            return baseFolder;
        }else{
            return null;
        }
    }

    @Override
    public AbstractItem ChangeFileOrFolderName(AbstractItem oldItem, AbstractItem newItem){
        File oldFile = new File(oldItem.getPath());
        File newFile = new File(newItem.getPath());
        oldFile.renameTo(newFile);
        addFileToRepository(newFile);
        removeFileToRepository(oldFile);
        commitRepository("rename "+ oldFile.getName() + " into " + newFile.getName(),"","");
        return baseFolder;
    }


    @Override
    public AbstractItem addFiletoRepositoryAfterUpload(AbstractItem item){
        File file = new File(item.getPath());
        addFileToRepository(file);
        commitRepository("add file" + file.getName(),"","");
        return baseFolder;
    }

    @Override
    public AbstractItem createFileIntoLocalRepository(AbstractItem item){
        File file = new File(item.getPath());
        try {
            file.createNewFile();
            addFileToRepository(file);
            // TODO TEST ADD FOLDER
            addFileToRepository(new File(item.getParent().getPath()));
        } catch (IOException e) {
            logger.debug("cannot create or add the file to the repository");
        }
        commitRepository("add file" + file.getName(),"","");
        return baseFolder;
    }

    @Override
    public AbstractItem createFolderIntoLocalRepository(AbstractItem item){
        File folder = new File(item.getPath());
        folder.mkdir();
        //addFileToRepository(folder);
        //commitRepository("add folder" + folder.getName(),"","");
        return baseFolder;
    }

    // Deletes all files and subdirectories under dir
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public AbstractItem initRepository(String login, String password, String nameRepository){
        createRepository(login,password,nameRepository);
        cloneRepository("https://" + login + "@github.com/" + login + "/" + nameRepository + ".git", nameRepository);
        createFileToInitRepository("https:  //" + login + "@github.com/" + login + "/" + nameRepository + ".git", nameRepository);
        commitRepository("commit init", login, "Email@login.org");
        pushRepository(login, password);
        baseFolder.setName(directoryPath+nameRepository);
        return baseFolder;
    }

    public boolean isRepoExist(String login, String password, String nameRepository ){
        RepositoryService service = new RepositoryService();
        service.getClient().setCredentials(login, password);
        try {
            service.getRepository(login, nameRepository);
            logger.debug("The repository exists ");
            return true;

        } catch (IOException e) {
            logger.debug("The repository doesn't exist ");
            return false;
        }
    }

    @Override
    public void createRepository(String login, String password, String nameRepository) {
        RepositoryService service = new RepositoryService();
        service.getClient().setCredentials(login, password);
        if(!isRepoExist(login,password,nameRepository)){
            Repository repo = new Repository();
            repo.setName(nameRepository);
            try {
                service.createRepository(repo);
            } catch (IOException e2) {
                logger.debug("Could not create repository: ", e2);
            }
        }
    }

    @Override
    public void createFileToInitRepository(String url, String nomRepo) {
       File file = new File(directoryPath+nomRepo+"/README.md");
        try {
            file.createNewFile();
            addFileToRepository(file);
            commitRepository("Init Repository with a README.md ","","");
        } catch (IOException e) {
            logger.debug("Cannot create the file "+e);
        }
    }


    @Override
    public boolean updateContentFileAndCommit(String file, byte[] editorText, String login) {
        logger.debug("I write there --------->" + file);
        boolean result = false;
        File fileToWrite = new File(file);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(editorText);
            fos.close();
            addFileToRepository(fileToWrite);
            commitRepository("update content of " + file, login, "");
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
        clone.setDirectory(new File(directoryPath+nameRepository));
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

    public boolean addFileToRepository(File fileToAdd) {
        Boolean result = false;
        File workingDir = git.getRepository().getWorkTree();
        try {
             String finalFilePath = fileToAdd.getPath().substring(fileToAdd.getPath().indexOf(workingDir.getPath())+ workingDir.getPath().length() + 1);
             git.add().addFilepattern(finalFilePath).call();
             result = true;
        } catch (NoFilepatternException e) {
            logger.debug("Cannot add file to repository " + e);
        }
        return result;
    }

    public boolean removeFileToRepository(File fileToAdd) {
        Boolean result = false;
        File workingDir = git.getRepository().getWorkTree();
        try {
            git.rm().addFilepattern(fileToAdd.getPath()).call();
            result = true;
        } catch (NoFilepatternException e) {
            logger.debug("Cannot remove file to repository " + e);
        }
        return result;
    }

    private String getFilePattern(File baseDir, File file) {
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
    public boolean pushRepository(String login, String password) {
        logger.debug("Erreur PUSH COMMAND login " + login + " Password " + password);
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
    public String getFileContent(String filePath)  throws IOException {
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(filePath), "UTF-8");
        try {
            while (scanner.hasNextLine()){
                text.append(scanner.nextLine() + NL);
            }
        }
        finally{
            scanner.close();
        }
       // File file = new File(filePath);
        logger.debug("--------------6> filePath " + filePath + " content " + text);
        return text.toString();
    }

    public String getNameRepositoryFromUrl(String url){
        String nameRepository = "";
        String[] urlAsArray = url.split("/");
        nameRepository = urlAsArray[urlAsArray.length-1];
        nameRepository = nameRepository.substring(0,(nameRepository.length())-(".git".length()));
        return nameRepository;
    }



}
