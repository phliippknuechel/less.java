less.java
=======

Java integration for the - The **dynamic** stylesheet language.

<http://lesscss.org>

- Minimal dependencies - Only 1 Rhino.jar

- Work with less files as if they were normal css files. 
  
	<link type="text/css" rel="stylesheet" href="/css/main.less" >

- CSS is generated on the fly. 

- Results of parsing are cached, optimal production speed.

- Cache gets invalidated if any part of the less file (includes) changes. Like using direct CSS files.

usage
-------
Add the following dependency to your maven project:
	<dependency>
		<groupId>org.lesscss.java</groupId>
		<artifactId>less.java</artifactId>
		<version>1.1.4</version>
	</dependency>


Add the following servlet to your web.xml:

	<servlet>
	    <servlet-name>lesscss</servlet-name>
	    <servlet-class>org.lesscss.servlet.LessCssServlet</servlet-class>
		<load-on-startup>3</load-on-startup><!-- does not matter -->
	</servlet>

	<servlet-mapping>
	    <servlet-name>lesscss</servlet-name>
	    <url-pattern>*.less</url-pattern>
	</servlet-mapping>

Optional configuration:
Limit the number of cached css resources:
	<servlet>...
		<init-param>
	        <param-name>cache-size</param-name>
	        <param-value>1000</param-value>
	    </init-param>
	</servlet>




license
-------

See `LICENSE` file.

> Copyright (c) 2009-2011 Philipp Knüchel
