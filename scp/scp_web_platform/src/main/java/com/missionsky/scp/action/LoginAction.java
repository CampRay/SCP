package com.missionsky.scp.action;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.missionsky.scp.service.TaskService;
import com.missionsky.scp.util.VerifyCodeUtil;

@Controller
public class LoginAction {
	@Autowired
	private TaskService taskService;
	
	@RequestMapping("/getVerifyCodeImage")
	public void getVerifyCodeImage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 设置页面不缓存
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String verifyCode = VerifyCodeUtil.generateTextCode(
				VerifyCodeUtil.TYPE_NUM_LOWER, 4, null);
		// 将验证码放到HttpSession里面
		request.getSession().setAttribute("verifyCode", verifyCode);
		System.out.println("本次生成的验证码为[" + verifyCode + "],已存放到HttpSession中");
		// 设置输出的内容的类型为JPEG图像
		response.setContentType("image/jpeg");
		BufferedImage bufferedImage = VerifyCodeUtil.generateImageCode(
				verifyCode, 90, 30, 3, true, Color.WHITE, Color.BLACK, null);
		// 写给浏览器
		ImageIO.write(bufferedImage, "JPEG", response.getOutputStream());
	}

	@RequestMapping(value="login")
	public String login(HttpServletRequest request){
		String resultPageURL = InternalResourceViewResolver.FORWARD_URL_PREFIX + "/";  
        String username = request.getParameter("username");  
        String password = request.getParameter("password");  
        //获取HttpSession中的验证码  
        String verifyCode = (String)request.getSession().getAttribute("verifyCode");  
        //获取用户请求表单中输入的验证码  
        String submitCode = WebUtils.getCleanParam(request, "verifyCode");  
        System.out.println("用户[" + username + "]登录时输入的验证码为[" + submitCode + "],HttpSession中的验证码为[" + verifyCode + "]");  
        if (StringUtils.isEmpty(submitCode) || !StringUtils.equals(verifyCode, submitCode.toLowerCase())){  
            request.setAttribute("message_login", "org.apache.shiro.authc.IncorrectAuthCodeException");  
            return resultPageURL;  
        }  
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);  
        token.setRememberMe(true);  
        System.out.println("为了验证登录用户而封装的token为" + ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));  
        //获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();  
        try {  
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查  
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应  
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法  
            System.out.println("对用户[" + username + "]进行登录验证..验证开始");  
            currentUser.login(token);  
            System.out.println("对用户[" + username + "]进行登录验证..验证通过");  
            resultPageURL = "redirect:task/tasklist";  
        }catch(UnknownAccountException uae){  
            System.out.println("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
            request.setAttribute("message_login", uae.getClass().getName());  
        }catch(IncorrectCredentialsException ice){  
            System.out.println("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");  
            request.setAttribute("message_login", ice.getClass().getName());  
        }catch(LockedAccountException lae){  
            System.out.println("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");  
            request.setAttribute("message_login", lae.getClass().getName());  
        }catch(ExcessiveAttemptsException eae){  
            System.out.println("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");  
            request.setAttribute("message_login", eae.getClass().getName());  
        }catch(AuthenticationException ae){  
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景  
            System.out.println("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");  
            ae.printStackTrace();  
            request.setAttribute("message_login", ae.getClass().getName());  
        }  
        //验证是否登录成功  
        if(currentUser.isAuthenticated()){  
            System.out.println("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");  
        }else{  
            token.clear();  
        }  
        return resultPageURL; 
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
		SecurityUtils.getSubject().logout();
		return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/";
	}
	
	@RequestMapping(value = "uploadFile")
	public void uoloadFile(MultipartHttpServletRequest request, HttpServletResponse response){
		JSONObject json = uploadfile(request,response);
		responseData(json, response);
	}
	
	@RequestMapping("/download/{rowkey}")
	public void downloadFile(@PathVariable("rowkey") String rowkey,
			HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;fileName="
				+ rowkey+".txt");
		try {
			/*List<String> list = taskService.downLoadData(rowkey);*/
			List<String> list = new ArrayList<String>();
			for(int i=0;i<50;i++){
				for(int j=0;j<i;j++){
					list.add("X:"+i+";Y:"+j+"------------"+rowkey);
				}
			}
			OutputStream os = response.getOutputStream();
			if(list != null && !list.isEmpty()){
				for(String str:list){
					byte[] b = Bytes.toBytes(str);
					os.write(b);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject uploadfile(MultipartHttpServletRequest request,
			HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			MultipartHttpServletRequest multipartRequest = request;
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("file");
			byte[] bytes = file.getBytes();
			if(bytes != null && bytes.length > 0){
				System.out.println(Bytes.toString(bytes));
				json.put("content", Bytes.toString(bytes));
			}
			json.put("status", "ok");
			json.put("fileName", file.getOriginalFilename());
//			String uploadDir = request.getSession().getServletContext().getRealPath("/");
//			File uploadFile = new File(uploadDir+System.getProperty("file.separator")+file.getOriginalFilename());
//			FileCopyUtils.copy(file.getBytes(), uploadFile);
//			json.put("path", uploadFile.getAbsolutePath());
		} catch (Exception e) {
			try {
				json.put("status", "fail");
			} catch (JSONException e2) {
			}
		}
		return json;
	}
	
	private void responseData(JSONObject json,HttpServletResponse response){
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");  
	    PrintWriter out = null;  
	    try {  
	        out = response.getWriter();  
	        out.append(json.toString());  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    } 
	}
}
