package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FileItem;


public class FormRenameFileOrFolder extends PopupPanel {


    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private TextBox tbNewFile;
    private AbstractItem oldItem;
    private boolean onFolder;
    private AbstractItem abstractItemRoot;
    private RootPanel systemFileRoot;

    public FormRenameFileOrFolder(AbstractItem absItem,AbstractItem absItemRoot, boolean rightClickOnFolder, RootPanel systemFile){
        super(false);
        this.oldItem = absItem;
        this.abstractItemRoot = absItemRoot;
        this.systemFileRoot = systemFile;
        this.onFolder = rightClickOnFolder;
        setStyleName("popup");

        Grid grid = new Grid(2, 2);
        Label lblNewFile = new Label("Rename " + absItem.getName() + " into : ");
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
                AbstractItem newFileName = new FileItem(tbNewFile.getText());
                if(onFolder) {
                    newFileName.setPath(oldItem.getPath()+newFileName.getName());
                }else{
                    newFileName.setPath(oldItem.getParent().getPath()+newFileName.getName());
                }
                repositoryToolsServices.move(oldItem, newFileName, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(Boolean item) {
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
