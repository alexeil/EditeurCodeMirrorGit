package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/11/12
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Singleton {

    private static volatile Singleton instance = null;

    private Tree tree;
    private TreeGrid localTreeGrid;
    private TreeNode currentSelectedNode;
    private AbstractItem abstractItemRoot;
    private String fileOpenedPath;
    private RootPanel systemFileRoot;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private final StructureServiceAsync structureService = GWT
            .create(StructureService.class);


    private Singleton() {
        super();
    }

    public final static Singleton getInstance() {
        if (Singleton.instance == null) {
            synchronized(Singleton.class) {
                if (Singleton.instance == null) {
                    Singleton.instance = new Singleton();
                }
            }
        }
        return Singleton.instance;
    }

    public void loadFileSystem(AbstractItem abstractItem, RootPanel systemFile) {
        this.abstractItemRoot = abstractItem;
        this.localTreeGrid = new TreeGrid();
        this.systemFileRoot = systemFile;
        this.localTreeGrid.setHeight("800px");
        this.systemFileRoot.clear();
        this.systemFileRoot.add(localTreeGrid);

        // add FileSystem's content
        structureService.getArborescence(abstractItem, new AsyncCallback<AbstractItem>() {
            @Override
            public void onFailure(Throwable caught) {
                RootPanel.get().add(new HTML("FAIL : message " + caught.getMessage()));
            }
            @Override
            public void onSuccess(AbstractItem result) {
                // result ---> /tmp/root for exemple
                // realRoot ---> root
                FolderItem realRoot = (FolderItem) ((FolderItem) result).getChilds().get(0);
                tree = new com.smartgwt.client.widgets.tree.Tree();

                TreeNode root = new TreeNode(realRoot.getName());
                root.setAttribute("abstractItem", realRoot);
                root.setAttribute("isFolder",true);

                // TreeNode root = new TreeNode(result.getName());
                tree.setData(new TreeNode[] { root });
                createGwtTree(realRoot, root);

                // createGwtTree(result, root);
                localTreeGrid.setData(tree);
                localTreeGrid.setContextMenu(initContextMenu());
                localTreeGrid.draw();
                //don't allow rightdirectoryPath clicks on tree grid
                localTreeGrid.addCellContextClickHandler(new CellContextClickHandler(){
                    public void onCellContextClick(CellContextClickEvent event) {
                        event.cancel();
                        currentSelectedNode = (TreeNode) event.getRecord();
                        localTreeGrid.getContextMenu().showContextMenu();
                    }
                });

                localTreeGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
                    @Override
                    public void onCellDoubleClick(CellDoubleClickEvent event) {
                        event.cancel();
                        if(!event.getRecord().getAttributeAsBoolean("isFolder")){
                            AbstractItem item = (AbstractItem) event.getRecord().getAttributeAsObject("abstractItem");
                            fileOpenedPath = item.getPath();

                            repositoryToolsServices.getFileContent(fileOpenedPath, new AsyncCallback<String>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    //To change body of implemented methods use File | Settings | File Templates.
                                }
                                @Override
                                public void onSuccess(String s) {
                                    CodeMirrorEditorWrapper.setFileOpened(fileOpenedPath);
                                    CodeMirrorEditorWrapper.setText(s);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void createGwtTree(AbstractItem item, TreeNode root) {
        for (int i = 0; i < ((FolderItem) item).getChilds().size(); i++) {
            if (((FolderItem) item).getChilds().get(i).getClass() == FolderItem.class) {
                FolderItem itemFolder = (FolderItem) ((FolderItem) item).getChilds().get(i);
                TreeNode folder = new TreeNode(itemFolder.getName());

                folder.setAttribute("abstractItem", itemFolder);
                folder.setAttribute("isFolder",true);
                tree.add(folder, root);
                if (itemFolder.getChilds().size() == 0){
                    folder.setIsFolder(true);
                }
                createGwtTree(itemFolder, folder);
            } else {
                TreeNode file = new TreeNode(((FolderItem) item).getChilds()
                        .get(i).getName());
                file.setAttribute("isFolder",false);
                file.setAttribute("abstractItem", ((FolderItem) item).getChilds().get(i));
                tree.add(file, root);
            }
        }
    }

    private Menu initContextMenu(){
        Menu mainMenu = new Menu();
        com.smartgwt.client.widgets.menu.MenuItem createFileMenu = new com.smartgwt.client.widgets.menu.MenuItem("Create file");
        createFileMenu.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent event) {
                AbstractItem item = (AbstractItem) currentSelectedNode.getAttributeAsObject("abstractItem");
                Boolean rightClickOnFolder = currentSelectedNode.getAttributeAsBoolean("isFolder");
                FormAddFile formAddFile = new FormAddFile(item,abstractItemRoot,rightClickOnFolder, systemFileRoot);
                formAddFile.center();
            }
        });

        com.smartgwt.client.widgets.menu.MenuItem createFolderMenu = new com.smartgwt.client.widgets.menu.MenuItem("Create folder");
        createFolderMenu.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                AbstractItem item = (AbstractItem) currentSelectedNode.getAttributeAsObject("abstractItem");
                Boolean rightClickOnFolder = currentSelectedNode.getAttributeAsBoolean("isFolder");
                FormAddFolder formAddFolder = new FormAddFolder(item,abstractItemRoot,rightClickOnFolder,systemFileRoot);
                formAddFolder.center();
            }
        });

        com.smartgwt.client.widgets.menu.MenuItem refactorMenu = new com.smartgwt.client.widgets.menu.MenuItem("Refactor");
        refactorMenu.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                AbstractItem item = (AbstractItem) currentSelectedNode.getAttributeAsObject("abstractItem");
                Boolean rightClickOnFolder = currentSelectedNode.getAttributeAsBoolean("isFolder");
                FormRenameFileOrFolder formRenameFileOrFolder = new FormRenameFileOrFolder(item,abstractItemRoot,rightClickOnFolder,systemFileRoot);
                formRenameFileOrFolder.center();
            }
        });

        com.smartgwt.client.widgets.menu.MenuItem importMenu = new com.smartgwt.client.widgets.menu.MenuItem("Import");
        importMenu.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent event) {
                AbstractItem item = (AbstractItem) currentSelectedNode.getAttributeAsObject("abstractItem");
                Boolean rightClickOnFolder = currentSelectedNode.getAttributeAsBoolean("isFolder");
                FormUploadFile formUploadFile = new FormUploadFile(item,abstractItemRoot,rightClickOnFolder,systemFileRoot);
                formUploadFile.center();
            }
        });

        mainMenu.setItems(createFileMenu,createFolderMenu,refactorMenu,importMenu);
        return mainMenu;
    }
}
