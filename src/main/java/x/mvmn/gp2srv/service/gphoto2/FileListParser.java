package x.mvmn.gp2srv.service.gphoto2;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileListParser {

	public static class CameraFileRefsCollected {
		private final Map<String, Map<String, CameraFileRef>> byFolder;
		private final Map<Integer, CameraFileRef> byRefId;

		public CameraFileRefsCollected(final Map<String, Map<String, CameraFileRef>> byFolder, final Map<Integer, CameraFileRef> byRefId) {
			super();
			this.byFolder = byFolder;
			this.byRefId = byRefId;
		}

		public Map<String, Map<String, CameraFileRef>> getByFolder() {
			return byFolder;
		}

		public Map<Integer, CameraFileRef> getByRefId() {
			return byRefId;
		}
	}

	public static class CameraFileRef {
		private final int refId;
		private final String filename;
		private final String filesizestr;
		private final String filetypestr;

		public CameraFileRef(final int refId, final String filename, final String filesizestr, final String filetypestr) {
			super();
			this.refId = refId;
			this.filename = filename;
			this.filesizestr = filesizestr;
			this.filetypestr = filetypestr;
		}

		public int getRefId() {
			return refId;
		}

		public String getFilename() {
			return filename;
		}

		public String getFilesizestr() {
			return filesizestr;
		}

		public String getFiletypestr() {
			return filetypestr;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((filename == null) ? 0 : filename.hashCode());
			result = prime * result + ((filesizestr == null) ? 0 : filesizestr.hashCode());
			result = prime * result + ((filetypestr == null) ? 0 : filetypestr.hashCode());
			result = prime * result + refId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CameraFileRef other = (CameraFileRef) obj;
			if (filename == null) {
				if (other.filename != null)
					return false;
			} else if (!filename.equals(other.filename))
				return false;
			if (filesizestr == null) {
				if (other.filesizestr != null)
					return false;
			} else if (!filesizestr.equals(other.filesizestr))
				return false;
			if (filetypestr == null) {
				if (other.filetypestr != null)
					return false;
			} else if (!filetypestr.equals(other.filetypestr))
				return false;
			if (refId != other.refId)
				return false;
			return true;
		}
	}

	protected static final Pattern FILE_ENTRY_PATTERN = Pattern
			.compile("^#(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+(\\d+ [^\\s]+)\\s+(?:|[\\d]+x[\\d]+\\s+)([^\\s]+)$");
	protected static final Pattern FOLDER_ENTRY_PATTERN = Pattern.compile("^There\\s+(?:is|are)\\s+([^\\s]+)\\s+files?\\s+in\\s+folder\\s+'([^']+)'.$");

	public static CameraFileRefsCollected parseList(final String listText) throws Exception {
		final Map<String, Map<String, CameraFileRef>> resultsByFolders = new HashMap<String, Map<String, CameraFileRef>>();
		final Map<Integer, CameraFileRef> resultsByRefIds = new HashMap<Integer, CameraFileRef>();

		final String[] lines = listText.split(ConfigParser.LINE_SEPARATOR);

		String currentFolder = null;
		for (final String line : lines) {
			if (line.startsWith("#")) {
				if (currentFolder == null) {
					throw new Exception("Unexpected line (expected folder line first): " + line);
				} else {
					final Matcher matcher = FILE_ENTRY_PATTERN.matcher(line);
					if (matcher.find()) {
						Map<String, CameraFileRef> fileRefs = resultsByFolders.get(currentFolder);
						if (fileRefs == null) {
							fileRefs = new HashMap<String, CameraFileRef>();
							resultsByFolders.put(currentFolder, fileRefs);
						}
						final CameraFileRef cfr = new CameraFileRef(Integer.parseInt(matcher.group(1)), matcher.group(2), matcher.group(4), matcher.group(5));
						fileRefs.put(cfr.getFilename(), cfr);
						resultsByRefIds.put(cfr.getRefId(), cfr);
					} else {
						throw new Exception("Unexpected line: " + line);
					}
				}
			} else if (line.startsWith("There ")) {
				final Matcher matcher = FOLDER_ENTRY_PATTERN.matcher(line);
				if (matcher.find()) {
					currentFolder = matcher.group(2);
					if (resultsByFolders.get(currentFolder) == null) {
						// Keep empty folders in results
						resultsByFolders.put(currentFolder, new HashMap<String, CameraFileRef>());
					}
				} else {
					throw new Exception("Unexpected line: " + line);
				}
			}
		}

		return new CameraFileRefsCollected(resultsByFolders, resultsByRefIds);
	}

	public static void main(final String[] args) throws Exception {
		// TODO: make unit test out of this
		final String dummyData = "There is no file in folder '/'.\n" + "There is no file in folder '/store_00020001'.\n"
				+ "There is no file in folder '/store_00020001/DCIM'.\n" + "There are 6 files in folder '/store_00020001/DCIM/100CANON'.\n"
				+ "#1     IMG_0781.JPG               rd   921 KB image/jpeg\n" + "#2     IMG_0782.JPG               rd   984 KB image/jpeg\n"
				+ "#3     IMG_0787.JPG               rd  2873 KB image/jpeg\n" + "#4     IMG_0792.JPG               rd  4888 KB image/jpeg\n"
				+ "#5     IMG_0793.JPG               rd  4893 KB image/jpeg\n" + "#6     IMG_0794.JPG               rd  4900 KB image/jpeg\n"
				+ "There is no file in folder '/store_00020001/MISC'.\n";

		final CameraFileRefsCollected results = parseList(dummyData);
		for (Map.Entry<String, Map<String, CameraFileRef>> entry : results.byFolder.entrySet()) {
			System.out.println("Folder " + entry.getKey() + " (" + entry.getValue().size() + " files)");
			for (final Map.Entry<String, CameraFileRef> cfrEntry : entry.getValue().entrySet()) {
				final CameraFileRef cfr = cfrEntry.getValue();
				if (!cfr.getFilename().equals(cfrEntry.getKey())) {
					throw new Exception("Bad mapping file with name " + cfr.getFilename() + " mapped as " + cfrEntry.getKey());
				}
				System.out.println(String.format("%s: %s - %s (%s)", cfr.getRefId(), cfr.getFilename(), cfr.getFilesizestr(), cfr.getFiletypestr()));
				if (results.byRefId.get(cfr.getRefId()) != cfr) {
					throw new Exception("Bad entry by ref ID " + cfr.getRefId());
				}
			}
		}
	}
}
