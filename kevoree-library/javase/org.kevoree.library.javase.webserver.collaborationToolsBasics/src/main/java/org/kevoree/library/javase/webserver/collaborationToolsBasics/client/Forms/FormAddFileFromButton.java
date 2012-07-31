package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.*;


public class FormAddFileFromButton extends Window {

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private TreeGrid treeGrid;
    private org.kevoree.library.javase.webserver.collaborationToolsBasics.client.IHMcodeMirror IHMcodeMirror;
    private TextItem textItem;

    public FormAddFileFromButton(IHMcodeMirror IHM){
        super();
        this.IHMcodeMirror = IHM;

        WindowFactory.setParameters(this, "Create New File", 500, 400, false, true, true, true);

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

        ok.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

            @Override
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent clickEvent) {
                AbstractItem item = (AbstractItem) treeGrid.getSelectedRecord().getAttributeAsObject("abstractItem");
                item.setPath(item.getPath()+textItem.getEnteredValue());

                repositoryToolsServices.newFileIntoRepository(item, new AsyncCallback<Boolean>() {
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
