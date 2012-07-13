package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;


public class FormOpen extends PopupPanel{
    TextBox  textBoxLoginImport,  textBoxPasswordImport, textBoxURLRepositoryImport;
    StringBuilder login, password,urlRepository;
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
        this.password = strPassword;

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
                login.append(textBoxLoginImport.getText());
                password.append(textBoxPasswordImport.getText());
                urlRepository.append(textBoxURLRepositoryImport.getText());
               // RootPanel.get().add(new HTML(" login " + login + " PW " + password + " url " + urlRepository));
                if(!login.toString().isEmpty() && !password.toString().isEmpty() && !urlRepository.toString().isEmpty())
                    repositoryToolsServices.importRepository(login.toString(), password.toString(), urlRepository.toString(), new AsyncCallback<AbstractItem>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            labelError.setText("Error importing the repository ( Wrong repository URL Or login/password");
                            labelError.setVisible(true);
                            btnSave.setEnabled(false);
                        }
                        @Override
                        public void onSuccess(AbstractItem abstractItem) {
                            labelError.setVisible(false);
                            CodeMirrorEditorWrapper.setText("");
                            CodeMirrorEditorWrapper.setFileOpened(null);
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

        this.setWidget(gridFieldsOpen);
    }
}
