package x.mvmn.gp2srv.web.servlets;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.util.ImageUtil;

public final class LiveViewServlet extends HttpServlet {
    private static final byte[] PREFIX;
    private static final byte[] SEPARATOR;
    static {
        byte[] prefix = null;
        byte[] separator = null;
        try {
            prefix = ("--BoundaryString\r\n" + "Content-type: image/jpeg\r\n" + "Content-Length: ").getBytes("UTF-8");
            separator = "\r\n\r\n".getBytes("UTF-8");
        } catch (UnsupportedEncodingException notGonnaHappen) {
            // Will never happen
            throw new RuntimeException(notGonnaHappen);
        }
        PREFIX = prefix;
        SEPARATOR = separator;
    }
    private static final long serialVersionUID = -6610127379314108183L;
    private final CameraService cameraService;
    private final GPhoto2Server gphoto2Server;

    public LiveViewServlet(final GPhoto2Server gphoto2Server, final CameraService cameraService) {
        this.gphoto2Server = gphoto2Server;
        this.cameraService = cameraService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GPhoto2Server.liveViewEnabled.set(false);
        gphoto2Server.getLogger().debug("Waiting for live view");
        try {
            GPhoto2Server.waitWhileLiveViewInProgress(50);
        } finally {
            GPhoto2Server.liveViewEnabled.set(true);
        }
        gphoto2Server.getLogger().info("Starting live view");
        response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
        final OutputStream outputStream = response.getOutputStream();
        byte[] jpeg;
        long count = 0L;
        long lastLogTime = 0;
        Boolean needsJpegConversion = null;
        while (GPhoto2Server.liveViewEnabled.get()) {
            try {
                long now = System.currentTimeMillis();
                boolean doLog = (now - lastLogTime) > 5000;
                if (doLog) {
                    lastLogTime = now;
                }
                if (doLog) {
                    gphoto2Server.getLogger().debug("Getting preview for live view " + count);
                }
                GPhoto2Server.liveViewInProgress.set(true);
                jpeg = cameraService.capturePreview();
                if (needsJpegConversion == null) {
                    needsJpegConversion = !ImageUtil.isJPEG(jpeg);
                }
                if (needsJpegConversion.booleanValue()) {
                    if (doLog) {
                        gphoto2Server.getLogger().debug("Converting image to JPEG " + count);
                    }
                    jpeg = ImageUtil.convertToJPEG(jpeg);
                }
                outputStream.write(PREFIX);
                outputStream.write(String.valueOf(jpeg.length).getBytes("UTF-8"));
                outputStream.write(SEPARATOR);
                outputStream.write(jpeg);
                outputStream.write(SEPARATOR);
                outputStream.flush();
                System.gc();
                if (doLog) {
                    gphoto2Server.getLogger().debug("Served preview for live view " + count);
                }
                count++;
                if (count == Long.MAX_VALUE) {
                    count = 0;
                }
                Thread.yield();
            } catch (final EOFException e) {
                gphoto2Server.getLogger().info("Ending live view");
                // This just means user closed preview
                break;
            } catch (final Exception e) {
                e.printStackTrace();
                gphoto2Server.getLogger().error("Live view stopped with error: " + e.getClass().getName() + " " + e.getMessage(), e);
                break;
            } finally {
                GPhoto2Server.liveViewInProgress.set(false);
            }
        }
        gphoto2Server.getLogger().info("Stopped live view");
    }
}