package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

public class GP2CmdCaptureImageTest extends MockGPhoto2ExecTest {
	@Test
	public void testResultParsing() throws Exception {
		GP2CmdCaptureImage result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdCaptureImage(null, LOGGER));
		TestCase.assertEquals("/store_00020001/DCIM/100CANON/IMG_0849.JPG", result.getResultFile());
	}
}
