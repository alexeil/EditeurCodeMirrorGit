package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.*;


public class FormAddFolderFromButton extends Window {

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private TextItem textItem;
    private org.kevoree.library.javase.webserver.collaborationToolsBasics.client.IHMcodeMirror IHMcodeMirror;
    private TreeGrid treeGrid;

    public FormAddFolderFromButton(IHMcodeMirror IHM){
        super();
        this.IHMcodeMirror = IHM;

        WindowFactory.setParameters(this, "Create New Folder", 350, 150, false, true, true, true);

        this.addCloseClickHandler(new CloseClickHandler() {
            @Override
            public void onCloseClick(CloseClientEvent closeClientEvent) {
                hide();
            }
        });

        DynamicForm form = new DynamicForm();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);

        textItem = new TextItem();
        textItem.setTitle("Name");
        textItem.setRequired(true);

        IButton ok = new IButton("Ok");
        form.setFields(textItem);
        this.addItem(form);
        this.addItem(ok);

        treeGrid =  Singleton.getInstance().loadFileSystemLight(IHMcodeMirror.getAbstractItemRoot());
        this.addItem(treeGrid);

        ok.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                AbstractItem item = (AbstractItem) treeGrid.getSelectedRecord().getAttributeAsObject("abstractItem");
                item.setPath(item.getPath()+textItem.getEnteredValue());

                repositoryToolsServices.createFolderIntoLocalRepository(item, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(Boolean bool) {
                        hide();
                        Singleton.getInstance().loadFileSystem(IHMcodeMirror.getAbstractItemRoot(), IHMcodeMirror.getSystemFileRoot());
                    }
                });
            }
        });
    }
}
