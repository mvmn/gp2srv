package x.mvmn.gp2srv.service.gphoto2.command;

import org.junit.Test;

import junit.framework.TestCase;

public class GP2CmdCapturePreviewTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() {
		final GP2CmdCapturePreview cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdCapturePreview(LOGGER, "thumb.jpg", true, 1));
		TestCase.assertEquals("thumb.jpg", cmd.getResultFileName());
		TestCase.assertEquals(0, cmd.getExitCode());
	}
}
