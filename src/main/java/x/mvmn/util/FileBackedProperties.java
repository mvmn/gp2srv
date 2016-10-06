package x.mvmn.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public final class FileBackedProperties extends Properties {
	private static final long serialVersionUID = 3248780562679168446L;

	protected final File backingFile;

	public FileBackedProperties(final File backingFile) {
		super();
		this.backingFile = backingFile;
		load();
	}

	@Override
	public Object remove(final Object key) {
		final Object result = super.remove(key);
		store();
		return result;
	}

	@Override
	public Object setProperty(final String key, final String value) {
		final Object result = super.setProperty(key, value);
		store();
		return result;
	}

	protected synchronized FileBackedProperties load() {
		FileReader tmpFileReader = null;
		try {
			tmpFileReader = new FileReader(backingFile);
			this.load(tmpFileReader);
		} catch (final Exception e) {
			throw new RuntimeException("Failed to read favoured camera conf settings file " + backingFile.getAbsolutePath(), e);
		} finally {
			IOUtils.closeQuietly(tmpFileReader);
		}
		return this;
	}

	protected synchronized FileBackedProperties store() {
		FileOutputStream tmpFileWriter = null;
		try {
			tmpFileWriter = new FileOutputStream(backingFile);
			this.store(tmpFileWriter, "GPhoto2Server - favoured camera settings");
		} catch (final Exception e) {
			throw new RuntimeException("Failed to write favoured camera conf settings file " + backingFile.getAbsolutePath(), e);
		} finally {
			IOUtils.closeQuietly(tmpFileWriter);
		}
		return this;
	}
}