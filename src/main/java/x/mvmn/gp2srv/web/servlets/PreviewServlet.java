package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.util.ImageUtil;

public final class PreviewServlet extends HttpServlet {
    private final CameraService cameraService;
    private final boolean convertToJpg;

    public PreviewServlet(final CameraService cameraService, final boolean convertToJpg) {
        this.cameraService = cameraService;
        this.convertToJpg = convertToJpg;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/jpeg");
        try (OutputStream outputStream = response.getOutputStream()) {
            byte[] jpeg;
            jpeg = cameraService.capturePreview();
            if(convertToJpg && !ImageUtil.isJPEG(jpeg)) {
                jpeg = ImageUtil.convertToJPEG(jpeg);
            }
            outputStream.write(jpeg);
            outputStream.flush();
        }
    }
}