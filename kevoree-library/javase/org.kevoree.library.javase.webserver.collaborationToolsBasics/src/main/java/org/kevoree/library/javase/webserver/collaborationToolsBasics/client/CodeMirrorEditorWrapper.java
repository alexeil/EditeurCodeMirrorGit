package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;


/**
* Doc can be found http://codemirror.net/doc/manual.html
* */
public class CodeMirrorEditorWrapper {

    private static String fileOpened;

    public static synchronized String getFileOpened(){
        return fileOpened;
    }

    public static synchronized void setFileOpened(String fileOpenedPath){
        fileOpened = fileOpenedPath;
    }

	public static native String getText() /*-{
		return $wnd.codeMirrorEditor.getValue();
	}-*/;

	public static native void setText(String text) /*-{
		$wnd.codeMirrorEditor.setValue(text);
	}-*/;

	public static native void addOnChangeHandler(MirrorEditorCallback callback)
	/*-{ $wnd.codeMirrorEditor.setOption("onChange",function (e,b) {
	  callback.@org.kevoree.library.javase.webserver.collaborationToolsBasics.client.MirrorEditorCallback::invokeMirrorCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
	  }); }-*/;
	 
}