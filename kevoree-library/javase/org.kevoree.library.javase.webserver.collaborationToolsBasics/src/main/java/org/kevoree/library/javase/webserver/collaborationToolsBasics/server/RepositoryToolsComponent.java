package org.kevoree.library.javase.webserver.collaborationToolsBasics.server;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.DictionaryAttribute;
import org.kevoree.annotation.DictionaryType;
import org.kevoree.annotation.Library;
import org.kevoree.library.javase.webserver.FileServiceHelper;
import org.kevoree.library.javase.webserver.KevoreeHttpRequest;
import org.kevoree.library.javase.webserver.KevoreeHttpResponse;
import org.kevoree.library.javase.webserver.ParentAbstractPage;
import org.kevoree.library.javase.webserver.servlet.LocalServletRegistry;
import scala.collection.immutable.List;

import javax.servlet.ServletContextListener;

/**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/31/12
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
@Library(name = "JavaSE")
@ComponentType
@DictionaryType({
		@DictionaryAttribute(name = "directoryPath", defaultValue = "/tmp/")
})
public class RepositoryToolsComponent extends ParentAbstractPage {

	private LocalServletRegistry servletRepository = null;


	public void startPage () {

		servletRepository = new LocalServletRegistry() {
			@Override
			public String getCDefaultPath () {
				return "/ihmcodemirror";
			}

			@Override
			public List<ServletContextListener> listeners () {
				return null;
			}

			@Override
			public void listeners_$eq (List<ServletContextListener> listeners) {
			}
		};
		super.startPage();
		servletRepository.registerServlet("/ihmcodemirror/htmleditor", new RepositoryToolsServicesImpl(this.getDictionary().get("directoryPath").toString()));
		servletRepository.registerServlet("/ihmcodemirror/systemFileServices", new StructureServiceImpl());
		servletRepository.registerServlet("/ihmcodemirror/upload", new UploadFileServer());
	}

	@Override
	public KevoreeHttpResponse process (KevoreeHttpRequest request, KevoreeHttpResponse response) {
		String pattern = this.getDictionary().get("urlpattern").toString();
		pattern = pattern.replace("**", "");
		if (request.getUrl().equals(pattern)) {
			if (FileServiceHelper.checkStaticFile("IHMcodeMirror.html", this, request, response)) {
				logger.debug("The IHMcodeMirror.html has been returned to respond to the request: {}", request.getUrl());
				return response;
			}
		}

		boolean res = servletRepository.tryURL(request.getUrl(), request, response);
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
