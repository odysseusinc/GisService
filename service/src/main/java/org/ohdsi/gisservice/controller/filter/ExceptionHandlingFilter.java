package org.ohdsi.gisservice.controller.filter;

import static org.springframework.web.servlet.DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ohdsi.gisservice.controller.RestExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

public class ExceptionHandlingFilter implements Filter {

	private RestExceptionHandler exceptionHandler;
	private Object monitor = new Object();
	private HandlerExceptionResolver exceptionResolver;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		initHandler(filterConfig.getServletContext());
	}

	private void initHandler(ServletContext servletContext) {

		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		exceptionHandler = context.getBean(RestExceptionHandler.class);
		exceptionResolver = context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} catch (Exception ex) {
			if (exceptionHandler == null) {
				synchronized (monitor) {
					initHandler(request.getServletContext());
				}
			}
			exceptionResolver.resolveException(request, response, null, ex);
		}
	}

	@Override
	public void destroy() {

	}
}
