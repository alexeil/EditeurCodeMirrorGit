package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FileItem;


public class FormAddFileFromButton extends PopupPanel {

    private TextBox tbNewFile;
    private TreeGrid treeGrid;
    Button btnOk;
    private IHMcodeMirror IHMcodeMirror;

    public FormAddFileFromButton(IHMcodeMirror IHM){
        super(false);
        this.IHMcodeMirror = IHM;
        setStyleName("popup");

        Grid grid = new Grid(2, 3);
        Label lblNewFile = new Label("Enter a new file name");
        tbNewFile = new TextBox();
        btnOk = new Button("Ok");
        Button btnCancel = new Button("Cancel");

        grid.setWidget(0, 0, lblNewFile);
        grid.setWidget(0, 1, tbNewFile);
        grid.setWidget(1,0,btnOk);
        grid.setWidget(1,1,btnCancel);

        add(grid);
        FlowPanel root = new FlowPanel();
        this.add(root);

        root.add(Singleton.getInstance().loadFileSystemLight(IHMcodeMirror.getAbstractItemRoot()));



        btnOk.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                RootPanel.get().add(new HTML("selected "));
                Singleton.getInstance().loadFileSystemLight(IHMcodeMirror.getAbstractItemRoot());
                RootPanel.get().add(new HTML("selected " + treeGrid.getSelectedPaths().toString()));
                /*AbstractItem fileTocreate = new FileItem(tbNewFile.getText());
                if(onFolder) {
                    fileTocreate.setPath(item.getPath()+fileTocreate.getName());
                    fileTocreate.setParent(item);
                }else{
                    fileTocreate.setPath(item.getParent().getPath()+fileTocreate.getName());
                    fileTocreate.setParent(item.getParent());
                }

                repositoryToolsServices.newFileIntoRepository(fileTocreate, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(Boolean bool) {
                        hide();

                    }
                });   */
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
