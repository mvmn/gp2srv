package x.mvmn.gp2srv.service.gphoto2;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.ExecService;
import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.ExecService.ExecCallback;

public class GPhoto2ExecServiceTest {

	GPhoto2ExecService unit;
	final ExecService mockExecService = new ExecService() {
		public boolean isProcessRunning() {
			return true;
		}

		public Process getCurrentProcess() {
			return null;
		}

		public ExecResult execCommandSync(String[] command, String[] envVars, File dir) throws IOException {
			TestCase.assertEquals("/fake/imgdir", dir.getAbsolutePath());
			final String[] expectedCommand = new String[] { "/path/to/gphoto2", "--fakeCommand", "fake param 1", "fake param 2" };
			TestCase.assertEquals(expectedCommand.length, command.length);
			for (int i = 0; i < expectedCommand.length; i++) {
				TestCase.assertEquals(expectedCommand[i], command[i]);
			}
			TestCase.assertTrue(unit.isProcessRunning());
			TestCase.assertNull(unit.getCurrentProcess());
			return new ExecResult("Unit passed", "", 123);
		}

		public void execCommandAsync(ExecCallback callback, String[] command, String[] envVars, File dir) {
		}
	};

	@Test
	public void testExecCommand() throws Exception {
		unit = new GPhoto2ExecService(mockExecService, "/path/to/gphoto2", null, new File("/fake/imgdir"));
		final ExecResult result = unit.execCommand(new String[] { "--fakeCommand", "fake param 1", "fake param 2" });

		TestCase.assertEquals("Unit passed", result.getStandardOutput());
		TestCase.assertEquals("", result.getErrorOutput());
		TestCase.assertEquals(123, result.getExitCode());
	}

	@Test
	public void testExecCommandAsync() throws Exception {
		unit = new GPhoto2ExecService(mockExecService, "/path/to/gphoto2", null, new File("/fake/imgdir"));

		unit.execCommandAsync(new ExecCallback() {
			public void processResult(final ExecResult execResult) {
				TestCase.assertEquals("Unit passed", execResult.getStandardOutput());
				TestCase.assertEquals("", execResult.getErrorOutput());
				TestCase.assertEquals(123, execResult.getExitCode());
			}

			public void processError(final Throwable error) {
				throw new RuntimeException(error);
			}
		}, "fakeCommand", "fakeParam1", "fakeParam2");
	}
}
