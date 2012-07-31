package org.kevoree.library.javase.webserver.portForward.test;


import org.kevoree.annotation.ComponentType;
import org.kevoree.library.javase.webserver.KevoreeHttpRequest;
import org.kevoree.library.javase.webserver.KevoreeHttpResponse;
import org.kevoree.library.javase.webserver.ParentAbstractPage;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 7/27/12
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
@ComponentType
public class compoTest extends ParentAbstractPage {
    public void startPage () {
        super.startPage();
    }

    @Override
    public KevoreeHttpResponse process(KevoreeHttpRequest kevoreeHttpRequest, KevoreeHttpResponse kevoreeHttpResponse) {
        if(kevoreeHttpRequest!=null)
            logger.debug((kevoreeHttpRequest!=null)+" kev http request" + kevoreeHttpRequest.toString());
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
