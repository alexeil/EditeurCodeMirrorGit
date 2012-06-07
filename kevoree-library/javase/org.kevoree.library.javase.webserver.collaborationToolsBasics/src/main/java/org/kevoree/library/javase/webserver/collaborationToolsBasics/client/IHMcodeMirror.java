package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IHMcodeMirror implements EntryPoint {
    private TextBox textBoxLogin, textBoxPassword, textBoxNameRepository,
            textBoxURLRepository, textBoxLogin2, textBoxPassword2;
    private String login, password, nomRepository;
    private PopupPanel popupFormNew, popupFormOpen;
    private Button btnOpen, btnSave;
    private NativeEvent ne;


      private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
       .create(RepositoryToolsServices.class);


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        RootPanel rootPanel = RootPanel.get();

        // general Layout
        Grid grid = new Grid(2, 2);
        rootPanel.add(grid, 10, 0);
        grid.setSize("", "");

        // Form open existing project
        popupFormOpen = new PopupPanel(true);
        Grid gridFieldsOpen = new Grid(4, 2);
        Label lblURLRepository = new Label("HTTPS du repository :");

        Button btnNouveau = new Button("New");
        btnNouveau.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popupFormNew.show();
            }
        });

        grid.setWidget(0, 0, btnNouveau);
        btnNouveau.setWidth("123px");

        btnSave = new Button("Save");
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                      repositoryToolsServices.pushRepository(login, password,new
                      AsyncCallback<Boolean>(){

                      @Override public void onFailure(Throwable throwable) { }

                      @Override public void onSuccess(Boolean aBoolean) {  }

                      });

            }
        });

        btnOpen = new Button("Open");
        grid.setWidget(0, 1, btnOpen);
        btnOpen.setWidth("125px");

        btnOpen.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popupFormOpen.show();
            }
        });

        Button btnImport = new Button("Import");
        btnImport.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popupFormOpen.hide();
                btnSave.setEnabled(true);
            }
        });

        grid.setWidget(1, 0, btnSave);
        btnSave.setWidth("125px");
        btnSave.setEnabled(false);

        popupFormOpen.setWidget(gridFieldsOpen);

        // Form New project
        popupFormNew = new PopupPanel(true);
        Grid gridFieldsNew = new Grid(4, 2);

        // component for form new
        Label lblLogin = new Label("Login :");
        gridFieldsNew.setWidget(0, 0, lblLogin);
        lblLogin.setHeight("25px");

        textBoxLogin = new TextBox();
        gridFieldsNew.setWidget(0, 1, textBoxLogin);

        Label lblPassword = new Label("Password :");
        gridFieldsNew.setWidget(1, 0, lblPassword);

        textBoxPassword = new TextBox();
        gridFieldsNew.setWidget(1, 1, textBoxPassword);

        Label lblNomDuRepository = new Label("Nom du repository :");
        gridFieldsNew.setWidget(2, 0, lblNomDuRepository);

        textBoxNameRepository = new TextBox();
        gridFieldsNew.setWidget(2, 1, textBoxNameRepository);

        // component for form Open
        Label lblLogin2 = new Label("Login :");
        lblLogin2.setHeight("25px");
        textBoxLogin2 = new TextBox();
        Label lblPassword2 = new Label("Password :");
        textBoxPassword2 = new TextBox();

        textBoxURLRepository = new TextBox();
        gridFieldsOpen.setWidget(0, 0, lblLogin2);
        gridFieldsOpen.setWidget(0, 1, textBoxLogin2);
        gridFieldsOpen.setWidget(1, 0, lblPassword2);
        gridFieldsOpen.setWidget(1, 1, textBoxPassword2);
        gridFieldsOpen.setWidget(2, 0, lblURLRepository);
        gridFieldsOpen.setWidget(2, 1, textBoxURLRepository);
        gridFieldsOpen.setWidget(3, 0, btnImport);

        Button btnCreateRepo = new Button("Create repository");
        btnCreateRepo.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                login = textBoxLogin.getText();
                password = textBoxPassword.getText();
                nomRepository = textBoxNameRepository.getText();


                      repositoryToolsServices.createRepository(login,password,
                      nomRepository, new AsyncCallback<Boolean>(){

                      @Override public void onFailure(Throwable throwable) {  }

                      @Override public void onSuccess(Boolean aBoolean) {
                          btnSave.setEnabled(true) ;
                          popupFormNew.hide();

                      }

                      });
            }
        });

        gridFieldsNew.setWidget(3, 0, btnCreateRepo);

        popupFormNew.setWidget(gridFieldsNew);
        popupFormOpen.setWidget(gridFieldsOpen);

        HandlerRegistration logHandler = Event
                .addNativePreviewHandler(new NativePreviewHandler() {

                    @Override
                    public void onPreviewNativeEvent(NativePreviewEvent event) {
                        ne = event.getNativeEvent();

                        if (ne.getType().equals("keydown")
                                && ne.getCtrlKey()
                                && (ne.getKeyCode() == 'n' || ne.getKeyCode() == 'N')) {
                            ne.preventDefault();
                            Scheduler.get().scheduleDeferred(
                                    new ScheduledCommand() {
                                        @Override
                                        public void execute() {
                                            if (popupFormNew.isShowing()) {
                                                popupFormNew.hide();
                                            } else {
                                                popupFormNew.show();
                                            }
                                        }
                                    });
                        } else if (ne.getType().equals("keydown")
                                && ne.getCtrlKey()
                                && (ne.getKeyCode() == 'o' || ne.getKeyCode() == 'O')) {
                            ne.preventDefault();
                            Scheduler.get().scheduleDeferred(
                                    new ScheduledCommand() {
                                        @Override
                                        public void execute() {
                                            if (popupFormOpen.isShowing()) {
                                                popupFormOpen.hide();
                                            } else {
                                                popupFormOpen.show();
                                            }
                                        }
                                    });
                        } else if (ne.getType().equals("keydown")
                                && ne.getCtrlKey()
                                && (ne.getKeyCode() == 's' || ne.getKeyCode() == 'S')) {
                            ne.preventDefault();

                            Scheduler.get().scheduleDeferred(
                                    new ScheduledCommand() {
                                        @Override
                                        public void execute() {
                                            if (btnSave.isEnabled()) {
                                                // fonction to call for saving
                                            }
                                        }
                                    });
                        }
                    }
                });

        // Auto-Save
        int savePeriod = 5000;
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
            @Override
            public boolean execute() {
                if (btnSave.isEnabled()) {
                    // fonction to call for saving
                    System.err.println("Test --> " + System.currentTimeMillis());
                }
                return true;
            }

        }, savePeriod);
    }
}
