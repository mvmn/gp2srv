package x.mvmn.gp2srv.scripting.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import x.mvmn.gp2srv.scripting.model.ScriptStep;
import x.mvmn.log.api.Logger;

public class ScriptsManagementServiceImpl {
	private static final FilenameFilter SCRIPT_FILENAME_FILTER = new FilenameFilter() {
		public boolean accept(final File dir, final String name) {
			return name.toLowerCase().endsWith(".gp2srv_script");
		}
	};

	protected final File scriptsFolder;
	protected final Gson gson = new GsonBuilder().create();

	public ScriptsManagementServiceImpl(final File scriptsFolder, final Logger logger) {
		this.scriptsFolder = scriptsFolder;
	}

	public List<String> listScriptFiles() {
		return Arrays.asList(scriptsFolder.list(SCRIPT_FILENAME_FILTER));
	}

	public String save(final String name, List<ScriptStep> steps) throws IOException {
		File file = new File(scriptsFolder, normalizeScriptName(name));
		FileUtils.write(file, gson.toJson(steps), StandardCharsets.UTF_8, false);
		return name;
	}

	public List<ScriptStep> load(final String name) throws IOException {
		File file = new File(scriptsFolder, normalizeScriptName(name));
		return file.exists() ? Arrays.asList(gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), ScriptStep[].class)) : null;
	}

	public String normalizeScriptName(final String name) {
		String scriptName = (name == null ? "unknown" : name.replaceAll("[^A-Za-z0-9_\\-\\.]", "_"));
		if (!scriptName.endsWith(".gp2srv_script")) {
			scriptName += ".gp2srv_script";
		}
		return scriptName;
	}
}
