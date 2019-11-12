package com.buaa.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import com.buaa.domain.User;
import com.buaa.service.LoginService;
import com.buaa.service.UserService;
import com.buaa.utils.CommonsUtils;
import com.buaa.utils.MailUtils;

/** * Servlet implementation class UserServlet
 */
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	//模块功能
	//--------------------------------------------激活功能-----------------------------------------------------
	public void Active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得激活码
		String activeCode = request.getParameter("activeCode");
		//调用service层
		UserService service = new UserService();
		boolean isActiveSuccess=service.active(activeCode);
		
		if(isActiveSuccess){
			//激活成功，跳转到登录界面
			response.sendRedirect(request.getContextPath()+"/login.jsp");
		}else{
			response.sendRedirect(request.getContextPath()+"/activeFail.jsp");
		}
	}
	
	//--------------------------------------------用户名存在功能-----------------------------------------------------
	public void CheckUserName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得用户名
		String userName=request.getParameter("username");
		
		UserService service = new UserService();
		boolean isExist=service.checkUserName(userName);
		//json形式
		String json="{\"isExist\":"+isExist+"}";
		response.getWriter().write(json);
	}
	
	//--------------------------------------------退出功能-----------------------------------------------------
	public void Exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.getServletContext().setAttribute("isLoginSuccess", null);
		this.getServletContext().setAttribute("username", null);
		response.sendRedirect("/WormShop");
	}
	
	//--------------------------------------------登录功能-----------------------------------------------------
	public void Login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得登录信息
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		//判断登录与注册信息
		LoginService service = new LoginService();
		boolean isLoginSuccess=service.loginHandle(username,password);
		boolean isRegister=service.loginHandleRegister(username,password);
		//显示处理
		if(isLoginSuccess){
				if(isRegister){
					this.getServletContext().setAttribute("username", username);
					this.getServletContext().setAttribute("isLoginSuccess", isLoginSuccess);
					response.sendRedirect("/WormShop");
				}else{
					request.setAttribute("isRegister", isRegister);
					request.getRequestDispatcher("/login.jsp").forward(request, response);
				}
		}else{
			request.setAttribute("isLoginSuccess", isLoginSuccess);
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}
	
	//--------------------------------------------注册功能-----------------------------------------------------
	public void Register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		//获得表单数据
		Map<String, String[]> parameterMap = request.getParameterMap();
		User user = new User();
		try {
			//映射封装
			BeanUtils.populate(user,parameterMap);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//完整封装
		user.setUid(CommonsUtils.getUUID());
		user.setTelephone(null);
		user.setState(0);
		String activeCode=CommonsUtils.getUUID();
		user.setCode(activeCode);
		
		//将user传递给service层
		
		UserService service = new UserService();
		boolean isRegisterSuccess=service.register(user);
		
		//判断是否注册成功
		if(isRegisterSuccess){
			//发送激活邮件
			String emailMsg="恭喜您注册成功！请点击链接激活！"+"<a http://localhost:8080/"
					+ "WormShop/active?activeCode="+activeCode+">"
							+ "http://localhost:8080/WormShop/active?activeCode="
					+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//跳转到成功界面
			response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
		}else{
			response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
		}			
	}

}