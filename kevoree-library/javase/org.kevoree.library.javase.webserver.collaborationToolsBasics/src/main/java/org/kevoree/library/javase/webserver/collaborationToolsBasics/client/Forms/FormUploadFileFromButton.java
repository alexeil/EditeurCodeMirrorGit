package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.SubmitValuesEvent;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.IHMcodeMirror;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Singleton;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.WindowFactory;

public class FormUploadFileFromButton extends Window {

    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";

    private DynamicForm form;
    private TextItem tbPath;
    private  FileUpload upload;
    private org.kevoree.library.javase.webserver.collaborationToolsBasics.client.IHMcodeMirror IHMcodeMirror;
    private TreeGrid treeGrid;

    public FormUploadFileFromButton(IHMcodeMirror IHM){
        super();
        this.IHMcodeMirror = IHM;

        WindowFactory.setParameters(this, "Upload New File", 350, 150, false, true, true, true);

        form = new DynamicForm();
        form.setAction(UPLOAD_ACTION_URL);

        form.setEncoding(Encoding.MULTIPART);
        form.setMethod(FormMethod.POST);

        treeGrid =  Singleton.getInstance().loadFileSystemLight(IHMcodeMirror.getAbstractItemRoot());
        this.addItem(treeGrid);

        treeGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent selectionEvent) {
                AbstractItem item = (AbstractItem) treeGrid.getSelectedRecord().getAttributeAsObject("abstractItem");
                tbPath.setValue(item.getPath());

            }
        });

        tbPath = new TextItem();
        tbPath.setValue("Selection a folder");
        tbPath.setName("nomDossier");

        upload = new FileUpload();
        upload.setName("uploadFormElement");

        form.setFields(tbPath);

        form.addSubmitValuesHandler(new SubmitValuesHandler() {
            @Override
            public void onSubmitValues(SubmitValuesEvent submitValuesEvent) {
                hide();
                Singleton.getInstance().loadFileSystem(IHMcodeMirror.getAbstractItemRoot(), IHMcodeMirror.getSystemFileRoot());
            }
        });

        this.addItem(form);
        this.addItem(upload);
        this.addItem(new IButton("Submit", new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.submit();
            }
        }));
        this.addItem(new IButton("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        }));
    }
}
