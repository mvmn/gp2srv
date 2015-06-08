package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;
import x.mvmn.lang.util.ImmutablePair;

public class GP2CmdCaptureImageAndListFilesTest extends MockGPhoto2ExecTest {

	@Test
	public void testResultParsing() throws Exception {
		final GPhoto2CommandResult<ImmutablePair<String, CameraFileRefsCollected>> cmdResult = MOCK_GPHOTO2_COMMAND_SERVICE
				.executeCommand(new GP2CmdCaptureImageAndListFiles(null, LOGGER));
		TestCase.assertEquals(0, cmdResult.getExitCode());
		TestCase.assertEquals("/store_00020001/DCIM/100CANON/IMG_0850.JPG", cmdResult.getResult().getA());
		final CameraFileRefsCollected result = cmdResult.getResult().getB();
		TestCase.assertEquals(2, result.getByRefId().size());
		TestCase.assertEquals(5, result.getByFolder().size());
		TestCase.assertEquals(0, result.getByFolder().get("/").size());
		TestCase.assertEquals(0, result.getByFolder().get("/store_00020001").size());
		TestCase.assertEquals(0, result.getByFolder().get("/store_00020001/MISC").size());
		TestCase.assertEquals(0, result.getByFolder().get("/store_00020001/DCIM").size());
		TestCase.assertEquals(2, result.getByFolder().get("/store_00020001/DCIM/100CANON").size());
		TestCase.assertEquals(new CameraFileRef(1, "IMG_0849.JPG", "2971 KB", "image/jpeg"),
				result.getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0849.JPG"));
		TestCase.assertEquals(new CameraFileRef(2, "IMG_0850.JPG", "2942 KB", "image/jpeg"),
				result.getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0850.JPG"));
	}
}
