package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;

public class GP2CmdSummaryTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() throws Exception {
		final GPhoto2CommandResult<String> result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdSummary(LOGGER));
		TestCase.assertEquals(MOCK_PROPERTIES.get("--summary"), result.getStandardOutput());
		TestCase.assertEquals(0, result.getExitCode());
	}
}
