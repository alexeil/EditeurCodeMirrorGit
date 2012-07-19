package org.kevoree.library.javase.fileSystemGitRepository;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kevoree.annotation.*;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FolderItem;
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
        @ProvidedPort(name = "createRepo", type= PortType.SERVICE, className = GitRepositoryActions.class)
})
@ComponentType
public class FileSystemGitRepositoryImpl extends GitFileSystem implements GitRepositoryActions {

    private Logger logger = LoggerFactory.getLogger(GitFileSystem.class);

    // FROM GitFileSystem
    //  protected File baseClone = null;
   // protected org.eclipse.jgit.lib.Repository repository = null;
   // protected Git git = null;
    @Start
    public void start() throws Exception {
       if(!this.getDictionary().get("url").toString().isEmpty() && !this.getDictionary().get("login").toString().isEmpty() && !this.getDictionary().get("pass").toString().isEmpty())
            super.start();

    }

    @Stop
    public void stop() {
    }

    @Override
    @Port(name="createRepo", method = "importRepository")
    public void importRepository(String login, String password, String url, String nameRepository, String pathRepository) {
        if(isRepoExist(login,password, nameRepository)){
            baseClone = new File(pathRepository+nameRepository);
            deleteDir(baseClone);
            cloneRepository(url, nameRepository, pathRepository);
        }
    }

    @Override
    @Port(name="createRepo", method = "ChangeFileOrFolderName")
    public void ChangeFileOrFolderName(AbstractItem oldItem, AbstractItem newItem){
       //TODO fileSystemGit::Rename
        File oldFile = new File(oldItem.getPath());
        File newFile = new File(newItem.getPath());
        oldFile.renameTo(newFile);
        addFileToRepository(newFile);
        removeFileFromRepository(oldFile);
        commitRepository("rename "+ oldFile.getName() + " into " + newFile.getName(),"","");
    }


    @Override
    @Port(name="createRepo", method = "addFiletoRepositoryAfterUpload")
    public void addFiletoRepositoryAfterUpload(AbstractItem item){
        File file = new File(item.getPath());
        addFileToRepository(file);
        commitRepository( file.getName()," uploaded","");
    }

    @Override
    @Port(name="createRepo", method = "createFileIntoLocalRepository")
    public void createFileIntoLocalRepository(AbstractItem item){
       //TODO fileSystemGit::CreateFile
        File file = new File(item.getPath());
        try {
            file.createNewFile();
            addFileToRepository(file);
            addFileToRepository(new File(item.getParent().getPath()));
        } catch (IOException e) {
            logger.debug("cannot create or add the file to the repository");
        }
        commitRepository("add file " + file.getName(),"","");
    }

    @Override
    @Port(name="createRepo", method = "createFolderIntoLocalRepository")
    public void createFolderIntoLocalRepository(AbstractItem item){
        File folder = new File(item.getPath());
        folder.mkdir();
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

    @Override
    @Port(name="createRepo", method = "initRepository")
    public AbstractItem initRepository(String login, String password, String nameRepository, String pathRepository){
        createRepository(login,password,nameRepository);
        cloneRepository("https://" + login + "@github.com/" + login + "/" + nameRepository + ".git", nameRepository, pathRepository);
        createFileToInitRepository("https:  //" + login + "@github.com/" + login + "/" + nameRepository + ".git", nameRepository, pathRepository);
        commitRepository("commit init", login, "Email@login.org");
        pushRepository(login, password);
        return new FolderItem(baseClone.getPath());
    }

 /*   @Override
    @Port(name="createRepo", method = "getArborescence")
    public AbstractItem getArborescence(AbstractItem absRoot) {


        Map map = new HashMap<String, String >();
        Map mapInv = new HashMap< String, String>();
        String treeItem;
        Set<String> setFilesPath = getFilesPath();

        for (String flatFile : setFilesPath) {
            if (flatFile.contains("/")) {
                String path = flatFile.substring(0, flatFile.lastIndexOf("/"));
                if (path.equals("")) {
                    // dossier toto/
                    if (flatFile.lastIndexOf("/") + 1 < flatFile.length()) {
                        if (!selectedCompileRootFilePath.equals(flatFile.substring(flatFile.lastIndexOf("/") + 1))) {
                            mapInv.put(tree.addItem(flatFile.substring(flatFile.lastIndexOf("/") + 1)), flatFile.substring(flatFile.lastIndexOf("/") + 1));
                        } else {
                            mapInv.put(tree.addItem("<span class=\"label warning\">" + flatFile.substring(flatFile.lastIndexOf("/") + 1) + "</span>"), flatFile.substring(flatFile.lastIndexOf("/") + 1));
                        }
                        map.put(flatFile.substring(flatFile.lastIndexOf("/") + 1), flatFile.substring(flatFile.lastIndexOf("/") + 1));
                       // logger.debug(" FIle " + flatFile.substring(flatFile.lastIndexOf("/") + 1));
                    }
                } else {
                    //TreeItem treeItem = null;
                    if (map.containsKey(path)) {
                        treeItem = map.get(path).toString();
                    } else {
                        treeItem = path;
                        map.put(path, treeItem);
                        mapInv.put(treeItem, path);
                    }
                    map.put(flatFile.substring(flatFile.lastIndexOf("/") + 1), flatFile.substring(flatFile.lastIndexOf("/") + 1));
                }
            } else {
                    map.put(flatFile, flatFile);
            }
        }




        Set cles = map.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()){
            Object cle = it.next(); // tu peux typer plus finement ici
            Object valeur = map.get(cle); // tu peux typer plus finement ici

            logger.debug(" Clef " + cle + " Valeur " + valeur);
        }

        return null;
        /*
        if(!file.getName().contains(".git") && !file.getName().endsWith("~"))
        {
            if(file.isFile()){
                FileItem itemToAdd = new FileItem(file.getName());
                itemToAdd.setParent(item);
                itemToAdd.setPath(file.getPath());
                //itemToAdd.setPath(getItemPath(itemToAdd));
                item.add(itemToAdd);
            }
            else if (file.isDirectory()) {
                FolderItem folder = new FolderItem(file.getName());
                folder.setParent(item);
                folder.setPath(file.getPath());
                //folder.setPath(getItemPath(folder));
                item.add(folder);
                File[] listOfFiles = file.listFiles();
                if(listOfFiles!=null) {
                    for (int i = 0; i < listOfFiles.length; i++)
                        Process(listOfFiles[i],folder);
                }
            }
        }

    }     */


    public boolean isRepoExist(String login, String password, String nameRepository ){
        RepositoryService service = new RepositoryService();
        service.getClient().setCredentials(login, password);
        try {
            service.getRepository(login, nameRepository);
            logger.debug("The repository exists ");
            return true;

        } catch (IOException e) {
            logger.debug("The repository : " + nameRepository +" doesn't exist ");
            return false;
        }
    }

    @Override
    @Port(name="createRepo", method = "createRepository")
    public void createRepository(String login, String password, String nameRepository) {
        RepositoryService service = new RepositoryService();
        service.getClient().setCredentials(login, password);
        if(!isRepoExist(login,password,nameRepository)){
            Repository repo = new Repository();
            repo.setName(nameRepository);
            try {
                service.createRepository(repo);
            } catch (IOException e) {
                logger.debug("Could not create repository: ", e);
            }
        }
    }

     @Override
     @Port(name="createRepo", method = "createFileToInitRepository")
    public void createFileToInitRepository(String url, String nomRepo, String directoryPath) {
        //TODO fileSystemGit::CreateFile
        File file = new File(directoryPath + nomRepo + "/README.md");
        try {
            file.createNewFile();
            addFileToRepository(file);
            commitRepository("Init Repository with a README.md ","","");
        } catch (IOException e) {
            logger.debug("Cannot create the file "+e);
        }
    }


    @Override
    @Port(name="createRepo", method = "updateContentFileAndCommit")
    public void updateContentFileAndCommit(String file, byte[] editorText, String login) {
        File fileToWrite = new File(file);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(editorText);
            fos.close();
            addFileToRepository(fileToWrite);
            commitRepository("update content of " + file, login, "");
        } catch (IOException e) {
            logger.debug("Cannot write into the file "+e);
        }
    }

    @Override
    @Port(name="createRepo", method = "cloneRepository")
    public void cloneRepository(String url, String nameRepository, String pathRepository) {
        baseClone = new File(pathRepository+nameRepository);
        CloneCommand clone = new CloneCommand();
        clone.setURI(url);
        clone.setDirectory(new File(pathRepository+nameRepository));
        clone.setBare(false);
        git = clone.call();
        repository = git.getRepository();
    }

    @Override
    @Port(name="createRepo", method = "commitRepository")
    public void commitRepository(String message, String nom, String email) {
        CommitCommand commit = git.commit();
        commit.setMessage(message);
        commit.setAuthor(new PersonIdent(nom, email));
        try {
            commit.call();
        } catch (Exception e) {
            logger.debug("Cannot commit repository "+e);
        }
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

    public boolean removeFileFromRepository(File fileToAdd) {
        Boolean result = false;
        try {

            String finalFilePath = fileToAdd.getPath().substring(fileToAdd.getPath().indexOf(baseClone.getPath())+ baseClone.getPath().length() + 1);

            git.rm().addFilepattern(finalFilePath).call();
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
    @Port(name="createRepo", method = "pushRepository")
    public void pushRepository(String login, String password) {
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(login, password);
        try {
            git.push().setCredentialsProvider(user).call();
        } catch (InvalidRemoteException e) {
            logger.debug("Cannot push repository "+e);
        }
    }

    @Override
    public byte[] getFileContent(String filePath) {
        //TODO fileSystemGit::getFileContent
        /*StringBuilder text = new StringBuilder();
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
       // return text.toString(); */
        return new byte[0];
    }
}
