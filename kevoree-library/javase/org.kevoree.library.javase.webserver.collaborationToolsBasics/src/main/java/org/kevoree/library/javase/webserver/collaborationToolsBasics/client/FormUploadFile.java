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
 * User: pdespagn
 * Date: 6/25/12
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormUploadFile extends PopupPanel{

    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

    private final StructureServiceAsync structureService = GWT
            .create(StructureService.class);

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private AbstractItem fileUpload;
    private RootPanel systemFileRoot;
    private FormPanel form;
    private boolean onFolder;
    private AbstractItem abstractItemRoot;
    private TextBox tbPath;

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
        tbPath.setText(fileToUpload.getName());
        tbPath.setName("nomDossier");
        panel.add(tbPath);

        FileUpload upload = new FileUpload();
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

                AbstractItem fileTocreate = new FileItem(tbPath.getText());
                if(onFolder) {
                    fileTocreate.setPath(fileUpload.getPath()+"/"+fileTocreate.getName());
                    fileTocreate.setParent(fileUpload);
                }else{
                    fileTocreate.setPath(fileUpload.getParent().getPath()+"/"+fileTocreate.getName());
                    fileTocreate.setParent(fileUpload.getParent());
                }

                repositoryToolsServices.addFiletoRepositoryAfterUpload(fileUpload, new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onSuccess(AbstractItem abstractItem) {
                        Singleton.getInstance().loadFileSystem(abstractItemRoot, systemFileRoot);
                        // hide();
                    }
                });
            }
        });
        this.add(form);
    }
}