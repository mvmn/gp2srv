package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2Command;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandService;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdCaptureImageAndListFiles;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdCapturePreview;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdGetAllCameraConfigurations;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdGetThumbnail;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdListFiles;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdSetSettingByIndex;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdSummary;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraControlServlet extends AbstractErrorHandlingServlet {

	private static final long serialVersionUID = 7389681375772493366L;

	protected final GPhoto2CommandService gphoto2CommandService;
	protected final VelocityContextService velocityContextService;
	protected volatile GPhoto2Command currentCommand = null;

	public CameraControlServlet(final GPhoto2CommandService gphoto2CommandService, final VelocityContextService velocityContextService,
			final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
		this.gphoto2CommandService = gphoto2CommandService;
		this.velocityContextService = velocityContextService;
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		if ("/mainsettingset".equals(request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : ""))) {
			final String key = request.getParameter("key");
			final int value = Integer.parseInt(request.getParameter("value"));
			if (!processCommandFailure(gphoto2CommandService.executeCommand(new GP2CmdSetSettingByIndex(key, value, logger)),
					makeVelocityContext(request, response), request, response, logger)) {
				try {
					response.sendRedirect(request.getContextPath() + "/mainsettings");
				} catch (final Exception e) {
					logger.error(e);
				}
			}
		}
	}

	protected Context makeVelocityContext(final HttpServletRequest request, final HttpServletResponse response) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("request", request);
		params.put("response", response);
		params.put("currentTimeMillis", System.currentTimeMillis());
		params.put("commandIsBeingExecuted", gphoto2CommandService.isProcessRunning());
		final Context velocityContext = velocityContextService.constructContextWithGlobals(params);
		return velocityContext;
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		processRequestByPath(request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : ""), makeVelocityContext(request, response),
				request, response, logger);
	}

	protected void processRequestByPath(final String requestPath, final Context velocityContext, final HttpServletRequest request,
			final HttpServletResponse response, final Logger logger) {
		if (requestPath.equals("/") || requestPath.equals("")) {
			serveTempalteUTF8Safely("camera/index.vm", velocityContext, response, logger);
		} else if (requestPath.equals("/mainsettings")) {
			final GP2CmdGetAllCameraConfigurations cameraConfigsCommand = gphoto2CommandService.executeCommand(new GP2CmdGetAllCameraConfigurations(logger));
			if (!processCommandFailure(cameraConfigsCommand, velocityContext, request, response, logger)) {
				velocityContext.put("cameraConfig", cameraConfigsCommand.getCameraConfig());
				serveTempalteUTF8Safely("camera/mainsettings.vm", velocityContext, response, logger);
			}
		} else if (requestPath.equals("/summary")) {
			final GP2CmdSummary cmd = gphoto2CommandService.executeCommand(new GP2CmdSummary(logger));
			populateContextWithGenericCommandResults(cmd, velocityContext);
			serveTempalteUTF8Safely("camera/textcommandresponse.vm", velocityContext, response, logger);
		} else if (requestPath.equals("/browse")) {
			final GP2CmdListFiles listFilesCommand = new GP2CmdListFiles(logger);
			if (!processCommandFailure(gphoto2CommandService.executeCommand(listFilesCommand), velocityContext, request, response, logger)) {
				velocityContext.put("filesList", listFilesCommand.getFilesList());
				serveTempalteUTF8Safely("camera/browse.vm", velocityContext, response, logger);
			}
		} else if (requestPath.equals("/preview")) {
			serveTempalteUTF8Safely("camera/preview.vm", velocityContext, response, logger);
		} else if (requestPath.equals("/refreshpreview")) {
			final Integer lastPreviewRetriesCount = (Integer) velocityContextService.getGlobalContext().get("lastPreviewRetriesCount");
			int previewRetriesCount = lastPreviewRetriesCount == null ? 0 : lastPreviewRetriesCount.intValue();
			boolean success = false;
			while (!success && previewRetriesCount < 5) {
				final GP2CmdCapturePreview command = gphoto2CommandService.executeCommand(new GP2CmdCapturePreview(logger, "thumb.jpg", true,
						previewRetriesCount));
				if (command.isResultedWithPossibleTimingFailure()) {
					previewRetriesCount++;
				} else {
					if (command.getRawErrorOutput() != null && command.getRawErrorOutput().trim().length() > 0) {
						processCommandFailure(command, velocityContext, request, response, logger);
						break;
					} else {
						success = true;
					}
				}
			}
			if (success) {
				velocityContextService.getGlobalContext().put("lastPreviewRetriesCount", previewRetriesCount);
				try {
					response.sendRedirect(request.getContextPath() + "/preview");
				} catch (final IOException e) {
					logger.error(e);
				}
			}
		} else if (requestPath.equals("/captureandrefreshpreview")) {
			final GP2CmdCaptureImageAndListFiles cmdCaptureAndList = new GP2CmdCaptureImageAndListFiles(null, logger);
			if (!processCommandFailure(gphoto2CommandService.executeCommand(cmdCaptureAndList), velocityContext, request, response, logger)) {
				final String newlyCapturedImagePath = cmdCaptureAndList.getResultFile();
				boolean proceed = true;
				if (newlyCapturedImagePath != null && cmdCaptureAndList.getFilesList() != null && cmdCaptureAndList.getFilesList().getByFolder() != null
						&& cmdCaptureAndList.getFilesList().getByRefId() != null) {
					final int indexOfLastSlash = newlyCapturedImagePath.lastIndexOf("/");
					final String fileName = newlyCapturedImagePath.substring(indexOfLastSlash + 1);
					final String folderPath = newlyCapturedImagePath.substring(0, indexOfLastSlash);
					final Map<String, CameraFileRef> filesInFolder = cmdCaptureAndList.getFilesList().getByFolder().get(folderPath);
					if (filesInFolder != null) {
						final CameraFileRef resultFileRef = filesInFolder.get(fileName);
						if (resultFileRef != null) {
							if (processCommandFailure(
									gphoto2CommandService.executeCommand(new GP2CmdGetThumbnail(resultFileRef.getRefId(), "thumb.jpg", logger)),
									velocityContext, request, response, logger)) {
								proceed = false;
							}
						}
					}
				}

				if (proceed) {
					try {
						response.sendRedirect(request.getContextPath() + "/preview");
					} catch (final IOException e) {
						logger.error(e);
					}
				}
			}
		} else {
			returnNotFound(request, response);
		}
	}

	protected boolean processCommandFailure(final GPhoto2Command cmd, final Context velocityContext, final HttpServletRequest request,
			final HttpServletResponse response, final Logger logger) {
		final boolean processed;
		if (cmd.getExitCode() != 0) {
			processed = true;
			serveGenericErrorPage(request, response, cmd.getExitCode(),
					((cmd.getRawErrorOutput() != null ? cmd.getRawErrorOutput().trim() : "") + "\n\n" + (cmd.getRawStandardOutput() != null ? cmd
							.getRawStandardOutput().trim() : "")));
		} else {
			processed = false;
		}
		return processed;
	}

	protected void populateContextWithGenericCommandResults(final GPhoto2Command cmd, final Context velocityContext) {
		velocityContext.put("commandResponseStdout", cmd.getRawStandardOutput());
		velocityContext.put("commandResponseStderr", cmd.getRawErrorOutput());
		velocityContext.put("commandExitCode", cmd.getExitCode());
	}
}
