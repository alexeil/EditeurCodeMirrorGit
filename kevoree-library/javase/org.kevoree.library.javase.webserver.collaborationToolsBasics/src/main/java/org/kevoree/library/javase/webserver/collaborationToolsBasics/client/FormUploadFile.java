package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.fileSystem.client.FileItem;

public class FormUploadFile extends PopupPanel{

    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private AbstractItem fileUpload;
    private RootPanel systemFileRoot;
    private FormPanel form;
    private boolean onFolder;
    private AbstractItem abstractItemRoot;
    private TextBox tbPath;
    private  FileUpload upload;
    public FormUploadFile(AbstractItem fileToUpload, AbstractItem absItemRoot, boolean rightClickOnFolder, RootPanel systemFile) {
        super(false);
        setStyleName("popup");
        this.fileUpload = fileToUpload;
        this.systemFileRoot = systemFile;
        this.abstractItemRoot = absItemRoot;
        this.onFolder = rightClickOnFolder;

        form = new FormPanel();
        form.setAction(UPLOAD_ACTION_URL);
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        VerticalPanel panel = new VerticalPanel();
        form.setWidget(panel);

        tbPath = new TextBox();
        tbPath.setText(fileUpload.getPath());
        tbPath.setName("nomDossier");
        panel.add(tbPath);

        upload = new FileUpload();
        upload.setName("uploadFormElement");
        panel.add(upload);

        panel.add(new Button("Submit", new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.submit();
            }
        }));

        panel.add(new Button("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        }));

        form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                hide();
                Singleton.getInstance().loadFileSystem(abstractItemRoot, systemFileRoot);
            }
        });
        this.add(form);
    }
}
