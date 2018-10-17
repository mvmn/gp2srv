package x.mvmn.gp2srv.camera.service.impl;

import java.io.Closeable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import x.mvmn.gp2srv.camera.CameraProvider;
import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraCaptureType;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;
import x.mvmn.jlibgphoto2.GP2CameraFilesHelper;
import x.mvmn.jlibgphoto2.GP2ConfigHelper;

public class CameraServiceImpl implements CameraService, Closeable {

	private final CameraProvider cameraProvider;

	public CameraServiceImpl(final CameraProvider camera) {
		this.cameraProvider = camera;
	}

	public void close() {
		cameraProvider.getCamera().close();
	}

	public synchronized byte[] capturePreview() {
		return cameraProvider.getCamera().capturePreview();
	}

	public synchronized CameraServiceImpl releaseCamera() {
		cameraProvider.getCamera().release();
		return this;
	}

	public synchronized CameraFileSystemEntryBean capture() {
		return cameraProvider.getCamera().capture();
	}

	public synchronized CameraFileSystemEntryBean capture(final GP2CameraCaptureType captureType) {
		return cameraProvider.getCamera().capture(captureType);
	}

	public synchronized String getSummary() {
		return cameraProvider.getCamera().getSummary();
	}

	public GP2CameraEventType waitForSpecificEvent(int timeout, GP2CameraEventType expectedEventType) {
		return cameraProvider.getCamera().waitForSpecificEvent(timeout, expectedEventType);
	}

	public GP2CameraEventType waitForEvent(int timeout) {
		return cameraProvider.getCamera().waitForEvent(timeout);
	}

	public synchronized List<CameraConfigEntryBean> getConfig() {
		return GP2ConfigHelper.getConfig(cameraProvider.getCamera());
	}

	public synchronized CameraServiceImpl setConfig(CameraConfigEntryBean configEntry) {
		GP2ConfigHelper.setConfig(cameraProvider.getCamera(), configEntry);
		return this;
	}

	public synchronized List<CameraFileSystemEntryBean> filesList(final String path, boolean includeFiles, boolean includeFolders, boolean recursive) {
		return GP2CameraFilesHelper.list(cameraProvider.getCamera(), path, includeFiles, includeFolders, recursive);
	}

	public synchronized CameraServiceImpl fileDelete(final String filePath, final String fileName) {
		GP2CameraFilesHelper.deleteCameraFile(cameraProvider.getCamera(), filePath, fileName);
		return this;
	}

	public CameraProvider getCameraProvider() {
		return cameraProvider;
	}

	public String downloadFile(final String cameraFilePath, final String cameraFileName, final File downloadFolder) {
		String result = null;
		byte[] content = fileGetContents(cameraFilePath, cameraFileName);
		File targetFile = new File(downloadFolder, cameraFileName);
		long i = 0;
		while (targetFile.exists()) {
			int dotIndex = cameraFileName.lastIndexOf(".");
			if (dotIndex > 0) {
				targetFile = new File(downloadFolder, cameraFileName.substring(0, dotIndex) + "_" + (i++) + "." + cameraFileName.substring(dotIndex + 1));
			} else {
				targetFile = new File(downloadFolder, cameraFileName + (i++));
			}
		}
		try {
			FileUtils.writeByteArrayToFile(targetFile, content);
			result = "Ok";
		} catch (Exception e) {
			result = "Error: " + e.getClass().getName() + " " + e.getMessage();
		}
		return result.trim();
	}

	public synchronized byte[] fileGetContents(final String filePath, final String fileName) {
		return GP2CameraFilesHelper.getCameraFileContents(cameraProvider.getCamera(), filePath, fileName);
	}

	public synchronized byte[] fileGetThumb(final String filePath, final String fileName) {
		return GP2CameraFilesHelper.getCameraFileContents(cameraProvider.getCamera(), filePath, fileName, true);
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
