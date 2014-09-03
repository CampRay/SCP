package com.missionsky.scp.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebInit implements ServletContextListener {
	
	private ServletContext context;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		context = event.getServletContext();
		initServletContextAttributes();
	}

	private void initServletContextAttributes(){
		final String ctx = "/scp_web_platform";
		final String cssPath = ctx + "/static/css/";
		final String jsPath = ctx + "/static/js/";
		final String imgPath = ctx + "/static/images/";
		context.setAttribute("ctx", ctx);
		context.setAttribute("css", cssPath);
		context.setAttribute("js", jsPath);
		context.setAttribute("images", imgPath);
	}
}
