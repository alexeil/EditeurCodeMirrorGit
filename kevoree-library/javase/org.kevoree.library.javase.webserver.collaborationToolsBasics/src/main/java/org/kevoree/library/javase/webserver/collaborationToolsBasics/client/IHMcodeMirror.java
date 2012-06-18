package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.AbstractItem;
import org.kevoree.library.javase.webserver.collaborationToolsBasics.shared.FolderItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IHMcodeMirror implements EntryPoint,MirrorEditorCallback {
    private TextBox textBoxLogin, textBoxPassword, textBoxNameRepository,
            textBoxURLRepository, textBoxLogin2, textBoxPassword2;
    private HTML textAreaCodeShow;
    private TextArea codeMirror;
    private String login, password, nomRepository;
    private PopupPanel popupFormNew, popupFormOpen;
    private Button btnOpen, btnSave;
    private NativeEvent ne;
    private int index = 1;
    private TreeItem root;
    private int currentNumber;
    private int nextNumber;
    private RootPanel buttonBar,editor,systemFile;

    private final StructureServiceAsync structureService = GWT
            .create(StructureService.class);

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // Listener CodeMirror
        //TODO
        // CodeMirrorEditorWrapper.addOnChangeHandler(this);

        // get Divs
        buttonBar = RootPanel.get("buttonBar");
        editor = RootPanel.get("editor");
        systemFile = RootPanel.get("fileSystem");

        // add editor's content
        Grid gridEditor = new Grid(2,2);

        // textArea codeMirror
        //TODO
        //codeMirror = new HTML("<form><textarea id=\"code\" name=\"code\"> </textarea></form>");

        // textArea Display codeMirror's stuff
        textAreaCodeShow = new HTML();
        codeMirror = new TextArea();
        gridEditor.setWidget(0,0,codeMirror);
        gridEditor.setWidget(0,1,textAreaCodeShow);

        codeMirror.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                repositoryToolsServices.updateContentFileAndCommit(codeMirror.getText().getBytes(), login, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        textAreaCodeShow.setHTML(codeMirror.getText());
                    }

                });
            }
        });

        //TODO


        editor.add(gridEditor);

        // add buttonBar's Content

        // general Layout
        Grid grid = new Grid(2, 2);
        buttonBar.add(grid, 10, 10);
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
                pushContentEditorToRepo();
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

                repositoryToolsServices.importRepository(login, password, textBoxURLRepository.getText(), new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    RootPanel.get().add(new HTML(" FAIL at import repo " ));
                    }

                    @Override
                    public void onSuccess(AbstractItem abstractItem) {
                        loadFileSystem(abstractItem);
                    }
                });
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

                repositoryToolsServices.initRepository(login,password,nomRepository,new AsyncCallback<AbstractItem>() {
                    @Override
                    public void onFailure(Throwable throwable) { }

                    @Override
                    public void onSuccess(AbstractItem abstractItem) {
                        loadFileSystem(abstractItem);
                        popupFormNew.hide();
                        btnSave.setEnabled(true);

                    }
                });
            }
        });

        gridFieldsNew.setWidget(3, 0, btnCreateRepo);
        popupFormNew.setWidget(gridFieldsNew);
        popupFormOpen.setWidget(gridFieldsOpen);

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
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
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
        //TODO
        //textAreaCodeShow.setHTML(CodeMirrorEditorWrapper.getText());
        textAreaCodeShow.setHTML(codeMirror.getText());
        writeEditorContentToFile();
    }

    public void writeEditorContentToFile(){
        //TODO
        //  String contentEditor =  CodeMirrorEditorWrapper.getText();
        String contentEditor =  codeMirror.getText();

        repositoryToolsServices.updateContentFileAndCommit(contentEditor.getBytes(), login, new AsyncCallback<Boolean>() {
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
        repositoryToolsServices.pushRepository(login, password,new
                AsyncCallback<Boolean>(){

                    @Override public void onFailure(Throwable throwable) { }

                    @Override public void onSuccess(Boolean aBoolean) {  }

                });
    }

    public void loadFileSystem(AbstractItem abstractItem) {
        // add FileSystem's content
        structureService.getArborescence(abstractItem, new AsyncCallback<AbstractItem>() {

            @Override
            public void onFailure(Throwable caught) {
                RootPanel.get().add(new HTML("FAIL" + caught.getStackTrace().toString() + " message " + caught.getMessage()));
            }

            @Override
            public void onSuccess(AbstractItem result) {
                Tree tree = new Tree();
                root = new TreeItem(result.getName());
                tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

                    @Override
                    public void onSelection(SelectionEvent<TreeItem> event) {
                        TreeItem item = event.getSelectedItem();
                        //RootPanel.get().add(new HTML(getItemPath(item)));
                        repositoryToolsServices.getFileContent(getItemPath(item), new AsyncCallback<String>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            @Override
                            public void onSuccess(String s) {
                                // CodeMirrorEditorWrapper.setText(s);
                                //TODO
                                codeMirror.setText(s);
                            }
                        });

                    }
                });
                createGwtTree(result, root);
                tree.addItem(root);
                systemFile.add(tree);
            }
        });
    }

    public String getItemPath(TreeItem item){
        String pathItem = item.getText();
        String path = "";
        while(item.getParentItem() != null){
            path = item.getParentItem().getText()+"/" + path;
            item = item.getParentItem();
        }
        path = path + pathItem;
        return path;
    }

    public void createGwtTree(AbstractItem item, TreeItem root){
        for(int i = 0 ; i < ((FolderItem) item).getChilds().size() ; i++){
            if(((FolderItem) item).getChilds().get(i).getClass() == FolderItem.class){
                TreeItem folder = new TreeItem(((FolderItem) item).getChilds().get(i).getName());
                root.addItem(folder);
                createGwtTree(((FolderItem) item).getChilds().get(i),folder);
            }else{
                root.addItem(((FolderItem) item).getChilds().get(i).getName());
            }
        }
    }
}

