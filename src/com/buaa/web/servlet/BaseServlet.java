package com.buaa.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BaseServlet
 */
public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("all")
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		req.setCharacterEncoding("UTF-8");//���ı���
		//�Է��������ָ������
		String methodName=req.getParameter("method");
		//System.out.println(methodName);
		Class clazz = this.getClass();
		Method method;
		try {
			method = clazz.getMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
			method.invoke(this,req,resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
}
