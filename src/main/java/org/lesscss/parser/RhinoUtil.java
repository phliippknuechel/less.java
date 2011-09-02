package org.lesscss.parser;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

public class RhinoUtil {
	private final File root;

	public RhinoUtil(File root) {
		this.root = root.getAbsoluteFile();
	}
	
	public void print(String out){
		Logger.getLogger("lesscss").info(out);
	}

	public String readFile(String file) throws IOException {
		File f = new File(file);
		checkRoot(f);

		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			Reader r = new  BufferedReader(new InputStreamReader(in));
			CharArrayWriter out = new CharArrayWriter(1024);
			char[] buf = new char[1024];

			int len = 0;
			while (len >= 0) {
				len = r.read(buf);
				if (len > 0) {
					out.write(buf, 0, len);
				}
			}

			return out.toString();
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

	private void checkRoot(File f) throws IOException {
		while ((f = f.getParentFile()) != null) {
			if(f.getName().equals("WEB-INF")){
				throw new IOException("Access denied! "+f);
			}
			if (f.equals(root))
				return;
		}
		throw new IOException("Access denied! "+f);
	}
}
