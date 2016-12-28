package x.mvmn.gp2srv.web.service.impl;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import x.mvmn.gp2srv.web.CameraService;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera;
import x.mvmn.jlibgphoto2.GP2CameraFilesHelper;
import x.mvmn.jlibgphoto2.GP2ConfigHelper;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraCaptureType;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;

public class CameraServiceImpl implements CameraService, Closeable {

	private GP2Camera camera;

	public CameraServiceImpl(final GP2Camera camera) {
		this.camera = camera;
	}

	public void close() {
		camera.close();
	}

	public byte[] capturePreview() {
		return camera.capturePreview();
	}

	public CameraServiceImpl releaseCamera() {
		camera.release();
		return this;
	}

	public CameraFileSystemEntryBean capture() {
		return camera.capture();
	}

	public CameraFileSystemEntryBean capture(final GP2CameraCaptureType captureType) {
		return camera.capture(captureType);
	}

	public String getSummary() {
		return camera.getSummary();
	}

	public GP2CameraEventType waitForSpecificEvent(int timeout, GP2CameraEventType expectedEventType) {
		return camera.waitForSpecificEvent(timeout, expectedEventType);
	}

	public GP2CameraEventType waitForEvent(int timeout) {
		return camera.waitForEvent(timeout);
	}

	public List<CameraConfigEntryBean> getConfig() {
		return GP2ConfigHelper.getConfig(camera);
	}

	public CameraServiceImpl setConfig(CameraConfigEntryBean configEntry) {
		GP2ConfigHelper.setConfig(camera, configEntry);
		return this;
	}

	public List<CameraFileSystemEntryBean> filesList(final String path, boolean includeFiles, boolean includeFolders, boolean recursive) {
		return GP2CameraFilesHelper.list(camera, path, includeFiles, includeFolders, recursive);
	}

	public CameraServiceImpl fileDelete(final String filePath, final String fileName) {
		GP2CameraFilesHelper.deleteCameraFile(camera, filePath, fileName);
		return this;
	}

	public byte[] fileGetContents(final String filePath, final String fileName) {
		return GP2CameraFilesHelper.getCameraFileContents(camera, filePath, fileName);
	}

	public Map<String, CameraConfigEntryBean> getConfigAsMap() {
		final List<CameraConfigEntryBean> config = this.getConfig();
		final Map<String, CameraConfigEntryBean> configMap = new TreeMap<String, CameraConfigEntryBean>();
		for (CameraConfigEntryBean configEntry : config) {
			configMap.put(configEntry.getPath(), configEntry);
		}
		return configMap;
	}
}
