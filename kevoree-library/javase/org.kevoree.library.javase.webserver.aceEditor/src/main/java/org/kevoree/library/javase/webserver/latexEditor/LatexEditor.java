package org.kevoree.library.javase.webserver.latexEditor;

import com.google.gwt.user.server.rpc.RPC;
import org.kevoree.annotation.*;
import org.kevoree.framework.message.StdKevoreeMessage;
import org.kevoree.library.javase.fileSystem.LockFilesService;
import org.kevoree.library.javase.webserver.AbstractPage;
import org.kevoree.library.javase.webserver.FileServiceHelper;
import org.kevoree.library.javase.webserver.KevoreeHttpRequest;
import org.kevoree.library.javase.webserver.KevoreeHttpResponse;
import org.kevoree.library.javase.webserver.latexEditor.server.latexEditorServiceImpl;
import org.kevoree.library.javase.webserver.servlet.LocalServletRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.List;

import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


/**
 * Created by IntelliJ IDEA.
 * User: duke
 * Date: 17/10/11
 * Time: 09:29
 * To change this template use File | Settings | File Templates.
 */
@ComponentType
@MessageTypes({
        @MessageType(name = "COMPILE",
                elems = {@MsgElem(name = "id", className = UUID.class), @MsgElem(name = "file",
                        className = String.class)}),
        @MessageType(name = "COMPILE_CALLBACK",
                elems = {@MsgElem(name = "id", className = UUID.class), @MsgElem(name = "path",
                        className = String.class), @MsgElem(name = "log", className = String.class), @MsgElem(
                        name = "success", className = boolean.class)})
})
@Requires({
        @RequiredPort(name = "files", type = PortType.SERVICE, className = LockFilesService.class),
        @RequiredPort(name = "compile", type = PortType.MESSAGE, optional = true, messageType = "COMPILE")
})
@Provides({
        @ProvidedPort(name = "compileCallback", type = PortType.MESSAGE, messageType = "COMPILE_CALLBACK")
})
public class LatexEditor extends AbstractPage {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public List<String> waitingID = Collections.synchronizedList(new ArrayList<String>());
    public Map<String, Boolean> compileResult = Collections.synchronizedMap(new HashMap<String, Boolean>());
    public Map<String, Object> compileLog = Collections.synchronizedMap(new HashMap<String, Object>());

    private LocalServletRegistry servletRepository = null;//new LocalServletRegistry();

    @Override
    public void startPage() {
        //Bundle b = (Bundle) this.getDictionary().get("osgi.bundle");
        servletRepository = new LocalServletRegistry(/*b*/){
            @Override
            public String getCDefaultPath(){
                return "/latexEditor";
            }

            @Override
            public List<ServletContextListener> listeners() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void listeners_$eq(List<ServletContextListener> listeners) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        super.startPage();
		RPC.setClassLoader(this.getClass().getClassLoader());
        //RPC.setCurrentBundle(b); //GWT ACK
        servletRepository.registerServlet("/latexEditor/latexEditorService",new latexEditorServiceImpl(this));
    }

    @Port(name = "compileCallback")
    public void compileCallback(Object o) {
        StdKevoreeMessage msg = (StdKevoreeMessage) o;
        logger.debug("Compilation result for uuid = {} ",msg.getValue("id").toString());
        Boolean compileresult = (Boolean) msg.getValue("success").get();
        compileResult.put(msg.getValue("id").get().toString(), compileresult);
        compileLog.put(msg.getValue("id").get().toString(), msg.getValue("log").get());
        waitingID.remove(msg.getValue("id").get().toString());
    }

    @Override
    public KevoreeHttpResponse process(KevoreeHttpRequest request, KevoreeHttpResponse response) {

        if(servletRepository.tryURL(request.getUrl(),request,response)){
            return response;
        }
        if (LatexService.checkService(this, request, response)) {
            return response;
        }
        if (FileServiceHelper.checkStaticFile("latexEditor.html", this, request, response)) {
            return response;
        }
        response.setContent("Bad request");

        return response;
    }


}

