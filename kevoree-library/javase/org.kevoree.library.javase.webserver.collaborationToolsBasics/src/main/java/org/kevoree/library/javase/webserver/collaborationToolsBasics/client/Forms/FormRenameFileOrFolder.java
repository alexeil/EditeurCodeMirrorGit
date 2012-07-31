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


public class FormRenameFileOrFolder extends Window {


    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private AbstractItem oldItem;
    private boolean onFolder;
    private AbstractItem abstractItemRoot;
    private RootPanel systemFileRoot;
    private FormRenameFileOrFolder window;
    private TextItem textItem;

    public FormRenameFileOrFolder(AbstractItem absItem,AbstractItem absItemRoot, boolean rightClickOnFolder, RootPanel systemFile){
        super();
        this.oldItem = absItem;
        this.abstractItemRoot = absItemRoot;
        this.systemFileRoot = systemFile;
        this.onFolder = rightClickOnFolder;

        window = this;
        WindowFactory.setParameters(this, "Rename File/Folder", 500, 150, false, true, true, true);

        this.addCloseClickHandler(new CloseClickHandler() {
            @Override
            public void onCloseClick(CloseClientEvent closeClientEvent) {
                window.hide();
            }
        });

        DynamicForm form = new DynamicForm();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);

        textItem = new TextItem();
        textItem.setTitle("Rename " + absItem.getName() + " into : ");
        textItem.setRequired(true);

        form.setFields(textItem);

        IButton ok = new IButton("Ok");
        ok.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                AbstractItem newFileName = new FileItem(textItem.getEnteredValue());
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

        this.addItem(form);
        this.addItem(ok);
    }
}
