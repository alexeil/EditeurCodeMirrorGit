package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
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
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FileItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/10/12
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormAddFile extends PopupPanel {

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private TextBox tbNewFile;
    private AbstractItem item;
    private boolean onFolder;

    private RootPanel systemFileRoot;
    private AbstractItem abstractItemRoot;

    public FormAddFile(AbstractItem absItem,AbstractItem absItemRoot, boolean rightClickOnFolder,RootPanel systemFile){
        super(false);
        this.systemFileRoot = systemFile;
        this.abstractItemRoot = absItemRoot;
        this.item = absItem;
        this.onFolder = rightClickOnFolder;
        setStyleName("popup");

        Grid grid = new Grid(2, 2);
        Label lblNewFile = new Label("Enter a new file name");
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
                    fileTocreate.setParent(item);
                }else{
                    fileTocreate.setPath(item.getParent().getPath()+"/"+fileTocreate.getName());
                    fileTocreate.setParent(item.getParent());
                }
                RootPanel.get().add(new HTML(" fileToCreate " + fileTocreate.getPath()+ " his parent " + fileTocreate.getParent().getName()));

                repositoryToolsServices.createFileIntoLocalRepository(fileTocreate, new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(AbstractItem item) {
                        hide();
                        Singleton.getInstance().loadFileSystem(abstractItemRoot, systemFileRoot);
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
