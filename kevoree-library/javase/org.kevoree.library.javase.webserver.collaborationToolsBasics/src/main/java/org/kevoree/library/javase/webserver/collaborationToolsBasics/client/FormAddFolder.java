package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FileItem;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/10/12
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormAddFolder extends PopupPanel {


    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private TextBox tbNewFile;
    private AbstractItem abstractItemRoot;
    private boolean onFolder;
    private TreeGrid localTreeGrid;
    private AbstractItem item;

    public FormAddFolder(AbstractItem absItem,AbstractItem absItemRoot, boolean rightClickOnFolder,TreeGrid treeGrid){
        super(false);
        this.localTreeGrid = treeGrid;
        this.abstractItemRoot = absItemRoot;
        this.item = absItem;
        this.onFolder = rightClickOnFolder;
        setStyleName("popup");

        Grid grid = new Grid(2, 2);
        Label lblNewFile = new Label("Enter a new folder name");
        tbNewFile = new TextBox();
        Button btnOk = new Button("Ok");
        Button btnCancel = new Button("Cancel");

        grid.setWidget(0, 0, lblNewFile);
        grid.setWidget(0, 1, tbNewFile);
        grid.setWidget(1,0,btnOk);
        grid.setWidget(1,1,btnCancel);

        add(grid);

        btnOk.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                AbstractItem fileTocreate = new FileItem(tbNewFile.getText());
                if(onFolder) {
                    fileTocreate.setPath(item.getPath()+"/"+fileTocreate.getName());
                }else{
                    fileTocreate.setPath(item.getParent().getPath()+"/"+fileTocreate.getName());
                }
                repositoryToolsServices.createFolderIntoLocalRepository(fileTocreate, new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(AbstractItem item) {
                        hide();
                        Singleton.getInstance().loadFileSystem(abstractItemRoot, localTreeGrid);
                    }
                });
            }
        });

        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });
    }
}
