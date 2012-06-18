package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.StructureService;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FileItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;

import java.io.File;
import java.util.List;


public class StructureServiceImpl extends RemoteServiceServlet implements StructureService{

    public AbstractItem getArborescence(AbstractItem folder) {
        String nameDirectory = folder.getName();
        File file = new File(nameDirectory);
        FolderItem root = new FolderItem(folder.getName());
        Process(file,root);
        trierListe(root.getChilds());
        return root;
    }

    public void Process(File file, FolderItem item) {
        if(!file.getName().contains(".git"))
        {
            if(file.isFile()){
                item.add(new FileItem(file.getName()));
            }
            else if (file.isDirectory()) {
                FolderItem folder = new FolderItem(file.getName());
                item.add(folder);
                File[] listOfFiles = file.listFiles();
                if(listOfFiles!=null) {
                    for (int i = 0; i < listOfFiles.length; i++)
                        Process(listOfFiles[i],folder);
                }
            }
        }
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


    private String getAccountName(String url) {
        String[] tableGit = url.split("/");
        String accountName = tableGit[3];
        return accountName;
    }
}
