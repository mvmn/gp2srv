package x.mvmn.gp2srv.camera;

import java.io.File;
import java.util.List;
import java.util.Map;

import x.mvmn.jlibgphoto2.api.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.api.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.api.GP2Camera.GP2CameraCaptureType;
import x.mvmn.jlibgphoto2.api.GP2Camera.GP2CameraEventType;

public interface CameraService {
	public void close();

	public byte[] capturePreview();

	public CameraService releaseCamera();

	public CameraFileSystemEntryBean capture();

	public CameraFileSystemEntryBean capture(GP2CameraCaptureType captureType);

	public String getSummary();

	public GP2CameraEventType waitForSpecificEvent(int timeout, GP2CameraEventType expectedEventType);

	public GP2CameraEventType waitForEvent(int timeout);

	public List<CameraConfigEntryBean> getConfig();

	public Map<String, CameraConfigEntryBean> getConfigAsMap();

	public CameraService setConfig(CameraConfigEntryBean configEntry);

	public List<CameraFileSystemEntryBean> filesList(final String path, boolean includeFiles, boolean includeFolders, boolean recursive);

	public CameraService fileDelete(final String filePath, final String fileName);

	public byte[] fileGetContents(final String filePath, final String fileName);

	public byte[] fileGetThumb(final String filePath, final String fileName);

	public String downloadFile(final String cameraFilePath, final String cameraFileName, final File downloadFolder);

	public CameraProvider getCameraProvider();
}