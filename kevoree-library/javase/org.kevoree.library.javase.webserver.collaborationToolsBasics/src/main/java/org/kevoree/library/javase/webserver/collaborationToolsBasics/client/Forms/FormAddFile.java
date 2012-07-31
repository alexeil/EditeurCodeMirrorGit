package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FileItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.RepositoryToolsServices;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.RepositoryToolsServicesAsync;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Singleton;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.WindowFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/31/12
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormAddFile extends Window {
    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private AbstractItem item;
    private boolean onFolder;
    private  TextItem textItem;
    private RootPanel systemFileRoot;
    private AbstractItem abstractItemRoot;

    public FormAddFile(AbstractItem absItem, AbstractItem absItemRoot, boolean rightClickOnFolder, RootPanel systemFile){
        super();
        this.systemFileRoot = systemFile;
        this.abstractItemRoot = absItemRoot;
        this.item = absItem;
        this.onFolder = rightClickOnFolder;

        WindowFactory.setParameters(this, "Create New File", 10, 10, false, true, true, true);

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

        ok.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                AbstractItem fileTocreate = new FileItem(textItem.getEnteredValue());
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
                        Singleton.getInstance().loadFileSystem(abstractItemRoot, systemFileRoot);
                    }
                });
            }
        });

        this.addItem(form);
        this.addItem(ok);
        this.show();
    }
}
