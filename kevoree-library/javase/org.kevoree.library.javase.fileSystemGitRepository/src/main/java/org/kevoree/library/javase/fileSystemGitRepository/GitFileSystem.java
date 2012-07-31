package org.kevoree.library.javase.fileSystemGitRepository;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/17/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FileItem;
import org.kevoree.library.javase.fileSystem.client.FolderItem;
import org.kevoree.library.javase.fileSystem.client.LockFilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

@Library(name = "JavaSE")
@Provides({
        @ProvidedPort(name = "files", type = PortType.SERVICE, className = LockFilesService.class)
})
@MessageTypes({
        @MessageType(name = "saveFile", elems = {@MsgElem(name = "path", className = String.class), @MsgElem(name = "data", className = Byte[].class)})
})
@DictionaryType({
        @DictionaryAttribute(name = "url", optional = true),
        @DictionaryAttribute(name = "login", optional = true),
        @DictionaryAttribute(name = "pass", optional = true)
})
@ComponentType
public class GitFileSystem extends AbstractComponentType implements LockFilesService {

    protected File baseClone = null;
    private Logger logger = LoggerFactory.getLogger(GitFileSystem.class);
    protected Repository repository = null;
    protected Git git = null;
    private Set<String> lockedFile = Collections.synchronizedSet(new HashSet<String>());

    @Start
    public void start () throws Exception {
        try {
            baseClone = File.createTempFile("kevoreessh", "temp");
            baseClone.delete();
            baseClone.mkdir();
            CloneCommand clone = Git.cloneRepository();
            clone.setURI(this.getDictionary().get("url").toString()).setDirectory(baseClone).setBranch(org.eclipse.jgit.lib.Constants.HEAD);
            clone.call();
            git = Git.open(baseClone);
            repository = git.getRepository();

        } catch (Exception e) {
            logger.debug("Could not clone repository: ", e);
            throw new Exception(e);
        }
    }

    @Stop
    public void stop () {
        //DELETE FILE
    }

    @Update
    public void update () throws Exception {
        stop();
        start();
    }

    private Set<String> getFlatFiles (File base, String relativePath, boolean root, Set<String> extensions) {
        Set<String> files = new HashSet<String>();
        if (base.exists() && !base.getName().startsWith(".")) {
            if (base.isDirectory()) {
                File[] childs = base.listFiles();
                for (int i = 0; i < childs.length; i++) {
                    if (root) {
                        files.addAll(getFlatFiles(childs[i], relativePath, false, extensions));
                    } else {
                        files.addAll(getFlatFiles(childs[i], relativePath + "/" + base.getName(), false, extensions));
                    }
                }
            } else {

                boolean filtered = false;
                if (extensions != null) {
                    filtered = true;
                    logger.debug("Look for extension for {}", base.getName());
                    for (String filter : extensions) {
                        if (base.getName().endsWith(filter)) {
                            filtered = false;
                        }
                    }
                }
                if (!root && !filtered) {
                    files.add(relativePath + "/" + base.getName());
                }
            }
        }
        return files;
    }


    @Port(name = "files", method = "getFilesPath")
    public Set<String> getFilesPath () {
        return getFlatFiles(baseClone, "", true, null);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Port(name = "files", method = "getFilteredFilesPath")
    public Set<String> getFilteredFilesPath (Set<String> extensions) {
        return getFlatFiles(baseClone, "", true, extensions);
    }


    private long lastRevisionCheck = -1;

    @Override
    @Port(name = "files", method = "getFileContent")
    public byte[] getFileContent (String relativePath, Boolean lock) {
        //UPDATE PHASE
        try {
            git.pull().call();
        } catch (Exception e) {
            logger.error("Error while getRevision");
        }
        if (lock) {
            final String[] relativePathClean = {relativePath};
            if (relativePath.startsWith("/")) {
                relativePathClean[0] = relativePath.substring(relativePath.indexOf("/") + 1);
            }
            if (!lockedFile.contains(relativePathClean)) {
                lockedFile.add(relativePathClean[0]);
                Thread t = new Thread() {
                    @Override
                    public void run () {
                        Map<String, Long> locks = new HashMap<String, Long>();
                        locks.put(relativePathClean[0], lastRevisionCheck);
                        try {

                            //repository.lock(locks, "AutoLock Kevoree Editor", false, null);
                            lockedFile.add(relativePathClean[0]);
                        } catch (Exception e) {
                            logger.error("Error while acquire lock ", e);
                        }
                    }
                };
                t.start();
            }
        }
        return this.getFileContent(relativePath);
    }


    public byte[] getFileContent (String relativePath) {
        File f = new File(baseClone.getAbsolutePath() + relativePath);
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                byte[] result = convertStream(fs);
                fs.close();

                return result;
            } catch (Exception e) {
                logger.error("Error while getting file ", e);
            }
        } else {
            logger.debug("No file exist = {}{}", baseClone.getAbsolutePath(), relativePath);
            return new byte[0];
        }
        return new byte[0];
    }

    @Port(name = "files", method = "getAbsolutePath")
    public String getAbsolutePath (String relativePath) {
        if (new File(baseClone.getAbsolutePath() + relativePath).exists()) {
            return new File(baseClone.getAbsolutePath() + relativePath).getAbsolutePath();
        } else {
            return null;
        }
    }

    @Override
    @Port(name = "files", method = "saveFile")
    public boolean saveFile (String relativePath, byte[] data, Boolean unlock) {
        boolean result = saveFile(relativePath, data);
        File f = new File(baseClone.getAbsolutePath() + relativePath);
        if (f.exists()) {
            //SVNCommitClient clientCommit = new SVNCommitClient(authManager, SVNWCUtil.createDefaultOptions(true));
            File[] paths = {f};
            if (unlock) {
                String relativePathClean = relativePath;
                if (relativePath.startsWith("/")) {
                    relativePathClean = relativePath.substring(relativePath.indexOf("/") + 1);
                }
                try {
                    // repository.unlock(unlocks, false, null);
                    // clientCommit.doCommit(paths, true, "AutoCommit Kevoree Editor", false, false);
                    //git.commit().setAll(true).call();
                    addFileToRepository(f);
                    commitRepository(" File " + relativePathClean + " saved ", " name " , " email ");

                    lockedFile.remove(relativePathClean);
                } catch (Exception e) {
                    logger.error("error while unkock and commit git ", e);
                }
            }
        }
        return result;
    }

    @Port(name = "files", method = "unlock")
    public void unlock (String relativePath) {
        final String[] relativePathClean = {relativePath};
        if (relativePath.startsWith("/")) {
            relativePathClean[0] = relativePath.substring(relativePath.indexOf("/") + 1);
        }
        Map<String, Long> locks = new HashMap<String, Long>();
        locks.put(relativePathClean[0], lastRevisionCheck);
        try {
            // repository.lock(locks, "AutoLock Kevoree Editor", false, null);
            lockedFile.add(relativePathClean[0]);
        } catch (Exception e) {
            logger.error("Error while acquire lock ", e);
        }
    }

    @Override
    @Port(name = "files", method = "mkdirs")
    public boolean mkdirs (String relativePath) {
        File f = new File(baseClone.getAbsolutePath() + relativePath);

        addFileToRepository(f);
        commitRepository(" folders " + f.getPath() + " created ", " name " , " email ");

        return !f.exists() && f.mkdirs();
    }

    @Override
    @Port(name = "files", method = "delete")
    public boolean delete (String relativePath) {
        File f = new File(baseClone.getAbsolutePath() + relativePath);

        if (f.exists() && f.delete()) {
            String relativePathClean = relativePath;
            if (relativePath.startsWith("/")) {
                relativePathClean = relativePath.substring(relativePath.indexOf("/") + 1);
            }
            try {
                removeFileToRepository(f);
                /*String finalFilePath = f.getPath().substring(f.getPath().indexOf(baseClone.getPath())+ baseClone.getPath().length() + 1);
                git.rm().addFilepattern(finalFilePath).call();
                commitRepository(" File " + relativePathClean + " removed ", " name " , " email "); */

                lockedFile.remove(relativePathClean);
                return true;
            } catch (Exception e) {
                logger.error("error while commit git ", e);
            }
        }
        return false;
    }

    @Override
    @Port(name = "files", method = "getArborecence")
    public AbstractItem getArborecence(AbstractItem absRoot) {
        String nameDirectory = absRoot.getName();
        File file = new File(nameDirectory);
        FolderItem root = new FolderItem(absRoot.getName());
        root.setPath("/");
        Process(file,root);
        trierListe(root.getChilds());
        return root;
    }

    public String getRelativePath(String absolutePath){
        return "/"+ absolutePath.substring((baseClone.getPath().length())+1);
    }

    public void Process(File file, FolderItem item) {
        if(!file.getName().contains(".git") && !file.getName().endsWith("~"))
        {
            if(file.isFile()){
                FileItem itemToAdd = new FileItem(file.getName());
                itemToAdd.setParent(item);
                itemToAdd.setPath(getRelativePath(file.getPath()));
                item.add(itemToAdd);
            }
            else if (file.isDirectory()) {
                FolderItem folder = new FolderItem(file.getName());
                folder.setParent(item);
                folder.setPath(getRelativePath(file.getPath() + "/"));
                item.add(folder);
                File[] listOfFiles = file.listFiles();
                if(listOfFiles!=null) {
                    for (int i = 0; i < listOfFiles.length; i++)
                        Process(listOfFiles[i],folder);
                }
            }
        }
    }

    private void trierListe(List<AbstractItem> listeTest) {
        int indexCurrentChar = 0;
        for(int i = 0 ; i < listeTest.size(); i++){
            if(listeTest.get(indexCurrentChar).getClass() == FileItem.class){
                listeTest.add(listeTest.get(indexCurrentChar));
                listeTest.remove(indexCurrentChar);
            }else{
                trierListe(((FolderItem) listeTest.get(indexCurrentChar)).getChilds());
                indexCurrentChar++;
            }
        }
    }


    public boolean saveFile (String relativePath, byte[] data) {
        File f = new File(baseClone.getAbsolutePath() + relativePath);
        try {
            if(!f.exists())
                f.createNewFile();
            if(data.length != 0){
                FileOutputStream fw = new FileOutputStream(f);
                fw.write(data);
                fw.flush();
                fw.close();
            }
            return true;
        } catch (Exception e) {
            logger.error("Error while getting file ", e);
            return false;
        }
    }

    public static byte[] convertStream (InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int l;
        do {
            l = (in.read(buffer));
            if (l > 0) {
                out.write(buffer, 0, l);
            }
        } while (l > 0);
        return out.toByteArray();
    }


    //TODO CHECK IF THESE FUNCTION DESERVE TO BE HERE
    @Override
    @Port(name = "files", method = "move")
    public boolean move(String oldRelativePath, String newRelativePath){

        File oldFile = new File(baseClone+oldRelativePath);
        File newFile = new File(baseClone+newRelativePath);

        oldFile.renameTo(newFile);
        addFileToRepository(newFile);
        removeFileToRepository(oldFile);
        // commitRepository("rename or move "+ oldFile.getName() + " into " + newFile.getName(),"","");
        return true;
    }
    public void commitRepository(String message, String nom, String email) {
        CommitCommand commit = git.commit();
        commit.setMessage(message);
        commit.setAuthor(new PersonIdent(nom, email));
        commit.setAll(true);
        try {
            commit.call();
        } catch (Exception e) {
            logger.debug("Cannot commit repository "+e);
        }
    }

    public boolean addFileToRepository(File fileToAdd) {
        Boolean result = false;
        try {
            String finalFilePath = fileToAdd.getPath().substring(fileToAdd.getPath().indexOf(baseClone.getPath())+ baseClone.getPath().length() + 1);
            git.add().addFilepattern(finalFilePath).call();
            result = true;
        } catch (NoFilepatternException e) {
            logger.debug("Cannot add file to repository " + e);
        }
        return result;
    }

    public boolean removeFileToRepository(File fileToRemove) {
        Boolean result = false;
        try {
            String finalFilePath = fileToRemove.getPath().substring(fileToRemove.getPath().indexOf(baseClone.getPath())+ baseClone.getPath().length() + 1);
            logger.debug(" file f " + fileToRemove.getPath() + " string " + finalFilePath);
            git.rm().addFilepattern(finalFilePath).call();
            commitRepository(" File " + finalFilePath + " removed ", " name " , " email ");
            result = true;
        } catch (NoFilepatternException e) {
            logger.debug("Cannot remove file to repository " + e);
        }
        return result;
    }
}