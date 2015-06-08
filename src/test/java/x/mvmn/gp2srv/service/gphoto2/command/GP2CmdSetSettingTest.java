package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;

public class GP2CmdSetSettingTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() throws Exception {
		final GPhoto2CommandResult<Object> result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdSetSetting("/main/imgsettings/colorspace", "sRGB",
				LOGGER));
		TestCase.assertEquals(0, result.getExitCode());
		TestCase.assertEquals("", result.getErrorOutput());
	}
}
