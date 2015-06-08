package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;

public class GP2CmdListFilesTest extends MockGPhoto2ExecTest {
	@Test
	public void testResultParsing() throws Exception {
		final GPhoto2CommandResult<CameraFileRefsCollected> result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdListFiles(LOGGER));
		TestCase.assertEquals(0, result.getExitCode());
		TestCase.assertEquals(4, result.getResult().getByRefId().size());
		TestCase.assertEquals(5, result.getResult().getByFolder().size());
		TestCase.assertEquals(0, result.getResult().getByFolder().get("/").size());
		TestCase.assertEquals(0, result.getResult().getByFolder().get("/store_00020001").size());
		TestCase.assertEquals(0, result.getResult().getByFolder().get("/store_00020001/MISC").size());
		TestCase.assertEquals(0, result.getResult().getByFolder().get("/store_00020001/DCIM").size());
		TestCase.assertEquals(4, result.getResult().getByFolder().get("/store_00020001/DCIM/100CANON").size());
		TestCase.assertEquals(new CameraFileRef(1, "IMG_0849.JPG", "2971 KB", "image/jpeg"),
				result.getResult().getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0849.JPG"));
		TestCase.assertEquals(new CameraFileRef(2, "IMG_0850.JPG", "2942 KB", "image/jpeg"),
				result.getResult().getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0850.JPG"));
		TestCase.assertEquals(new CameraFileRef(3, "IMG_0851.JPG", "2942 KB", "image/jpeg"),
				result.getResult().getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0851.JPG"));
		TestCase.assertEquals(new CameraFileRef(4, "IMG_0852.JPG", "2942 KB", "image/jpeg"),
				result.getResult().getByFolder().get("/store_00020001/DCIM/100CANON").get("IMG_0852.JPG"));
	}
}
