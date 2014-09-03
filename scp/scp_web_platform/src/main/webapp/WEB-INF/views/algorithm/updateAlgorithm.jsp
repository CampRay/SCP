<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon" href="${ctx}/static/images/favicon.png">
<title>Update Algorithm</title>
<link href="${ctx}/static/css/bootstrap.css" rel="stylesheet">
<link href="${ctx}/static/css/navbar.css" rel="stylesheet">
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
				<a class="navbar-brand" href="#">SCP Web Platform</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li class="active"><a href="#"><img src="${ctx}/static/images/setting.png" />Change Pwd</a></li>
					<li><a href="#"><img src="${ctx}/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img src="${ctx}/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li><a href="${ctx}/task/tasklist">Task Manage</a></li>
					<li class="active"><a href="${ctx}/algorithm/algorithmlist">Algorithm Manage</a></li>
					<li><a href="${ctx}/source/sourcelist">Source Manage</a></li>
					<li><a href="${ctx}/inter/interlist">Interface Manage</a></li>
					<li><a href="${ctx}/config/configlist">Config Manage</a></li>
					<li><a href="${ctx}/user/userlist">User Manager</a></li>
				</ul>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="col-md-2">
				<div class="panel panel-default" style="min-height:400px;">
					<div class="panel-body">
						<ul class="nav nav-pills nav-stacked">
							<li class="active"><a href="${ctx}/algorithm/algorithmlist">Algorithm List</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="panel panel-default">
					<div class="panel-heading">
						<div class="alert alert-info" id="showTitle" style="margin-bottom:0px;">Add/Edit/View Algorithm</div>
					</div>
					<div class="panel-body" style="min-height: 390px;">
						<form:form class="form-horizontal" modelAttribute="algorithm" method="get" action="${ctx}/algorithm/savealgorithm" id="inputForm">
							<div class="form-group">
								<label class="col-sm-2 control-label">Algorithm Type:</label>
								<div class="col-sm-10">
									<form:select path="type" class="form-control validate[required]" onchange="typeChange(this)">
										<form:option value="">--Please Select--</form:option>
										<form:option value="1">BasicAlgothm</form:option>
										<form:option value="2">MinerAlgorithm</form:option>
									</form:select>
								</div>
							</div>
							<div class="form-group">
								<form:hidden path="rowKey" id="rowKey"/>
								<form:label for="algorithm-name" class="col-sm-2 control-label" path="name">Algorithm Name:</form:label>
								<div class="col-sm-10">
									<form:select path="name" class="form-control validate[required]" id="algorithm-name" onchange="algorithmChange(this)">
										<form:option value="">--please select--</form:option>
										<form:options items="${algorithms}"/>
									</form:select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Description:</label>
								<div class="col-sm-10">
									<form:input id="algorithm-description" path="description" class="form-control" readonly="true"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Package:</label>
								<div class="col-sm-10">
									<form:input id="algorithm-pathName" path="pathName" class="form-control" readonly="true"/>
								</div>
							</div>
							<c:if test="${not empty algorithm.params}">
								<div class="form-group algorithm-param">
									<label class="control-label col-sm-2">Parameters:</label>
									<label class="control-label col-sm-3" style="text-align: center;">Name</label>
									<label class="control-label col-sm-5" style="text-align: center;">Description</label>
									<label class="control-label col-sm-2" style="text-align: center;">Type</label>
								</div>
								<c:forEach items="${algorithm.params}" var="item" varStatus="status">
									<div class="form-group algorithm-param">
										<div class="col-sm-offset-2 col-sm-4">
											<form:input path="params[${status.index}].name" class="form-control" readonly="true"/>
										</div>
										<div class="col-sm-4">
											<form:input path="params[${status.index}].description" class="form-control" readonly="true"/>
										</div>
										<div class="col-sm-2">
											<form:input path="params[${status.index}].type" class="form-control" readonly="true"/>
										</div>
									</div>
								</c:forEach>
							</c:if>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10" align="center">
									<input type="submit" value="Save" class="btn btn-default">
									<input type="button" value="Back" class="btn btn-default"
										onclick="location='${ctx}/algorithm/algorithmlist'">
								</div>
							</div>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
	<script src="${ctx}/static/js/bootstrap.min.js"></script>
	<script src="${ctx}/static/js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			var value = $("#rowKey").val();
			if(value == ""){
				$("#showTitle").text("Add Algorithm");
				$("title").text("Add Algorithm");
			}else{
				$("#showTitle").text("Update Algorithm");
				$("title").text("Update Algorithm");
			}
			
			$.validationEngineLanguage.newLang("");
			$('#inputForm').validationEngine({
				ajaxFormValidation : true,
				promptPosition : "topLeft",
				ajaxFormValidationMethod:'POST',
				onAjaxFormComplete : function(errorInForm, form, json,options){
					var e = eval(json);
					if (e.msg == "success") {
						alert('save success！');
						window.location.href = "${ctx}/algorithm/algorithmlist";
					} else if(e.msg == "exist"){
						alert("this algorithm exists");
					} else if(e.msg == "empty"){
						alert("algorithm entity empty");
					} else {
						alert(e.msg);
					}
				},
				success : false,
				failure : function() {
				}
			});
		});
		
		function typeChange(k){
			var type = $(k).val();
			if(type == undefined || type == ""){
				return false;
			}
			removeParams();
			$.ajax({
					url:"${ctx}/algorithm/getalgorithmbytype",
					type:'post',
					data:{"type":type},
					dataType:'json',
					success:function(data){
						var e = eval(data);
						if(e.msg == "success"){
							var list = e.algorithms;
							$("#algorithm-name").empty();
							var html ="<option value=''>--please select--</option>";
							$.each(list,function(i,item){
								html = html + "<option value="+item+">"+item+"</option>";
							});
							$("#algorithm-name").append(html);
						}else{
							alert("Retrieve Algorithms Failure");
						}
					}
			});
		}
		
		function algorithmChange(k){
			var exp = $(k).val();
			removeParams();
			if(exp != ""){
				$.ajax({
					url:"${ctx}/algorithm/getalgorithmparam",
					type:'post',
					data:{"algorithmName":exp},
					dataType:'json',
					success:function(data){
						var e = eval(data);
						if(e.msg == "success"){
							var params = e.params;
							if(params != null){
								var html = "<div class=\"form-group algorithm-param\"><label class=\"control-label col-sm-2\">Parameters:</label><label class=\"control-label col-sm-3\" style=\"text-align: center;\">Name</label><label class=\"control-label col-sm-5\" style=\"text-align: center;\">Description</label><label class=\"control-label col-sm-2\" style=\"text-align: center;\">Type</label></div>";
								$.each(params,function(i,item){
										html = html + "<div class='form-group algorithm-param'>"
											+"<div class='col-sm-offset-2 col-sm-3'><input type='text' class='form-control' name='params["+i+"].name' value='"+item.name+"' readonly='readonly'/></div>"
											+"<div class='col-sm-5'><input type='text' name='params["+i+"].description' class='form-control' value='"+item.description+"' readonly='readonly'/></div>"
											+"<div class='col-sm-2'><input type='text' class='form-control' name='params["+i+"].type' value='"+item.type+"' readonly='readonly'/></div></div>";
								});
								$(".form-group:eq(3)").after(html);
							}
							var description = e.description;
							$("#algorithm-description").val(description);
							$("#algorithm-pathName").val(e.pathName);
						}else{
							alert("Retrieve Algorithm Msg Failure!");
						}
					}
				});
			}
		}
		
		function removeParams(){
			if($(".algorithm-param").length > 0){
				$(".algorithm-param").each(function(i,item){
					$(this).remove();
				});
			}
			$("#algorithm-description").val("");
			$("#algorithm-pathName").val("");
		}
	</script>
</body>
</html>