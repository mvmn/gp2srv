package x.mvmn.gp2srv;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.cli.Options;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.gp2srv.web.servlets.AbstractErrorHandlingServlet;
import x.mvmn.gp2srv.web.servlets.AdminServlet;
import x.mvmn.gp2srv.web.servlets.ImagesServlet;
import x.mvmn.gp2srv.web.servlets.StaticsResourcesServlet;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.PrintStreamLogger;
import x.mvmn.log.api.Logger;

public class GPhoto2Server implements Provider<TemplateEngine> {

	private static final String DEFAULT_CONTEXT_PATH = "/";
	private static final int DEFAULT_PORT = 8080;

	public static void main(String[] args) throws Exception {
		Integer port = null;
		Options cliOptions = new Options();
		if (cliOptions.hasOption("port")) {
			String portOptionVal = cliOptions.getOption("port").getValue();
			try {
				int parsedPort = Integer.parseInt(portOptionVal.trim());
				if (parsedPort < 1 || parsedPort > 65535) {
					throw new RuntimeException("Bad port value: " + parsedPort);
				} else {
					port = parsedPort;
				}
			} catch (NumberFormatException e) {
				throw new RuntimeException("Unable to parse port parameter as integer: '" + portOptionVal + "'");
			}
		}
		new GPhoto2Server(port).start().join();
	}

	public GPhoto2Server() {
		this(DEFAULT_CONTEXT_PATH, DEFAULT_PORT);
	}

	public GPhoto2Server(String contextPath) {
		this(contextPath, DEFAULT_PORT);
	}

	public GPhoto2Server(Integer port) {
		this(DEFAULT_CONTEXT_PATH, port);
	}

	private final Server server;
	private volatile TemplateEngine templateEngine;
	private final VelocityContextService contextService;
	private final Logger logger;

	public GPhoto2Server(String contextPath, Integer port) {
		this.logger = makeLogger();

		logger.info("Initializing...");

		try {
			if (contextPath == null) {
				contextPath = DEFAULT_CONTEXT_PATH;
			}
			if (port == null) {
				port = DEFAULT_PORT;
			}

			this.templateEngine = makeTemplateEngine();
			this.contextService = new VelocityContextService();

			this.server = new Server(port);
			this.server.setStopAtShutdown(true);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath(contextPath);
			String userHome = System.getProperty("user.home");
			// File appHomeFolder = new File(userHome + File.separator + ".gp2srv");
			// appHomeFolder.mkdir();
			File imagesFolder = new File(userHome + File.separator + ".gp2srv" + File.separator + "img");
			imagesFolder.mkdirs();
			context.addServlet(new ServletHolder(new ImagesServlet(this, imagesFolder, logger)), "/img/*");
			context.addServlet(new ServletHolder(new StaticsResourcesServlet(this, logger)), "/static/*");
			context.addServlet(new ServletHolder(new AdminServlet(this)), "/admin/*");
			context.setErrorHandler(new ErrorHandler() {
				private AbstractErrorHandlingServlet eh = new AbstractErrorHandlingServlet(GPhoto2Server.this, GPhoto2Server.this.getLogger()) {
					private static final long serialVersionUID = -30520483617261093L;
				};

				@Override
				protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) {
					eh.serveGenericErrorPage(request, writer, code, message);
				}
			});

			server.setHandler(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.info("Initializing: done.");
	}

	public void reReadTemplates() {
		try {
			this.templateEngine = makeTemplateEngine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Logger makeLogger() {
		return new PrintStreamLogger(System.out);
	}

	public Logger getLogger() {
		return this.logger;
	}

	protected TemplateEngine makeTemplateEngine() throws IOException {
		Map<String, String> templatesRegistrations = new HashMap<String, String>();
		{
			Properties templatesListProps = new Properties();
			templatesListProps.load(GPhoto2Server.class.getResourceAsStream(TemplateEngine.DEFAULT_TEMPLATES_CLASSPATH_PREFIX + "templates_list.properties"));
			for (Object templateNameObj : templatesListProps.keySet()) {
				String key = templateNameObj.toString();
				templatesRegistrations.put(key, templatesListProps.getProperty(key));
			}
		}
		return new TemplateEngine(templatesRegistrations);
	}

	public GPhoto2Server start() throws Exception {
		logger.info("Starting server...");
		this.server.start();
		logger.info("Starting server: done.");
		return this;
	}

	public GPhoto2Server stop() throws Exception {
		this.server.stop();
		return this;
	}

	public GPhoto2Server join() throws Exception {
		this.server.join();
		return this;
	}

	@Override
	public TemplateEngine provide() {
		return templateEngine;
	}
}
