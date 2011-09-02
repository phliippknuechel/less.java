package org.lesscss;

import java.io.File;
import java.io.IOException;

import org.lesscss.parser.LessCssParser;

public class Test {
	public static void main(String[] args) {
		
		LessCssParser parser=new LessCssParser();
		try{
			File test=new File("test/import.less");
			parser.init(new File("test"));
			parser.parser(test);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			parser.close();
		}
	}
}
