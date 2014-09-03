<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon" href="${ctx}/static/images/favicon.png">
<title>Standard File List</title>
<link href="${ctx}/static/css/bootstrap.css" rel="stylesheet">
<link href="${ctx}/static/css/navbar.css" rel="stylesheet">
<link href="${ctx}/static/css/jquery-ui.css" rel="stylesheet">
<link rel="stylesheet" href="${ctx}/static/css/validationEngine.jquery.css" type="text/css"/>
</head>
<body>
	<div class="container">
		<div class="navbar navbar-default">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">SCP Web Management</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li class="active"><a href="#"><img
							src="${ctx}/static/images/setting.png" />Change Pwd</a></li>
					<li><a href="#"><img src="${ctx}/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img src="${ctx}/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li><a href="${ctx}/task/tasklist">Task
							Manage</a></li>
					<li><a href="${ctx}/algorithm/algorithmlist">Algorithm
							Manage</a></li>
					<li><a href="${ctx}/source/sourcelist">Source
							Manage</a></li>
					<li><a href="${ctx}/inter/interlist">Interface
							Manage</a></li>
					<li class="active"><a href="${ctx}/config/configlist">Config
							Manage</a></li>
					<li><a href="${ctx}/user/userlist">User Manager</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-md-2">
				<div class="panel panel-default" style="min-height:400px;">
					<div class="panel-body">
						<ul class="nav nav-pills nav-stacked">
							<li class="active"><a href="${ctx}/config/configlist">Standard File List</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="panel panel-default">
					<div class="panel-heading">
						<div class="alert alert-info" style="margin-bottom:0px;">Standard File List</div>
					</div>
					<div class="panel-body">
						<form method="get" action="${ctx}/config/configlist">
							<div class="form-group">
								<div class="col-sm-2" align="right">
									<label class="control-label">File Name:</label>
								</div>
								<div class="col-sm-7">
									<input name="fileName" type="text" id="algorithmName"
										class="form-control" value="${fileName}"/>
								</div>
								<div class="col-sm-3">
									<button type="submit" class="btn btn-default"><img src="${ctx}/static/images/search.png" /></button>&nbsp;
									<button id="btnUp" type="button" class="btn btn-info">upload</button>
								</div>
							</div>
						</form>
						<br><br><br>
						<div class="col-sm-12"><label>${size} files found</label></div>
					</div>
					<c:if test="${not empty files}">
					<table class="table table-bordered">
						<tr class="active">
							<th class="col-md-3">File Name</th>
							<th class="col-md-5">Description</th>
							<th class="col-md-2">Operation</th>
						</tr>
							<c:forEach items="${files}" var="item" varStatus="status"
								begin="0" step="1">
								<tr class="success">
									<td>
										<input type="hidden" value="${item.rowKey}">
										${item.name}
									</td>
									<td>${item.description}</td>
									<td><button type="button" class="btn btn-info" onclick="delFile(this)">del</button></td>
								</tr>
							</c:forEach>
						
					</table>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<div class="container" id="dialogDiv">
		<form:form id="fileForm" class="form-horizontal" modelAttribute="file" action="${ctx}/config/saveuploadfile">
			<div class="form-group">
				<label class="col-sm-2 control-label">File Name</label>
				<div class="col-sm-8">
					<form:input path="name" class="form-control validate[required]" readonly="true"/>
					<form:hidden path="content"/>
				</div>
				<button type="button" class="btn btn-default" id="upload">select</button>
			</div>
			<div class="form-group">
				<label class="col-sm-2 control-label">Description</label>
				<div class="col-sm-10">
					<form:textarea path="description" class="form-control validate[required]" rows="3"/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button type="submit" class="btn btn-default">Submit</button>
				</div>
			</div>
		</form:form>
	</div>
	<script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
	<script src="${ctx}/static/js/jquery-ui.js"></script>
	<script src="${ctx}/static/js/bootstrap.min.js"></script>
	<script src="${ctx}/static/js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/ajaxupload.js"></script>
	<script type="text/javascript">	
	$(document).ready(function() {
		var button = $("#upload");
		if(button.length > 0){
			new AjaxUpload(button,{
				action:"${ctx}/uploadFile",
				name:'file',
				responseType:'json',
				onSubmit:function(file,text){
					if(!(text&&/^(xml|XML)$/.exec(text))){
						alert("please select available file which type is xml");
						return false;
					}
					$('#btnUp').attr('disabled',true);
				},
				onComplete:function(file,response){
					if(response.status == 'ok'){
						alert("upload file success");
						$('#btnUp').attr('disabled',false);
						if(response.content == null || response.content == ""){
							alert("file content is empty,please choose a standard file upload");
							return false;
						}else{
							$("#dialogDiv").find("form").find("input:eq(0)").val(response.fileName);
							$("#dialogDiv").find("form").find("input:eq(1)").val(response.content);
						}
					}else{
						$('#btnUp').attr('disabled',false);
						alert("upload file failure!");
					}
				}
			});
		}
		
		$("#dialogDiv").dialog({
			autoOpen : false,
			height : 380,
			width : 800,
			modal : true,
			title : 'Upload File'
		});
		
		$("#btnUp").click(function(){
			$("#dialogDiv").find("input").val("");
			$("#fileForm").validationEngine("hideAll");
			$("#dialogDiv").dialog("open");
		});
		
		$.validationEngineLanguage.newLang("");
		$('#fileForm').validationEngine({
			ajaxFormValidation : true,
			promptPosition : "topLeft",
			ajaxFormValidationMethod:'POST',
			onAjaxFormComplete : function(errorInForm, form, json,options){
				var e = eval(json);
				if (e.msg == "success") {
					alert('save success！');
					$("#dialogDiv").dialog("close");
					window.location.href = "${ctx}/config/configlist";
				} else if(e.msg == "empty"){
					alert("something is empty");
				} else {
					alert(e.msg);
				}
			},
			success : false,
			failure : function() {
			}
		});
	});
	
	function delFile(k){
		var rowkey = $(k).parent().parent().find("input[type='hidden']").val();
		alert(rowkey);
		if(rowkey == undefined){
			alert("get rowkey error");
			return;
		}
		$.ajax({
			type:'post',
			url:'${ctx}/config/deletefile',
			data:{"rowKey":rowkey},
			dataType:'json',
			success:function(data){
				var e = eval(data);
				if(e.msg == "success"){
					alert("delete successful!");
					location.reload();
				}else if(e.msg == "failure"){
					alert("delete failure!");
				}else{
					alert(e.msg);
				}
			}
		});
	}
	</script>
</body>
</html>