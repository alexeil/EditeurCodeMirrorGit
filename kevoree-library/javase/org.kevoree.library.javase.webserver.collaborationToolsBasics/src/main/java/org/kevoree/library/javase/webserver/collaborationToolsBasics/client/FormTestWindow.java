package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
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

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/31/12
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormTestWindow extends Window {
    private  FormTestWindow window;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private AbstractItem item;
    private boolean onFolder;
    private  TextItem textItem;
    private RootPanel systemFileRoot;
    private AbstractItem abstractItemRoot;

    public FormTestWindow(AbstractItem absItem,AbstractItem absItemRoot, boolean rightClickOnFolder,RootPanel systemFile){
        super();
        this.systemFileRoot = systemFile;
        this.abstractItemRoot = absItemRoot;
        this.item = absItem;
        this.onFolder = rightClickOnFolder;

        window = this;
        this.setTitle("Create New File");
        this.setWidth(350);
        this.setHeight(150);
        this.setShowMinimizeButton(false);
        this.setIsModal(true);
        this.setShowModalMask(true);
        this.centerInPage();

        this.addCloseClickHandler(new CloseClickHandler() {
            @Override
            public void onCloseClick(CloseClientEvent closeClientEvent) {
                window.destroy();
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

      /*  IButton cancel = new IButton("Cancel");
        form.setFields(textItem);

        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                window.destroy();
            }
        });    */

        this.addItem(form);
        this.addItem(ok);
      //  this.addItem(cancel);
        this.show();
    }
}
