package x.mvmn.gp2srv.mock.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import x.mvmn.gp2srv.web.CameraService;
import x.mvmn.gphoto2.jna.Gphoto2Library;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraCaptureType;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;
import x.mvmn.jlibgphoto2.exception.GP2Exception;

public class MockCameraServiceImpl implements CameraService {

	protected volatile boolean closed = false;
	protected final Map<String, CameraConfigEntryBean> initialConfig;
	protected final Map<String, CameraConfigEntryBean> config = new ConcurrentHashMap<>();
	protected final Map<String, CameraFileSystemEntryBean> fsEntries = new ConcurrentHashMap<>();
	protected final AtomicInteger counter = new AtomicInteger(0);
	protected final byte[] mockPicture;

	public MockCameraServiceImpl() {
		try {
			initialConfig = Collections
					.unmodifiableMap(new Gson().fromJson(IOUtils.toString(this.getClass().getResourceAsStream("/x/mvmn/gp2srv/mock/config.json")),
							new TypeToken<Map<String, CameraConfigEntryBean>>() {
							}.getType()));
			mockPicture = IOUtils.toByteArray(this.getClass().getResourceAsStream("/x/mvmn/gp2srv/mock/picture.jpg"));
			reset();
		} catch (Exception e) {
			throw new RuntimeException("Failed to set-up mock camera service", e);
		}
	}

	protected void checkClosed() {
		if (closed) {
			throw new RuntimeException("This GP2Camera instance has already been closed.");
		}
	}

	public void reset() {
		this.closed = false;
		config.clear();
		config.putAll(initialConfig);
		fsEntries.clear();
		fsEntries.put("/photos", new CameraFileSystemEntryBean("photos", "/", true));
		counter.set(0);
	}

	@Override
	public void close() {
		checkClosed();
		this.closed = true;
	}

	@Override
	public byte[] capturePreview() {
		checkClosed();
		return mockPicture;
	}

	@Override
	public byte[] fileGetContents(String filePath, String fileName) {
		checkFileExists(filePath, fileName);
		return capturePreview();
	}

	@Override
	public CameraFileSystemEntryBean capture() {
		checkClosed();
		CameraFileSystemEntryBean newCapture = new CameraFileSystemEntryBean(String.format("photo%08d", counter.incrementAndGet()), "/photos/", false);
		fsEntries.put(newCapture.getPath() + newCapture.getName(), newCapture);
		return null;
	}

	@Override
	public CameraFileSystemEntryBean capture(GP2CameraCaptureType captureType) {
		if (!captureType.equals(GP2CameraCaptureType.IMAGE)) {
			throw new UnsupportedOperationException();
		}
		return capture();
	}

	@Override
	public String getSummary() {
		checkClosed();
		return "Mock";
	}

	@Override
	public GP2CameraEventType waitForSpecificEvent(int timeout, GP2CameraEventType expectedEventType) {
		checkClosed();
		return null;
	}

	@Override
	public GP2CameraEventType waitForEvent(int timeout) {
		checkClosed();
		return null;
	}

	protected CameraFileSystemEntryBean checkFileExists(String filePath, String fileName) {
		final CameraFileSystemEntryBean fsEntry = fsEntries.get(filePath + fileName);
		if (fsEntry == null || !fsEntry.isFile()) {
			throw new GP2Exception(Gphoto2Library.GP_ERROR_FILE_NOT_FOUND, "File not found");
		}
		return fsEntry;
	}

	@Override
	public CameraService fileDelete(String filePath, String fileName) {
		checkClosed();
		final CameraFileSystemEntryBean fsEntry = checkFileExists(filePath, fileName);
		fsEntries.remove(fsEntry.getPath() + fsEntry.getName());
		return this;
	}

	@Override
	public List<CameraFileSystemEntryBean> filesList(String path, boolean includeFiles, boolean includeFolders, boolean recursive) {
		checkClosed();
		return fsEntries.values().stream().filter(new Predicate<CameraFileSystemEntryBean>() {
			@Override
			public boolean test(CameraFileSystemEntryBean t) {
				return (t.isFile() && includeFiles) || (t.isFolder() && includeFolders);
			}
		}).collect(Collectors.toList());
	}

	@Override
	public List<CameraConfigEntryBean> getConfig() {
		checkClosed();
		return new ArrayList<>(config.values());
	}

	@Override
	public CameraService setConfig(CameraConfigEntryBean configEntry) {
		checkClosed();
		config.put(configEntry.getPath(), configEntry);
		return this;
	}

	@Override
	public CameraService releaseCamera() {
		checkClosed();
		return this;
	}
}
