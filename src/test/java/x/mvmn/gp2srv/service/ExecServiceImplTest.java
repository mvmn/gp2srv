package x.mvmn.gp2srv.service;

import org.junit.Test;

import x.mvmn.gp2srv.service.ExecServiceImpl;
import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.log.PrintStreamLogger;
import junit.framework.TestCase;

public class ExecServiceImplTest {

	@Test
	public void testEcho() throws Exception {
		final ExecServiceImpl svc = new ExecServiceImpl(new PrintStreamLogger(System.out));
		final ExecResult resultOne = svc.execCommandSync(new String[] { "echo", "testechoutput" }, null, null);
		TestCase.assertEquals(0, resultOne.getExitCode());
		TestCase.assertEquals("testechoutput", resultOne.getStandardOutput().trim());
		TestCase.assertEquals("", resultOne.getErrorOutput());
	}
}
