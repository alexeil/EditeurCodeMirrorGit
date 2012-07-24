package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.kevoree.library.javase.fileSystem.client.LockFilesService;
import org.kevoree.library.javase.webserver.servlet.FakeServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class UploadFileServer extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(UploadFileServer.class);
    private  String uploadDirectory;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
    }

    private  RepositoryToolsComponent repositoryToolsComponent;

    public UploadFileServer(RepositoryToolsComponent repoToolsComponent){
        this.repositoryToolsComponent = repoToolsComponent;
    }

    public javax.servlet.ServletContext getServletContext() {
        return new FakeServletContext();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // process only multipart requests
        if (ServletFileUpload.isMultipartContent(req)) {
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            try {
                List<FileItem> items = upload.parseRequest(req);
                for (FileItem item : items) {
                    // process only file upload - discard other form item types
                    if (item.isFormField()){
                        uploadDirectory = item.getString();
                        continue;
                    }

                    String fileName = item.getName();
                    // get only the file name not whole path
                    if (fileName != null) {
                        fileName = FilenameUtils. getName(fileName);
                    }

                    String pathFile = uploadDirectory+fileName;
                    byte[] content = convertStream(item.getInputStream());

                    if(!repositoryToolsComponent.getPortByName("files", LockFilesService.class).saveFile(pathFile, content, true))
                        logger.debug(" Error while uploading {}", fileName);
                }
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while creating the file : " + e.getMessage());

                logger.debug(" Exeption ",e);
            }

        } else {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Request contents type is not supported by the servlet.");
        }
    }

    public byte[] convertStream (InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int l;
        do {
            l = (in.read(buffer));
            if (l > 0) {
                out.write(buffer, 0, l);
            }
        } while (l > 0);
        return out.toByteArray();
    }
}
