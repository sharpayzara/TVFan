package crack.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			file = new File(path + fileName);
			if (file.exists()) {
				file.delete();
			}
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int count;
			while ((count = input.read(buffer)) != -1) {
				Log.i("count: ", "" + count);
				output.write(buffer, 0, count);
			}

			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
