package org.lesscss.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

public class LessCssParser {
	private static Logger logger = Logger.getLogger("lesscss");
	private ScriptableObject scope;
	private Script callScript;
	private RhinoUtil rhinoUtil;
	

	public LessCssParser() {

	}

	public void init(File root) throws IOException {
		
		try{
			Context ctx=Context.enter();
			rhinoUtil = new RhinoUtil(root);
			scope = ctx.initStandardObjects();
			scope.defineProperty("rhinoUtil", rhinoUtil, ScriptableObject.READONLY);
			loadLessJs();
			StringBuilder r=new StringBuilder();
			r.append("var retval=-1;");
			r.append("print(lessCssSource);");
			r.append("var input=readFile(lessCssSource);");
			r.append("var result=null;");
			r.append("var resources=null;");
			r.append("var parser = new less.Parser({filename:String(lessCssSource)});");
			r.append("parser.parse(input, function (e, root, env) {");
			r.append("    if (e) {");
			r.append("		 print('Error: ' + e.name + ' ' + e.message + ' ' + e.filename);");
			r.append("       retval=-1;");
			r.append("    } else {");
			r.append("        retval=0;");
			r.append("        result = root.toCSS();");
			r.append("		  resources=env.resources;");
			r.append("    }");
			r.append("});");


			callScript = ctx.compileString(r.toString(), "javaCall.js", 0, null);
			
		}
		finally{
			Context.exit();
		}
		

	}

	private void loadLessJs()
			throws IOException {
		String f="less-rhino-1.1.3.js";
		InputStreamReader in = null;
		try {
			Context ctx = Context.enter();
			logger.log(Level.INFO, "Reading js " + f);
			in = new InputStreamReader(this.getClass().getResourceAsStream(f));
			Script s = ctx.compileReader(in, f, 0, null);
			s.exec(ctx, scope);

		} catch (Exception e) {
			logger.log(Level.INFO, "Error compiling js " + f, e);
		} finally {
			Context.exit();
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	public LessCssResult parser(File lessCss) throws IOException{
		try{
			Context ctx = Context.enter();
			ScriptableObject sc= (ScriptableObject) ctx.newObject(scope);
			sc.setPrototype(scope);
			sc.setParentScope(null);
			sc.defineProperty("lessCssSource", lessCss.getAbsolutePath(),
					ScriptableObject.READONLY);
			
			callScript.exec(ctx, sc);
			
			Number retval = (Number) sc.get("retval");
			
			Object result = sc.get("result");
			NativeArray resources= (NativeArray) sc.get("resources");
			if(retval.intValue()<0 || result==null){
				throw new IOException("Failed to parse "+lessCss);
			}
			
			
			LessCssResult res=new LessCssResult();
			res.setCss(result.toString());
			res.setMainFile(lessCss.getAbsoluteFile());
			res.setAllFiles(new ArrayList<File>());
			res.getAllFiles().add(lessCss.getAbsoluteFile());
			if(resources!=null){
				for (int i = 0; i <resources.size(); i++) {
					res.getAllFiles().add(new File( resources.get(i).toString() ).getAbsoluteFile());
				}
			}

			return res;
			
		}finally{
			Context.exit();
		}
	}

	public void close() {

	}

}
