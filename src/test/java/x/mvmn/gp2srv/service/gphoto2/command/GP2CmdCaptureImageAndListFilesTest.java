package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;

public class GP2CmdCaptureImageAndListFilesTest extends MockGPhoto2ExecTest {

	@Test
	public void testResultParsing() throws Exception {
		GP2CmdCaptureImageAndListFiles result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdCaptureImageAndListFiles(null, LOGGER));
		TestCase.assertEquals(0, result.getExitCode());
		TestCase.assertEquals("/store_00020001/DCIM/100CANON/IMG_0850.JPG", result.getResultFile());
		TestCase.assertEquals(2, result.getFilesList().getByRefId().size());
		TestCase.assertEquals(5, result.getFilesList().getByFolder().size());
		TestCase.assertEquals(0, result.getFilesList().getByFolder().get("/").size());
		TestCase.assertEquals(0, result.getFilesList().getByFolder().get("/store_00020001").size());
		TestCase.assertEquals(0, result.getFilesList().getByFolder().get("/store_00020001/MISC").size());
		TestCase.assertEquals(0, result.getFilesList().getByFolder().get("/store_00020001/DCIM").size());
		TestCase.assertEquals(2, result.getFilesList().getByFolder().get("/store_00020001/DCIM/100CANON").size());
		TestCase.assertEquals(new CameraFileRef(1, "IMG_0849.JPG", "2971 KB", "image/jpeg"),
				result.getFilesList().getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0849.JPG"));
		TestCase.assertEquals(new CameraFileRef(2, "IMG_0850.JPG", "2942 KB", "image/jpeg"),
				result.getFilesList().getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0850.JPG"));
	}
}
