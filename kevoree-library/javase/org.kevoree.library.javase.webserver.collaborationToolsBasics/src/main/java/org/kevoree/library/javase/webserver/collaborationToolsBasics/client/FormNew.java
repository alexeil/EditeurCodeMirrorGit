package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.fileSystem.client.AbstractItem;


public class FormNew extends PopupPanel {
    TextBox textBoxLoginNew,  textBoxPasswordNew, textBoxNameRepositoryNew;
    StringBuilder login, password;
    String nomRepository;
    Label labelError;
    Button btnSave;
    AbstractItem abstractItemRoot;
    RootPanel systemFileRoot;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);


    public FormNew(Label lblError, Button buttonSave, AbstractItem absItemRoot, RootPanel systemFile, StringBuilder strLogin, StringBuilder strPassword){
        super(false);
        this.labelError = lblError;
        this.btnSave = buttonSave;
        this.abstractItemRoot = absItemRoot;
        this.systemFileRoot = systemFile;
        this.login = strLogin;
        this.password = strPassword;
        setStyleName("popup");

        Grid gridFieldsNew = new Grid(4, 2);

        // component for form new
        Label lblLogin = new Label("Login :");
        gridFieldsNew.setWidget(0, 0, lblLogin);
        lblLogin.setHeight("25px");

        textBoxLoginNew = new TextBox();
        gridFieldsNew.setWidget(0, 1, textBoxLoginNew);

        Label lblPassword = new Label("Password :");
        gridFieldsNew.setWidget(1, 0, lblPassword);

        textBoxPasswordNew = new PasswordTextBox();
        gridFieldsNew.setWidget(1, 1, textBoxPasswordNew);

        Label lblNomDuRepository = new Label("Nom du repository :");
        gridFieldsNew.setWidget(2, 0, lblNomDuRepository);

        textBoxNameRepositoryNew = new TextBox();
        gridFieldsNew.setWidget(2, 1, textBoxNameRepositoryNew);

        Button btnCreateRepo = new Button("Create repository");
        btnCreateRepo.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                login.append(textBoxLoginNew.getText());
                password.append(textBoxPasswordNew.getText());
                nomRepository = textBoxNameRepositoryNew.getText();

                RootPanel.get().add(new HTML(" login " + login.toString() + " PW " + password.toString() + " url " + nomRepository));

                if(!login.toString().isEmpty() && !password.toString().isEmpty() && !nomRepository.isEmpty())
                    repositoryToolsServices.initRepository(login.toString(),password.toString(),nomRepository,new AsyncCallback<AbstractItem>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            labelError.setText("Error : repository already exists Or wrong login/password" + " error " + throwable.getMessage() + " " + login + " " + password + " " + nomRepository );
                            labelError.setVisible(true);
                            btnSave.setEnabled(false);
                        }

                        @Override
                        public void onSuccess(AbstractItem absItem) {
                            CodeMirrorEditorWrapper.setText("");
                            CodeMirrorEditorWrapper.setFileOpened(null);
                            abstractItemRoot = absItem;
                            Singleton.getInstance().loadFileSystem(abstractItemRoot,systemFileRoot);
                            hide();
                            labelError.setVisible(false);
                            btnSave.setEnabled(true);
                            RootPanel.get().add(new HTML(" login " + login + " password " + password));
                        }
                    });
            }
        });

        gridFieldsNew.setWidget(3, 0, btnCreateRepo);
        setWidget(gridFieldsNew);
    }


}
