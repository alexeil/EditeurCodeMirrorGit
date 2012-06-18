package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IHMcodeMirror implements EntryPoint {

    TreeItem root;

    /**
     * Create a remote service proxy to talk to the server-side Greeting
     * service.
     */
    private final StructureServiceAsync systemFileService = GWT
            .create(StructureService.class);

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        String login = "AccountTest";
        String password = "AccountTest1";
        String url = "https://github.com/AccountTest/tusaisbienceque.git";

        repositoryToolsServices.initRepository(login, password, url, new AsyncCallback<AbstractItem>() {
            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSuccess(AbstractItem folder) {
                systemFileService.getArborescence(folder, new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(AbstractItem result) {

                        Tree tree = new Tree();
                        root = new TreeItem(result.getName());
                        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

                            @Override
                            public void onSelection(SelectionEvent<TreeItem> event) {
                                TreeItem item = event.getSelectedItem();
                                RootPanel.get().add(new HTML(getItemPath(item)));
                            }
                        });
                        createGwtTree(result,root);
                        tree.addItem(root);
                        RootPanel.get().add(tree);
                    }
                });
            }
        });
    }




    public String getItemPath(TreeItem item){
        String pathItem = item.getText();
        String path = "";
        while(item.getParentItem() != null){
            path = item.getParentItem().getText()+"/" + path;
            item = item.getParentItem();
        }
        path = path + pathItem;
        return path;
    }

    public void createGwtTree(AbstractItem item, TreeItem root){
        for(int i = 0 ; i < ((FolderItem) item).getChilds().size() ; i++){
            if(((FolderItem) item).getChilds().get(i).getClass() == FolderItem.class){
                TreeItem folder = new TreeItem(((FolderItem) item).getChilds().get(i).getName());
                root.addItem(folder);
                createGwtTree(((FolderItem) item).getChilds().get(i),folder);
            }else{
                root.addItem(((FolderItem) item).getChilds().get(i).getName());
            }
        }
    }


}
