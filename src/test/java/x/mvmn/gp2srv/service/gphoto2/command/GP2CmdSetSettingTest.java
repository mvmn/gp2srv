package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

public class GP2CmdSetSettingTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() {
		final GP2CmdSetSetting cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdSetSetting("/main/imgsettings/colorspace", "sRGB", LOGGER));
		TestCase.assertEquals(0, cmd.getExitCode());
		TestCase.assertEquals("", cmd.getRawErrorOutput());
	}
}
