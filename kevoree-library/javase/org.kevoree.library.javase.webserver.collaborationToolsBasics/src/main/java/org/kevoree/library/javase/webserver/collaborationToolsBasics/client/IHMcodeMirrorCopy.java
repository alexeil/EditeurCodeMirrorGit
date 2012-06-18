package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IHMcodeMirrorCopy implements EntryPoint {

    private TextArea textAreaInsert, textAreaShow;
    private TextBox textBoxLogin, textBoxPassword, textBoxNameRepository;
    private Button btnPush;
    private String login, password, nomRepository;
    private PopupPanel popupForm;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel rootPanel = RootPanel.get();

        Grid grid = new Grid(2, 2);
        rootPanel.add(grid, 0, 0);
        grid.setSize("600px", "600px");

        popupForm = new PopupPanel(true);

        Grid gridFields = new Grid(4,2);

        Label lblLogin = new Label("Login :");

        gridFields.setWidget(0, 0, lblLogin);
        lblLogin.setHeight("25px");

        textBoxLogin = new TextBox();
        gridFields.setWidget(0, 1, textBoxLogin);

        Label lblPassword = new Label("Password :");
        gridFields.setWidget(1, 0, lblPassword);

        textBoxPassword = new TextBox();
        gridFields.setWidget(1, 1, textBoxPassword);

        Label lblNomDuRepository = new Label("Nom du repository :");
        gridFields.setWidget(2, 0, lblNomDuRepository);

        textBoxNameRepository = new TextBox();
        gridFields.setWidget(2, 1, textBoxNameRepository);

        btnPush = new Button("push");
        btnPush.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                repositoryToolsServices.pushRepository(login, password,new AsyncCallback<Boolean>(){
                    @Override
                    public void onFailure(Throwable throwable) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                });
            }
        });



        Button btnNouveau = new Button("Nouveau");
        btnNouveau.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popupForm.show();
            }
        });




        Button btnCreateRepo = new Button("Create repository");
        btnCreateRepo.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                login = textBoxLogin.getText();
                password = textBoxPassword.getText();
                nomRepository = textBoxNameRepository.getText();

                repositoryToolsServices.createRepository(login,password,nomRepository, new AsyncCallback<Boolean>(){
                    @Override
                    public void onFailure(Throwable throwable) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        btnPush.setEnabled(true);
                        textAreaInsert.setEnabled(true);
                        popupForm.hide();

                    }

                });
                btnPush.setEnabled(true);
                textAreaInsert.setEnabled(true);
                popupForm.hide();
            }
        });

        gridFields.setWidget(3, 0, btnCreateRepo);

        grid.setWidget(0, 0, btnNouveau);
        grid.setWidget(0, 1, btnPush);
        btnPush.setWidth("125px");
        btnPush.setEnabled(false);

        textAreaShow = new TextArea();
        textAreaShow.setEnabled(false);
        grid.setWidget(1, 1, textAreaShow);
        textAreaShow.setSize("203px", "176px");

        textAreaInsert = new TextArea();
        textAreaInsert.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                repositoryToolsServices.updateContentFileAndCommit(textAreaInsert.getText().getBytes(), login, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        textAreaShow.setText(textAreaInsert.getText());
                    }

                });
                textAreaShow.setText(textAreaInsert.getText());

            }
        });

        grid.setWidget(1, 0, textAreaInsert);
        textAreaInsert.setSize("203px", "176px");
        textAreaInsert.setEnabled(false);

        popupForm.setWidget(gridFields);


    }
}
