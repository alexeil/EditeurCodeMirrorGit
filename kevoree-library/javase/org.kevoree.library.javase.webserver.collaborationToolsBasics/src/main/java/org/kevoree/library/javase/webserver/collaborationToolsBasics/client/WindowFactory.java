package org.kevoree.library.javase.webserver.collaborationToolsBasics.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Window;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/31/12
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class  WindowFactory {

    private static Window window;

    public static void setParameters(Window windowParam, String title, int width, int height, Boolean ShowMinimizeButton,
                                     Boolean IsModal, Boolean setShowModalMask,Boolean setAutoSize){
        window = windowParam;

        window.setTitle(title);
        window.setWidth(width);
        window.setHeight(height);
        window.setShowMinimizeButton(ShowMinimizeButton);
        window.setIsModal(IsModal);
        window.setShowModalMask(setShowModalMask);
        window.centerInPage();
        window.setAutoSize(setAutoSize);


        // TODO TEST
        window.setPadding(10);
        window.setAutoCenter(true);
        window.setAlign(Alignment.CENTER);
    }
}
