package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
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


public class FormNew extends Window {

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    private StringBuilder login, password;
    private String nomRepository;
    private Label labelError;
    private ToolStripMenu toolStripMenu;
    private RootPanel systemFileRoot;
    private org.kevoree.library.javase.webserver.collaborationToolsBasics.client.IHMcodeMirror IHMcodeMirror;
    private TextItem textItemLogin;
    private PasswordItem textItemPassword;
    private TextItem textItemNameRepository;
    private FormNew window;


    public FormNew(Label lblError, ToolStripMenu menu, IHMcodeMirror IHM, RootPanel systemFile, StringBuilder strLogin, StringBuilder strPassword){
        super();
        this.labelError = lblError;
        this.toolStripMenu = menu;
        this.IHMcodeMirror = IHM;
        this.systemFileRoot = systemFile;
        this.login = strLogin;
        this.password = strPassword;

        window = this;
        WindowFactory.setParameters(this, "Create New Repository", 350, 150, false, true, true, true);

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

        textItemNameRepository = new TextItem();
        textItemNameRepository.setTitle("Repository Name");
        textItemNameRepository.setRequired(true);

        form.setFields(textItemLogin,textItemPassword, textItemNameRepository);

        IButton btnCreateRepo = new IButton("Create repository");
        btnCreateRepo.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                login.setLength(0);
                login.append(textItemLogin.getEnteredValue());
                password.setLength(0);
                password.append(textItemPassword.getEnteredValue());
                nomRepository = textItemNameRepository.getEnteredValue();

                if(isAnySpecialChar(nomRepository)){
                    labelError.setText("Error : repository name invalid ");
                    labelError.setVisible(true);
                }else{
                    if(!login.toString().isEmpty() && !password.toString().isEmpty() && !nomRepository.isEmpty())
                        repositoryToolsServices.initRepository(login.toString(),password.toString(),nomRepository,new AsyncCallback<AbstractItem>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                window.hide();
                                labelError.setText("Error : repository already exists Or wrong login/password");
                                labelError.setVisible(true);
                                toolStripMenu.disableButtons();
                                systemFileRoot.clear();
                            }

                            @Override
                            public void onSuccess(AbstractItem absItem) {
                                CodeMirrorEditorWrapper.setText("");
                                CodeMirrorEditorWrapper.setFileOpened("");
                                IHMcodeMirror.setAbstractItemRoot(absItem);
                                Singleton.getInstance().loadFileSystem(IHMcodeMirror.getAbstractItemRoot(),systemFileRoot);
                                window.hide();
                                labelError.setVisible(false);
                                toolStripMenu.enableButtons();
                            }
                        });
                }
            }
        });

        this.addItem(form);
        this.addItem(btnCreateRepo);
    }

    private boolean isAnySpecialChar(String nameRepository) {
        RegExp regExp = RegExp.compile("[^ \\w]", "g");
        return regExp.test(nameRepository);
    }
}
