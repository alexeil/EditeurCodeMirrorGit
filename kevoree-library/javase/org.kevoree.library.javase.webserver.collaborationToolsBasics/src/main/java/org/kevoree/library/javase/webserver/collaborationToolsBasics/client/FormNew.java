package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
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
                login.setLength(0);
                login.append(textBoxLoginNew.getText());
                password.setLength(0);
                password.append(textBoxPasswordNew.getText());
                nomRepository = textBoxNameRepositoryNew.getText();

                if(isAnySpecialChar(nomRepository)){
                    labelError.setText("Error : repository name invalid ");
                    labelError.setVisible(true);
                }else{
                    if(!login.toString().isEmpty() && !password.toString().isEmpty() && !nomRepository.isEmpty())
                        repositoryToolsServices.initRepository(login.toString(),password.toString(),nomRepository,new AsyncCallback<AbstractItem>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                hide();
                                labelError.setText("Error : repository already exists Or wrong login/password");
                                labelError.setVisible(true);
                                btnSave.setEnabled(false);
                                systemFileRoot.clear();
                            }

                            @Override
                            public void onSuccess(AbstractItem absItem) {
                                CodeMirrorEditorWrapper.setText("");
                                CodeMirrorEditorWrapper.setFileOpened("");
                                abstractItemRoot = absItem;
                                Singleton.getInstance().loadFileSystem(abstractItemRoot,systemFileRoot);
                                hide();
                                labelError.setVisible(false);
                                btnSave.setEnabled(true);
                            }
                        });
                }
            }
        });

        gridFieldsNew.setWidget(3, 0, btnCreateRepo);

        Button cancel = new Button("Cancel");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide();
            }
        });
        gridFieldsNew.setWidget(3, 1, cancel);
        setWidget(gridFieldsNew);
    }

    private boolean isAnySpecialChar(String nameRepository) {
        RegExp regExp = RegExp.compile("[^ \\w]", "g");
        return regExp.test(nameRepository);
    }
}
