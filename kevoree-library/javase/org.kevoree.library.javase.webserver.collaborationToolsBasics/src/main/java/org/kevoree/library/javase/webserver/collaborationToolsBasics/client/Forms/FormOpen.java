package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.client.*;


public class FormOpen extends Window {

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private StringBuilder login, password;
    private String urlRepository;
    private Label labelError;
    private RootPanel systemFileRoot;
    private ToolStripMenu toolStripMenu;
    private org.kevoree.library.javase.webserver.collaborationToolsBasics.client.IHMcodeMirror IHMcodeMirror;
    private FormOpen window;
    private TextItem textItemLogin;
    private PasswordItem textItemPassword;
    private TextItem textItemURLRepository;

    public FormOpen(Label lblError, ToolStripMenu menu, IHMcodeMirror IHM, RootPanel systemFile,StringBuilder strLogin, StringBuilder strPassword){
        super();
        this.IHMcodeMirror = IHM;
        this.labelError = lblError;
        this.toolStripMenu = menu;
        this.systemFileRoot = systemFile;
        this.login = strLogin;
        this.login.setLength(0);
        this.password = strPassword;
        this.password.setLength(0);

        window = this;
        WindowFactory.setParameters(this, "Import Repository", 350, 150, false, true, true, true);

        this.addCloseClickHandler(new CloseClickHandler() {
            @Override
            public void onCloseClick(CloseClientEvent closeClientEvent) {
                window.hide();
            }
        });

        DynamicForm form = new DynamicForm();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);

        textItemLogin = new TextItem();
        textItemLogin.setTitle("Login");
        textItemLogin.setRequired(true);

        textItemPassword = new PasswordItem();
        textItemPassword.setTitle("Password");
        textItemPassword.setRequired(true);

        textItemURLRepository = new TextItem();
        textItemURLRepository.setTitle("HTTPS Repository");
        textItemURLRepository.setRequired(true);

        form.setFields(textItemLogin, textItemPassword, textItemURLRepository);



        IButton btnImport = new IButton("Import");
        btnImport.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                login.setLength(0);
                login.append(textItemLogin.getEnteredValue());
                password.setLength(0);
                password.append(textItemPassword.getEnteredValue());
                urlRepository = textItemURLRepository.getEnteredValue();

                if(!login.toString().isEmpty() && !password.toString().isEmpty() && !urlRepository.isEmpty())
                    try {
                        repositoryToolsServices.importRepository(login.toString(), password.toString(), urlRepository, new AsyncCallback<AbstractItem>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                hide();
                                labelError.setText("Error importing the repository ( Wrong repository URL Or login/password )");
                                labelError.setVisible(true);
                                toolStripMenu.disableButtons();
                                systemFileRoot.clear();
                            }
                            @Override
                            public void onSuccess(AbstractItem abstractItem) {
                                labelError.setVisible(false);
                                toolStripMenu.enableButtons();
                                CodeMirrorEditorWrapper.setText("");
                                CodeMirrorEditorWrapper.setFileOpened("");

                                IHMcodeMirror.setAbstractItemRoot(abstractItem);
                                Singleton.getInstance().loadFileSystem(IHMcodeMirror.getAbstractItemRoot(),systemFileRoot);
                                hide();
                            }
                        });
                    } catch (Exception e) {
                        RootPanel.get().add(new HTML(" Exception while import " + e.getMessage() + " " + e.getStackTrace()));
                    }
            }
        });

        this.addItem(form);
        this.addItem(btnImport);
    }
}
