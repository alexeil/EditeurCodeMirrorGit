package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IHMcodeMirror implements EntryPoint,MirrorEditorCallback {
    private TextBox textBoxLoginNew, textBoxNameRepositoryNew,
            textBoxURLRepositoryImport, textBoxLoginImport ;
    private PasswordTextBox  textBoxPasswordImport,textBoxPasswordNew;
    private HTML textAreaCodeShow;
    private StringBuilder login, password, nomRepository, urlRepository;
    private PopupPanel popupUploadFile;
    private Button btnOpen, btnSave;
    private NativeEvent ne;
    private Label labelError;
    private FormOpen formOpen;
    private FormNew formNew;
    private RootPanel buttonBar,editor,systemFileRoot;

    private AbstractItem abstractItemRoot;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);


    public void onModuleLoad() {
        // Listener CodeMirror
        CodeMirrorEditorWrapper.addOnChangeHandler(this);

        // get Divs
        buttonBar = RootPanel.get("buttonBar");
        editor = RootPanel.get("editor");
        systemFileRoot = RootPanel.get("fileSystem");



        // add editor's content
        Grid gridEditor = new Grid(1,1);

        // textArea Display codeMirror's stuff
        textAreaCodeShow = new HTML();
        textAreaCodeShow.setStyleName("textAreaCodeShow");

        gridEditor.setWidget(0,0,textAreaCodeShow);

        editor.add(gridEditor);

        // add buttonBar's Content
        Grid grid = new Grid(2, 2);
        buttonBar.add(grid);
        buttonBar.setStyleName("buttonBarGrid");

        labelError = new Label();
        labelError.setVisible(false);
        labelError.setStyleName("labelError");
        //grid.setWidget(2,0, labelError);
        buttonBar.add(labelError);

        // Form open existing project
      /*  popupFormOpen = new PopupPanel(true);
        popupFormOpen.setStyleName("popup");
        Grid gridFieldsOpen = new Grid(4, 2);
        Label lblURLRepository = new Label("HTTPS du repository :");  */

        Button btnNouveau = new Button("New");
        btnNouveau.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                formNew.center();
            }
        });

        grid.setWidget(0, 0, btnNouveau);
        btnNouveau.setWidth("123px");

        btnSave = new Button("Save");
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                pushContentEditorToRepo();
            }
        });

        login = new StringBuilder();
        password = new StringBuilder();


        formOpen =  new FormOpen(labelError, btnSave, abstractItemRoot,systemFileRoot, login, password);
        formNew =  new FormNew(labelError, btnSave, abstractItemRoot,systemFileRoot, login, password);

        btnOpen = new Button("Open");
        grid.setWidget(0, 1, btnOpen);
        btnOpen.setWidth("125px");

        btnOpen.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                formOpen.center();
            }
        });

        /*Button btnImport = new Button("Import");
        btnImport.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                login = textBoxLoginImport.getText();
                password = textBoxPasswordImport.getText();
                urlRepository = textBoxURLRepositoryImport.getText();
                if(!login.isEmpty() && !password.isEmpty() && !urlRepository.isEmpty())
                    repositoryToolsServices.importRepository(login, password, urlRepository, new AsyncCallback<AbstractItem>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            labelError.setText("Error importing the repository ( Wrong repository URL Or login/password");
                            labelError.setVisible(true);
                            btnSave.setEnabled(false);
                        }
                        @Override
                        public void onSuccess(AbstractItem abstractItem) {
                            labelError.setVisible(true);
                            textAreaCodeShow.setHTML("");
                            CodeMirrorEditorWrapper.setText("");
                            CodeMirrorEditorWrapper.setFileOpened(null);
                            abstractItemRoot = abstractItem;
                            Singleton.getInstance().loadFileSystem(abstractItemRoot,systemFileRoot);
                            popupFormOpen.hide();
                        }
                    });
            }
        });  */

        grid.setWidget(1, 0, btnSave);
        btnSave.setWidth("125px");
        btnSave.setEnabled(false);

       // popupFormOpen.setWidget(gridFieldsOpen);

        // Form New project
       /* popupFormNew = new PopupPanel(true);
        popupFormNew.setStyleName("popup");
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
        gridFieldsNew.setWidget(2, 1, textBoxNameRepositoryNew);    */

        // component for form Open
      /*  Label lblLogin2 = new Label("Login :");
        lblLogin2.setHeight("25px");
        textBoxLoginImport = new TextBox();
        Label lblPassword2 = new Label("Password :");
        textBoxPasswordImport = new PasswordTextBox();

        textBoxURLRepositoryImport = new TextBox();
        gridFieldsOpen.setWidget(0, 0, lblLogin2);
        gridFieldsOpen.setWidget(0, 1, textBoxLoginImport);
        gridFieldsOpen.setWidget(1, 0, lblPassword2);
        gridFieldsOpen.setWidget(1, 1, textBoxPasswordImport);
        gridFieldsOpen.setWidget(2, 0, lblURLRepository);
        gridFieldsOpen.setWidget(2, 1, textBoxURLRepositoryImport);
        gridFieldsOpen.setWidget(3, 0, btnImport); */

      /*  Button btnCreateRepo = new Button("Create repository");
        btnCreateRepo.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            login = textBoxLoginNew.getText();
            password = textBoxPasswordNew.getText();
            nomRepository = textBoxNameRepositoryNew.getText();
            if(!login.isEmpty() && !password.isEmpty() && !nomRepository.isEmpty())
                repositoryToolsServices.initRepository(login,password,nomRepository,new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        labelError.setText("Error : repository already exists Or wrong login/password");
                        labelError.setVisible(true);
                        btnSave.setEnabled(false);
                    }

                    @Override
                    public void onSuccess(AbstractItem abstractItem) {
                        textAreaCodeShow.setHTML("");
                        CodeMirrorEditorWrapper.setText("");
                        CodeMirrorEditorWrapper.setFileOpened(null);
                        abstractItemRoot =  abstractItem;
                        Singleton.getInstance().loadFileSystem(abstractItemRoot,systemFileRoot);
                        popupFormNew.hide();
                        btnSave.setEnabled(true);
                    }
                });
            }
        });        */


       /* gridFieldsNew.setWidget(3, 0, btnCreateRepo);
        popupFormNew.setWidget(gridFieldsNew);  */
     //   popupFormOpen.setWidget(gridFieldsOpen);

        HandlerRegistration logHandler = Event.addNativePreviewHandler(new NativePreviewHandler() {

            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
            ne = event.getNativeEvent();

            if (ne.getType().equals("keydown")
                    && ne.getCtrlKey()
                    && (ne.getKeyCode() == 'n' || ne.getKeyCode() == 'N')) {
                ne.preventDefault();
                Scheduler.get().scheduleDeferred( new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (formNew.isShowing()) {
                            formNew.hide();
                        } else {
                            formNew.center();
                        }
                    }
                });
            } else if (ne.getType().equals("keydown")
                    && ne.getCtrlKey()
                    && (ne.getKeyCode() == 'o' || ne.getKeyCode() == 'O')) {
                ne.preventDefault();
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (formOpen.isShowing()) {
                            formOpen.hide();
                        } else {
                            formOpen.center();
                        }
                    }
                });
            } else if (ne.getType().equals("keydown")
                    && ne.getCtrlKey()
                    && (ne.getKeyCode() == 's' || ne.getKeyCode() == 'S')) {
                ne.preventDefault();

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (btnSave.isEnabled()) {
                            // fonction to call for saving
                            pushContentEditorToRepo();
                        }
                    }
                });
            }
            }
        });

        // Auto-Save
        int savePeriod = 5000000;
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
            @Override
            public boolean execute() {
                if (btnSave.isEnabled()) {
                    // fonction to call for saving
                    pushContentEditorToRepo();
                }
                return true;
            }

        }, savePeriod);
    }

    @Override
    public void invokeMirrorCallback(JavaScriptObject obj) {
        textAreaCodeShow.setHTML(CodeMirrorEditorWrapper.getText());
        writeEditorContentToFile();
    }

    public void writeEditorContentToFile(){
        String contentEditor =  CodeMirrorEditorWrapper.getText();
        String filePath = CodeMirrorEditorWrapper.getFileOpened();
        repositoryToolsServices.updateContentFileAndCommit(filePath, contentEditor.getBytes(), login.toString(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
            }
        });
    }

    public void pushContentEditorToRepo() {
        repositoryToolsServices.pushRepository(login.toString(), password.toString(),new
                AsyncCallback<Boolean>(){
                    @Override public void onFailure(Throwable throwable) { }
                    @Override public void onSuccess(Boolean aBoolean) {  }
                });
    }
}

