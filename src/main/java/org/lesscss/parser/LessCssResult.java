package org.lesscss.parser;

import java.io.File;
import java.util.List;

public class LessCssResult {
	private File mainFile;
	private List<File> allFiles;
	private String css;
	
	public File getMainFile() {
		return mainFile;
	}
	public void setMainFile(File mainFile) {
		this.mainFile = mainFile;
	}
	public List<File> getAllFiles() {
		return allFiles;
	}
	public void setAllFiles(List<File> allFiles) {
		this.allFiles = allFiles;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	
	
}
