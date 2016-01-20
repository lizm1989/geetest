package com.joyme.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhimingli
 * Date: 14-1-23
 * Time: 上午9:29
 * 编码拦截
 */
public class CharactEncodingFilter implements Filter {

	private FilterConfig filterConfig = null;

	private String encoding = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.encoding = "utf-8";
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (encoding != null) {
			servletRequest.setCharacterEncoding(encoding);
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {
		filterConfig = null;
		encoding = null;
	}
}
