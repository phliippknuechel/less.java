package org.lesscss.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lesscss.parser.LessCssParser;
import org.lesscss.parser.LessCssResult;

public class LessCssServlet implements Servlet{

	private ServletConfig config;
	private LessCssParser parser;
	private Map<File,CacheResult> cache;


	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		try {
			parser=new LessCssParser();
			parser.init(new File(config.getServletContext().getRealPath("")));
			String cacheSize = config.getInitParameter("cache-size");
			if(cacheSize!=null){
				cache=Collections.synchronizedMap(new LruCache<File, CacheResult>(Integer.parseInt("cache-size")));
			} else {
				cache=Collections.synchronizedMap(new HashMap<File, CacheResult>());
			}
			
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public ServletConfig getServletConfig() {
		return config;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		if(req instanceof HttpServletRequest){
			HttpServletRequest request=(HttpServletRequest) req;
			HttpServletResponse response=(HttpServletResponse) res;
			String servletPath = request.getServletPath();

			File f=new File(getServletConfig().getServletContext().getRealPath(servletPath));
			f=f.getAbsoluteFile();
			
			CacheResult cacheResult = cache.get(f);
			LessCssResult result = null;
			if(cacheResult!=null && checkModified(cacheResult)){
				result=cacheResult.result;
			} else {
				result = parser.parser(f);
				CacheResult cr=new CacheResult();
				cr.modified=getModifiedTimes(result.getAllFiles());
				cr.result=result;
				cache.put(result.getMainFile(),cr);
			}
			
			response.setContentType("text/css");
			response.getWriter().print(result.getCss());
		}
		
	}

	private boolean checkModified(CacheResult cacheResult) {
		int len=cacheResult.result.getAllFiles().size();
		for (int i = 0; i < len; i++) {
			long lastModified = cacheResult.result.getAllFiles().get(i).lastModified();
			long oldLastModified = cacheResult.modified.get(i);
			if( lastModified!= oldLastModified){
				return false;
			}
			
		}
		return true;
	}

	private List<Long> getModifiedTimes(List<File> allFiles) {
		List<Long> ret=new ArrayList<Long>();
		for(File f:allFiles){
			ret.add(f.lastModified());
		}
		return ret;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
		parser.close();
	}
	
	private static class CacheResult{
		public LessCssResult result;
		public List<Long> modified=new ArrayList<Long>();
	}

}
