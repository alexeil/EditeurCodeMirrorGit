package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/30/12
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolStripMenu extends ToolStrip {
    private IHMcodeMirror IHMCodeMirror;
    private FormNew formNew;
    private FormOpen formOpen;
    private ToolStripButton createFile;
    private ToolStripButton createFolder;
    private ToolStripButton importFiles;
    private ToolStripButton btnSave;

    public ToolStripMenu(){
        loadMenu();
    }

    public ToolStripMenu(IHMcodeMirror IHM, FormNew formNewRepo, FormOpen formOpenrepo){
        this.IHMCodeMirror = IHM;
        this.formNew = formNewRepo;
        this.formOpen = formOpenrepo;
        loadMenu();
    }

    public void loadMenu(){
        this.setWidth(600);

        //push all buttons to the right
        this.addFill();

        ToolStripButton btnNouveau = new ToolStripButton("New");
        btnNouveau.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                formNew.center();
            }
        });
        this.addButton(btnNouveau);

        ToolStripButton btnOpen = new ToolStripButton("Open");
        btnOpen.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                formOpen.center();
            }
        });
        this.addButton(btnOpen);

        this.addSeparator();
        btnSave = new ToolStripButton("Save");
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                IHMCodeMirror.pushContentEditorToRepo();
            }
        });
        this.addButton(btnSave);
        btnSave.setDisabled(true);

        createFile = new ToolStripButton("Create File");
        createFile.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                FormTestWindow formTestWindow = new FormTestWindow(null,null,true,null);
               /* RootPanel.get().add(new HTML(" LOLILOL "));
                RootPanel.get().add(new HTML(" LOLILOL " +(IHMCodeMirror.getAbstractItemRoot()==null)));
                FormAddFileFromButton formAddFileFromButton = new FormAddFileFromButton(IHMCodeMirror);
                formAddFileFromButton.center();
                RootPanel.get().add(new HTML(" LOLILOL "));   */
            }
        });
        this.addButton(createFile);
        createFile.setDisabled(true);

        createFolder = new ToolStripButton("Create Folder");
        createFolder.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                formNew.center();
            }
        });
        this.addButton(createFolder);
        createFolder.setDisabled(true);

        importFiles = new ToolStripButton("Import");
        importFiles.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                formNew.center();
            }
        });
        this.addButton(importFiles);
        importFiles.setDisabled(true);

        this.draw();
    }

    public void disableButtons() {
        btnSave.setDisabled(true);
        createFile.setDisabled(true);
        createFolder.setDisabled(true);
        importFiles.setDisabled(true);
    }

    public void enableButtons() {
        btnSave.setDisabled(false);
        createFile.setDisabled(false);
        createFolder.setDisabled(false);
        importFiles.setDisabled(false);
    }

    public void setIHMCodeMirror(IHMcodeMirror IHMCodeMirror) {
        this.IHMCodeMirror = IHMCodeMirror;
    }

    public void setFormNew(FormNew formNew) {
        this.formNew = formNew;
    }

    public void setFormOpen(FormOpen formOpen) {
        this.formOpen = formOpen;
    }

    public ToolStripButton getBtnSave(){
        return btnSave;
    }
}
