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
import org.kevoree.library.javase.fileSystem.client.AbstractItem;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IHMcodeMirror implements EntryPoint,MirrorEditorCallback {
    private HTML textAreaCodeShow;
    private StringBuilder login, password;
    private Button btnSave;
    private NativeEvent ne;
    private FormOpen formOpen;
    private FormNew formNew;

    private AbstractItem abstractItemRoot;

    private final RepositoryToolsServicesAsync repositoryToolsServices = GWT
            .create(RepositoryToolsServices.class);


    public void onModuleLoad() {
        // Listener CodeMirror
        CodeMirrorEditorWrapper.addOnChangeHandler(this);

        // get Divs
        RootPanel buttonBar = RootPanel.get("buttonBar");
        RootPanel editor = RootPanel.get("editor");
        RootPanel systemFileRoot = RootPanel.get("fileSystem");



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

        Label labelError = new Label();
        labelError.setVisible(false);
        labelError.setStyleName("labelError");
        buttonBar.add(labelError);


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

        Button btnOpen = new Button("Open");
        grid.setWidget(0, 1, btnOpen);
        btnOpen.setWidth("125px");

        btnOpen.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                formOpen.center();
            }
        });


        grid.setWidget(1, 0, btnSave);
        btnSave.setWidth("125px");
        btnSave.setEnabled(false);

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

