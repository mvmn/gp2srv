package x.mvmn.gp2srv;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.gp2srv.web.servlets.AbstractErrorHandlingServlet;
import x.mvmn.gp2srv.web.servlets.CameraControlServlet;
import x.mvmn.gp2srv.web.servlets.DevModeServlet;
import x.mvmn.gp2srv.web.servlets.ImagesServlet;
import x.mvmn.gp2srv.web.servlets.StaticsResourcesServlet;
import x.mvmn.jlibgphoto2.GP2Camera;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.PrintStreamLogger;
import x.mvmn.log.api.Logger;
import x.mvmn.log.api.Logger.LogLevel;

public class GPhoto2Server implements Provider<TemplateEngine> {

	private static final String DEFAULT_CONTEXT_PATH = "/";
	private static final int DEFAULT_PORT = 8080;

	private final Server server;
	private final Logger logger;
	private volatile TemplateEngine templateEngine;
	private final VelocityContextService velocityContextService;
	private final GP2Camera camera;

	private final File userHome;
	private final File appHomeFolder;
	private final File imagesFolder;
	private final File favouredCamConfSettingsFile;
	private final Properties favouredCamConfSettings;

	public static final AtomicBoolean liveViewEnabled = new AtomicBoolean(true);
	public static final AtomicBoolean liveViewInProgress = new AtomicBoolean(false);

	public GPhoto2Server(final LogLevel logLevel, final boolean mockMode) {
		this(DEFAULT_CONTEXT_PATH, DEFAULT_PORT, logLevel, mockMode);
	}

	public GPhoto2Server(final LogLevel logLevel) {
		this(DEFAULT_CONTEXT_PATH, DEFAULT_PORT, logLevel, false);
	}

	public GPhoto2Server(Integer port, final LogLevel logLevel, final boolean mockMode) {
		this(DEFAULT_CONTEXT_PATH, port, logLevel, mockMode);
	}

	public GPhoto2Server(Integer port, final LogLevel logLevel) {
		this(DEFAULT_CONTEXT_PATH, port, logLevel, false);
	}

	public GPhoto2Server(String contextPath, Integer port, final LogLevel logLevel, final boolean mockMode) {
		this.logger = makeLogger(logLevel);

		this.camera = new GP2Camera();

		logger.info("Initializing...");

		try {
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
			userHome = new File(System.getProperty("user.home"));
			appHomeFolder = new File(userHome, ".gp2srv");
			appHomeFolder.mkdir();
			imagesFolder = new File(appHomeFolder, "img");
			imagesFolder.mkdirs();
			favouredCamConfSettingsFile = new File(appHomeFolder, "favouredConfs.properties");
			if (!favouredCamConfSettingsFile.exists()) {
				favouredCamConfSettingsFile.createNewFile();
			}
			favouredCamConfSettings = new Properties() {
				private static final long serialVersionUID = 3248780562679168446L;

				@Override
				public Object remove(final Object key) {
					final Object result = super.remove(key);
					store();
					return result;
				}

				@Override
				public Object setProperty(final String key, final String value) {
					final Object result = super.setProperty(key, value);
					store();
					return result;
				}

				protected void store() {
					FileOutputStream tmpFileWriter = null;
					try {
						tmpFileWriter = new FileOutputStream(favouredCamConfSettingsFile);
						this.store(tmpFileWriter, "GPhoto2Server - favoured camera settings");
					} catch (final Exception e) {
						throw new RuntimeException("Failed to write favoured camera conf settings file " + favouredCamConfSettingsFile.getAbsolutePath(), e);
					} finally {
						IOUtils.closeQuietly(tmpFileWriter);
					}
				}
			};
			FileReader tmpFileReader = null;
			try {
				tmpFileReader = new FileReader(favouredCamConfSettingsFile);
				favouredCamConfSettings.load(tmpFileReader);
			} catch (final Exception e) {
				throw new RuntimeException("Failed to read favoured camera conf settings file " + favouredCamConfSettingsFile.getAbsolutePath(), e);
			} finally {
				IOUtils.closeQuietly(tmpFileReader);
			}
			velocityContextService.getGlobalContext().put("favouredCamConfSettings", favouredCamConfSettings);

			final Properties mockResults;
			if (mockMode) {
				mockResults = new Properties();
				mockResults.load(this.getClass().getResourceAsStream("/x/mvmn/gp2srv/service/gphoto2/gphoto2mocks.properties"));
			} else {
				mockResults = null;
			}

			context.setErrorHandler(new ErrorHandler() {
				private AbstractErrorHandlingServlet eh = new AbstractErrorHandlingServlet(GPhoto2Server.this, GPhoto2Server.this.getLogger()) {
					private static final long serialVersionUID = -30520483617261093L;
				};

				@Override
				protected void handleErrorPage(final HttpServletRequest request, final Writer writer, final int code, final String message) {
					eh.serveGenericErrorPage(request, writer, code, message);
				}
			});

			context.addServlet(new ServletHolder(new ImagesServlet(this, imagesFolder, logger)), "/img/*");
			context.addServlet(new ServletHolder(new StaticsResourcesServlet(this, logger)), "/static/*");
			context.addServlet(new ServletHolder(new CameraControlServlet(camera, favouredCamConfSettings, velocityContextService, this, imagesFolder, logger)),
					"/");
			context.addServlet(new ServletHolder(new DevModeServlet(this)), "/devmode/*");
			final byte[] prefix = ("--BoundaryString\r\n" + "Content-type: image/jpeg\r\n" + "Content-Length: ").getBytes("UTF-8");
			final byte[] separator = "\r\n\r\n".getBytes("UTF-8");
			context.addServlet(new ServletHolder(new HttpServlet() {
				private static final long serialVersionUID = -6610127379314108183L;

				@Override
				public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
					GPhoto2Server.liveViewEnabled.set(false);
					try {
						GPhoto2Server.waitWhileLiveViewInProgress(50);
					} finally {
						GPhoto2Server.liveViewEnabled.set(true);
					}
					response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
					final OutputStream outputStream = response.getOutputStream();
					byte[] jpeg;
					while (liveViewEnabled.get()) {
						try {
							liveViewInProgress.set(true);
							jpeg = camera.capturePreview();
							outputStream.write(prefix);
							outputStream.write(String.valueOf(jpeg.length).getBytes("UTF-8"));
							outputStream.write(separator);
							outputStream.write(jpeg);
							outputStream.write(separator);
							outputStream.flush();
							System.gc();
							Thread.yield();
						} catch (final EOFException e) {
							// Ignore - this just means user closed preview
						} catch (final Exception e) {
							System.err.println("Live view stopped: " + e.getClass().getName() + " " + e.getMessage());
							break;
						} finally {
							liveViewInProgress.set(false);
						}
					}
				}
			}), "/stream.mjpeg");

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

	protected Logger makeLogger(final LogLevel logLevel) {
		return new PrintStreamLogger(System.out).setLevel(logLevel);
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

	public static void waitWhileLiveViewInProgress(int waitTime) {
		while (GPhoto2Server.liveViewInProgress.get() && waitTime-- > 0) {
			Thread.yield();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
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

	public TemplateEngine provide() {
		return templateEngine;
	}

	public GP2Camera getCamera() {
		return camera;
	}
}
