<%@ page language="java" pageEncoding="UTF-8"%>  
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${ctx}/static/images/favicon.png">
    <title>Signin SCP</title>
    <!-- Bootstrap core CSS -->
    <link href="${ctx}/static/css/bootstrap.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="${ctx}/static/css/signin.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/static/css/validationEngine.jquery.css" type="text/css"/>

    <!-- Just for debugging purposes. Don't actually copy this line! -->
    <!--[if lt IE 9]><script src="../../docs-assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.0/html5shiv.min.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
    <script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
    <script src="${ctx}/static/js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript">  
    	$( document ).ready(function(){
    		var errorkey = "<%=(String) request.getAttribute("message_login")%>";
    		showError(errorkey);
    		
    		$(".formErrorContent").click(function(){
    			var input = $(".formErrorContent").parent().next();
    			$(input).validationEngine("hideAll");
    		});
    	});
		<!--  
		function reloadVerifyCode(){  
		    document.getElementById('verifyCodeImage').setAttribute('src', '${pageContext.request.contextPath}/getVerifyCodeImage');  
		}  
		//-->
		function showError(errorkey){
			var msg = "";
			if(errorkey != null || errorKey != ''){
				if("org.apache.shiro.authc.IncorrectAuthCodeException" == errorkey){
					msg = "AuthCode error!";
					$("#verifyCode").validationEngine("showPrompt",msg,"error");
				}
				if("org.apache.shiro.authc.UnknownAccountException" == errorkey){
					msg = "Unknown account!";
					$("#username").validationEngine("showPrompt",msg,"error");
				}
				if("org.apache.shiro.authc.IncorrectCredentialsException" == errorkey){
					msg = "Incorrect password!";
					$("#password").validationEngine("showPrompt",msg,"error");
				}
				if("org.apache.shiro.authc.LockedAccountException" == errorkey){
					msg = "Locked account!";
					$("#username").validationEngine("showPrompt",msg,"error");
				}
				if("org.apache.shiro.authc.ExcessiveAttemptsException" == errorkey){
					msg = "Too many wrong!";
					$("#username").validationEngine("showPrompt",msg,"error");
				}
				if("org.apache.shiro.authc.AuthenticationException" == errorkey){
					msg = "Username or Password incorrect!";
					$("#username").validationEngine("showPrompt",msg,"error");
				}
			}
		}
	</script>
  </head>

  <body>

	<div class="container">
		<form class="form-horizontal form-signin"
			action="<%=request.getContextPath()%>/login" method="POST">
			<h2 class="form-signin-heading">Please sign in</h2>
			<div class="form-group">
				<label class="col-sm-2 control-label">Name:</label>
				<div class="col-sm-10">
					<input id="username" type="text" class="form-control" name="username" required autofocus>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-2 control-label">Password:</label>
				<div class="col-sm-10">
					<input id="password" type="password" class="form-control" name="password" required>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-2 control-label">AuthCode：</label>
				<div class="col-sm-8">
					<input id="verifyCode" type="text" class="form-control" name="verifyCode" required>
				</div>
				<div class="col-sm-2">
					<img id="verifyCodeImage" onclick="reloadVerifyCode()"
						src="<%=request.getContextPath()%>/getVerifyCodeImage" />
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button type="submit" class="btn btn-lg btn-primary btn-block">Sign
						in</button>
				</div>
			</div>
		</form>
	</div><!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
  </body>
</html>