package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FileItem;


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
