package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.kevoree.annotation.*;
import org.kevoree.library.javase.fileSystem.client.LockFilesService;
import org.kevoree.library.javase.fileSystemGitRepository.GitRepositoryActions;
import org.kevoree.library.javase.webserver.FileServiceHelper;
import org.kevoree.library.javase.webserver.KevoreeHttpRequest;
import org.kevoree.library.javase.webserver.KevoreeHttpResponse;
import org.kevoree.library.javase.webserver.ParentAbstractPage;
import org.kevoree.library.javase.webserver.servlet.LocalServletRegistry;

import javax.servlet.http.HttpServlet;


@Library(name = "JavaSE")
@ComponentType
@DictionaryType({
		@DictionaryAttribute(name = "directoryPath", defaultValue = "/tmp/")
})
@Requires({ @RequiredPort(name = "createRepo", type = PortType.SERVICE,
        className = GitRepositoryActions.class, optional = true, needCheckDependency = true),
        @RequiredPort(name = "files", type = PortType.SERVICE,
                className = LockFilesService.class, optional = true, needCheckDependency = true)
})
public class RepositoryToolsComponent extends ParentAbstractPage {

	private LocalServletRegistry servletRepository = null;

	public void startPage () {
		servletRepository = new LocalServletRegistry() {
			@Override
			public String getCDefaultPath () {
				return "/ihmcodemirror";
			}
		};

		super.startPage();

        RemoteServiceServlet s1 = new RepositoryToolsServicesImpl(this, this.getDictionary().get("directoryPath").toString());

        RemoteServiceServlet s2 =   new StructureServiceImpl(this);
        HttpServlet s3 =   new UploadFileServer(this);

		servletRepository.registerServlet("/ihmcodemirror/htmleditor", s1);
		servletRepository.registerServlet("/ihmcodemirror/systemFileServices", s2);
		servletRepository.registerServlet("/ihmcodemirror/upload", s3);
	}

	@Override
	public KevoreeHttpResponse process (KevoreeHttpRequest request, KevoreeHttpResponse response) {
		String pattern = this.getDictionary().get("urlpattern").toString();
		pattern = pattern.replace("**", "");
		if (request.getUrl().equals(pattern)) {
			//if (FileServiceHelper.checkStaticFile("IHMcodeMirror.html", this, request, response)) {
            if (FileServiceHelper.checkStaticFile("IHMcodeMirror.html", this, request, response)) {
				logger.debug("The IHMcodeMirror.html has been returned to respond to the request: {}", request.getUrl());
				return response;
			}
		}
		ClassLoader l = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(RepositoryToolsComponent.class.getClassLoader());

		boolean res = servletRepository.tryURL(request.getUrl(), request, response);
		Thread.currentThread().setContextClassLoader(l);
		if (res) {
			logger.debug("one servlet is able to respond to the request: {}", request.getUrl());
			return response;

		}


		if (FileServiceHelper.checkStaticFile(request.getUrl(), this, request, response)) {
			logger.debug("A static resource has been found to respond to the request: {}", request.getUrl());
			return response;
		}

		logger.warn("Unable to find the needed resource to the request: {}", request.getUrl());
		response.setContent("Bad request");
		return response;
	}
}
