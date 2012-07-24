package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;


public class FormOpen extends PopupPanel{
    TextBox  textBoxLoginImport,  textBoxPasswordImport, textBoxURLRepositoryImport;
    StringBuilder login, password;
    String urlRepository;
    Label labelError;
    Button btnSave;
    AbstractItem abstractItemRoot;
    RootPanel systemFileRoot;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    public FormOpen(Label lblError, Button buttonSave, AbstractItem absItemRoot, RootPanel systemFile,StringBuilder strLogin, StringBuilder strPassword){
        super(false);
        this.labelError = lblError;
        this.btnSave = buttonSave;
        this.abstractItemRoot = absItemRoot;
        this.systemFileRoot = systemFile;
        this.login = strLogin;
        this.login.setLength(0);
        this.password = strPassword;
        this.password.setLength(0);

        setStyleName("popup");

        Grid gridFieldsOpen = new Grid(4, 2);

        // component for form Open
        Label lblLogin = new Label("Login :");
        lblLogin.setHeight("25px");
        textBoxLoginImport = new TextBox();
        Label lblPassword2 = new Label("Password :");
        textBoxPasswordImport = new PasswordTextBox();
        Label lblURLRepository = new Label("HTTPS du repository :");
        textBoxURLRepositoryImport = new TextBox();

        Button btnImport = new Button("Import");
        btnImport.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                login.setLength(0);
                login.append(textBoxLoginImport.getText());
                password.setLength(0);
                password.append(textBoxPasswordImport.getText());
                urlRepository = textBoxURLRepositoryImport.getText();

                if(!login.toString().isEmpty() && !password.toString().isEmpty() && !urlRepository.isEmpty())
                    repositoryToolsServices.importRepository(login.toString(), password.toString(), urlRepository, new AsyncCallback<AbstractItem>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            hide();
                            labelError.setText("Error importing the repository ( Wrong repository URL Or login/password");
                            labelError.setVisible(true);
                            btnSave.setEnabled(false);
                            systemFileRoot.clear();
                        }
                        @Override
                        public void onSuccess(AbstractItem abstractItem) {
                            labelError.setVisible(false);
                            btnSave.setEnabled(true);
                            CodeMirrorEditorWrapper.setText("");
                            CodeMirrorEditorWrapper.setFileOpened("");
                            abstractItemRoot = abstractItem;
                            Singleton.getInstance().loadFileSystem(abstractItemRoot,systemFileRoot);
                            hide();
                        }
                    });
            }
        });

        gridFieldsOpen.setWidget(0, 0, lblLogin);
        gridFieldsOpen.setWidget(0, 1, textBoxLoginImport);
        gridFieldsOpen.setWidget(1, 0, lblPassword2);
        gridFieldsOpen.setWidget(1, 1, textBoxPasswordImport);
        gridFieldsOpen.setWidget(2, 0, lblURLRepository);
        gridFieldsOpen.setWidget(2, 1, textBoxURLRepositoryImport);
        gridFieldsOpen.setWidget(3, 0, btnImport);

        Button cancel = new Button("Cancel");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });

        gridFieldsOpen.setWidget(3, 1, cancel);

        this.setWidget(gridFieldsOpen);
    }
}
