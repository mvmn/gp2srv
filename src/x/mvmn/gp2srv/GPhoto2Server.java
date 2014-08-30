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

import x.mvmn.gp2srv.service.ExecServiceImpl;
import x.mvmn.gp2srv.service.PathFinderHelper;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandService;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2ExecService;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.gp2srv.web.servlets.AbstractErrorHandlingServlet;
import x.mvmn.gp2srv.web.servlets.CameraControlServlet;
import x.mvmn.gp2srv.web.servlets.DevModeServlet;
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
		final String gphoto2path;
		if (cliOptions.hasOption("gphoto2path") && cliOptions.getOption("gphoto2path").getValue() != null
				&& cliOptions.getOption("gphoto2path").getValue().trim().length() > 0) {
			gphoto2path = cliOptions.getOption("gphoto2path").getValue().trim();
		} else {
			gphoto2path = PathFinderHelper.findInPath("gphoto2", true).getAbsolutePath();
		}
		new GPhoto2Server(gphoto2path, port).start().join();
	}

	public GPhoto2Server(String pathToGphoto2) {
		this(pathToGphoto2, DEFAULT_CONTEXT_PATH, DEFAULT_PORT);
	}

	public GPhoto2Server() {
		this(null, DEFAULT_CONTEXT_PATH, DEFAULT_PORT);
	}

	public GPhoto2Server(String pathToGphoto2, Integer port) {
		this(pathToGphoto2, DEFAULT_CONTEXT_PATH, port);
	}

	public GPhoto2Server(Integer port) {
		this(null, DEFAULT_CONTEXT_PATH, port);
	}

	private final Server server;
	private final Logger logger;
	private volatile TemplateEngine templateEngine;
	private final VelocityContextService velocityContextService;
	private final GPhoto2CommandService gphoto2CommandService;
	private final String pathToGphoto2;

	public GPhoto2Server(String pathToGphoto2, String contextPath, Integer port) {
		this.logger = makeLogger();

		logger.info("Initializing...");

		try {
			if (pathToGphoto2 == null) {
				pathToGphoto2 = PathFinderHelper.findInPath("gphoto2", true).getAbsolutePath();
				if (pathToGphoto2 == null) {
					throw new RuntimeException("Unable to find gphoto2 in path.");
				}
			}
			this.pathToGphoto2 = pathToGphoto2;

			if (contextPath == null) {
				contextPath = DEFAULT_CONTEXT_PATH;
			}
			if (port == null) {
				port = DEFAULT_PORT;
			}

			this.templateEngine = makeTemplateEngine();
			this.velocityContextService = new VelocityContextService();

			this.server = new Server(port);
			this.server.setStopAtShutdown(true);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath(contextPath);
			String userHome = System.getProperty("user.home");
			File appHomeFolder = new File(userHome + File.separator + ".gp2srv");
			appHomeFolder.mkdir();
			File imagesFolder = new File(userHome + File.separator + ".gp2srv" + File.separator + "img");
			imagesFolder.mkdirs();

			this.gphoto2CommandService = new GPhoto2CommandService(new GPhoto2ExecService(new ExecServiceImpl(logger), pathToGphoto2, appHomeFolder,
					imagesFolder));

			context.addServlet(new ServletHolder(new ImagesServlet(this, imagesFolder, logger)), "/img/*");
			context.addServlet(new ServletHolder(new StaticsResourcesServlet(this, logger)), "/static/*");
			context.addServlet(new ServletHolder(new CameraControlServlet(gphoto2CommandService, velocityContextService, this, logger)), "/");
			context.addServlet(new ServletHolder(new DevModeServlet(this)), "/devmode/*");
			context.setErrorHandler(new ErrorHandler() {
				private AbstractErrorHandlingServlet eh = new AbstractErrorHandlingServlet(GPhoto2Server.this, GPhoto2Server.this.getLogger()) {
					private static final long serialVersionUID = -30520483617261093L;
				};

				@Override
				protected void handleErrorPage(final HttpServletRequest request, final Writer writer, final int code, final String message) {
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
		final Map<String, String> templatesRegistrations = new HashMap<String, String>();
		{
			final Properties templatesListProps = new Properties();
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

	public String getPathToGphoto2() {
		return pathToGphoto2;
	}
}
