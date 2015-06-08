package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

public class GP2CmdSetSettingByIndexTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() {
		final GP2CmdSetSettingByIndex cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdSetSettingByIndex("/main/imgsettings/colorspace", 1, LOGGER));
		TestCase.assertEquals(0, cmd.getExitCode());
		TestCase.assertEquals("", cmd.getRawErrorOutput());
	}
}
