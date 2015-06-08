package x.mvmn.gp2srv.service.gphoto2.command;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;
import junit.framework.TestCase;

public class GP2CmdCapturePreviewTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() throws Exception {
		final GPhoto2CommandResult<String> result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdCapturePreview(LOGGER, "thumb.jpg", true, 1));
		TestCase.assertEquals("thumb.jpg", result.getResult());
		TestCase.assertEquals(0, result.getExitCode());
	}
}
