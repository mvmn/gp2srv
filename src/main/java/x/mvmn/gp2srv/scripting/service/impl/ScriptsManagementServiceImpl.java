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

public class ScriptsManagementServiceImpl {
	private static final FilenameFilter SCRIPT_FILENAME_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".gp2srv_script");
		}
	};

	protected final File scriptsFolder;
	protected final Gson gson = new GsonBuilder().create();

	public ScriptsManagementServiceImpl(final File scriptsFolder) {
		this.scriptsFolder = scriptsFolder;
	}

	public List<String> listScriptFiles() {
		return Arrays.asList(scriptsFolder.list(SCRIPT_FILENAME_FILTER));
	}

	public void save(String name, List<ScriptStep> steps) throws IOException {
		name = name == null ? "unknown" : name.replaceAll("[^A-Za-z_-\\.]", "_");
		File file = new File(scriptsFolder, name + ".gp2srv_script");
		FileUtils.write(file, gson.toJson(steps), StandardCharsets.UTF_8, false);
	}

	public List<ScriptStep> load(String name) throws IOException {
		name = name == null ? "unknown" : name.replaceAll("[^A-Za-z_-\\.]", "_");
		File file = new File(scriptsFolder, name + ".gp2srv_script");
		return Arrays.asList(gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), ScriptStep[].class));
	}
}
