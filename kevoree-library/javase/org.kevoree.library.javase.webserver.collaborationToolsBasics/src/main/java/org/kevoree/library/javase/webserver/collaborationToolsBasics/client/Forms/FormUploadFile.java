package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.WindowFactory;

public class FormUploadFile extends Window {

    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

    private AbstractItem fileUpload;
    private RootPanel systemFileRoot;
    private DynamicForm form;
    private AbstractItem abstractItemRoot;
    private TextItem tbPath;
    private FileUpload upload;
    private FormUploadFile window;

    public FormUploadFile(AbstractItem fileToUpload, AbstractItem absItemRoot, RootPanel systemFile) {
        super();
        this.fileUpload = fileToUpload;
        this.systemFileRoot = systemFile;
        this.abstractItemRoot = absItemRoot;

        window = this;
        WindowFactory.setParameters(this, "Upload New File", 350, 150, false, true, true, true);

        UploadDragAndDrop form = new UploadDragAndDrop();
       // this.addItem(form);
        this.addItem(new UploadDragAndDrop());

        this.addItem(new IButton("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                window.hide();
            }
        }));
        /*
        form = new DynamicForm();
        form.setAction(UPLOAD_ACTION_URL);

        form.setEncoding(Encoding.MULTIPART);
        form.setMethod(FormMethod.POST);


        tbPath = new TextItem();
        tbPath.setValue(fileUpload.getPath());
        tbPath.setName("nomDossier");

        upload = new FileUpload();
        upload.setName("uploadFormElement");

        form.setFields(tbPath);

        form.addSubmitValuesHandler(new SubmitValuesHandler() {
            @Override
            public void onSubmitValues(SubmitValuesEvent submitValuesEvent) {
                window.hide();
                Singleton.getInstance().loadFileSystem(abstractItemRoot, systemFileRoot);
            }
        });

        this.addItem(form);
        this.addItem(upload);
        this.addItem(new IButton("Submit",new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.submitForm();
              //  form.submit();
            }
        }));
        this.addItem(new IButton("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                window.hide();
            }
        }));
         */

    }
}
