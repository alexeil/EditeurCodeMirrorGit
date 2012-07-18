package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FileItem;
import org.kevoree.library.javase.fileSystem.client.FolderItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.StructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;


public class StructureServiceImpl extends RemoteServiceServlet implements StructureService{

    private Logger logger = LoggerFactory.getLogger(StructureServiceImpl.class);

    public AbstractItem getArborescence(AbstractItem folder) {
        String nameDirectory = folder.getName();
        File file = new File(nameDirectory);
        FolderItem root = new FolderItem(folder.getName());
        Process(file,root);
        trierListe(root.getChilds());
        return root;
    }

    public void Process(File file, FolderItem item) {
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
}