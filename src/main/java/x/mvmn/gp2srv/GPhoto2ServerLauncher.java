package x.mvmn.gp2srv;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import x.mvmn.log.api.Logger.LogLevel;

public class GPhoto2ServerLauncher {

	public static void main(String[] args) throws Exception {
		Integer port = null;
		final Options cliOptions = new Options();

		cliOptions.addOption("usemocks", false, "Use mocks instead of real gphoto2 - for code testing.");
		cliOptions.addOption("port", true, "HTTP port.");
		cliOptions.addOption("gphoto2path", true, "Path to gphoto2 executable.");
		cliOptions.addOption("logLevel", true, "Log level (TRACE, DEBUG, INFO, WARN, ERROR, SEVERE, FATAL).");

		final CommandLine commandLine = new PosixParser().parse(cliOptions, args);
		if (commandLine.hasOption("port")) {
			String portOptionVal = commandLine.getOptionValue("port");
			try {
				int parsedPort = Integer.parseInt(portOptionVal.trim());
				if (parsedPort < 1 || parsedPort > 65535) {
					throw new RuntimeException("Bad port value: " + parsedPort);
				} else {
					port = parsedPort;
				}
			} catch (NumberFormatException e) {
				throw new RuntimeException("Unable to parse port parameter as integer: '" + portOptionVal + "'.");
			}
		}

		final LogLevel logLevel;
		if (commandLine.hasOption("logLevel")) {
			try {
				logLevel = LogLevel.valueOf(commandLine.getOptionValue("logLevel").trim());
			} catch (Exception e) {
				throw new RuntimeException("Unable to parse logLevel parameter: '" + cliOptions.getOption("logLevel").getValue() + "'.s");
			}
		} else {
			logLevel = LogLevel.INFO;
		}

		new GPhoto2Server(port, logLevel, commandLine.hasOption("usemocks")).start().join();
	}
}
