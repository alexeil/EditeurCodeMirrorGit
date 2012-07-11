package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FileItem;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/10/12
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormRenameFileOrFolder extends PopupPanel {


    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private TextBox tbNewFile;
    private AbstractItem oldItem;
    private boolean onFolder;


    public FormRenameFileOrFolder(AbstractItem absItem, boolean rightClickOnFolder){
        super(false);
        this.oldItem = absItem;
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
                    newFileName.setPath(oldItem.getPath()+"/"+newFileName.getName());
                }else{
                    newFileName.setPath(oldItem.getParent().getPath()+"/"+newFileName.getName());
                }
                repositoryToolsServices.ChangeFileOrFolderName(oldItem,newFileName, new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(AbstractItem item) {
                        hide();
                        //loadFileSystem(item);
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
