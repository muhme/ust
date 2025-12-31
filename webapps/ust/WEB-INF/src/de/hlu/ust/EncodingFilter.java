/*
 * EncodingFilter.java - set char set UTF-8
 *
 * ust web application, Copyright (c) 2008 Heiko Lübbe, MIT License, https://github.com/muhme/ust
 */

package de.hlu.ust;

import java.io.IOException;
import jakarta.servlet.*;

/**
 * Encoding-Filter to set request character encoding to UTF-8.
 * 
 * @author Heiko Lübbe
 */
public class EncodingFilter implements Filter {

   /**
	 * Set the character-encoding to UTF-8.
	 */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      request.setCharacterEncoding("UTF-8");
      filterChain.doFilter(request, response);
   }

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void destroy() {
   }
}
