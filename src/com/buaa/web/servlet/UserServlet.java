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

	//ģ�鹦��
	//--------------------------------------------�����-----------------------------------------------------
	public void Active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//��ü�����
		String activeCode = request.getParameter("activeCode");
		//����service��
		UserService service = new UserService();
		boolean isActiveSuccess=service.active(activeCode);
		
		if(isActiveSuccess){
			//����ɹ�����ת����¼����
			response.sendRedirect(request.getContextPath()+"/login.jsp");
		}else{
			response.sendRedirect(request.getContextPath()+"/activeFail.jsp");
		}
	}
	
	//--------------------------------------------�û������ڹ���-----------------------------------------------------
	public void CheckUserName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//����û���
		String userName=request.getParameter("username");
		
		UserService service = new UserService();
		boolean isExist=service.checkUserName(userName);
		//json��ʽ
		String json="{\"isExist\":"+isExist+"}";
		response.getWriter().write(json);
	}
	
	//--------------------------------------------�˳�����-----------------------------------------------------
	public void Exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.getServletContext().setAttribute("isLoginSuccess", null);
		this.getServletContext().setAttribute("username", null);
		response.sendRedirect("/WormShop");
	}
	
	//--------------------------------------------��¼����-----------------------------------------------------
	public void Login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//��õ�¼��Ϣ
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		//�жϵ�¼��ע����Ϣ
		LoginService service = new LoginService();
		boolean isLoginSuccess=service.loginHandle(username,password);
		boolean isRegister=service.loginHandleRegister(username,password);
		//��ʾ����
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
	
	//--------------------------------------------ע�Ṧ��-----------------------------------------------------
	public void Register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		//��ñ�����
		Map<String, String[]> parameterMap = request.getParameterMap();
		User user = new User();
		try {
			//ӳ���װ
			BeanUtils.populate(user,parameterMap);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//������װ
		user.setUid(CommonsUtils.getUUID());
		user.setTelephone(null);
		user.setState(0);
		String activeCode=CommonsUtils.getUUID();
		user.setCode(activeCode);
		
		//��user���ݸ�service��
		
		UserService service = new UserService();
		boolean isRegisterSuccess=service.register(user);
		
		//�ж��Ƿ�ע��ɹ�
		if(isRegisterSuccess){
			//���ͼ����ʼ�
			String emailMsg="��ϲ��ע��ɹ����������Ӽ��"+"<a http://localhost:8080/"
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
			//��ת���ɹ�����
			response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
		}else{
			response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
		}			
	}

}