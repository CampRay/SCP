<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="ctx" content="${ctx}" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon" href="${ctx}/static/images/favicon.png">
<title>Update Mining</title>
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
				<a class="navbar-brand" href="#">SCP Web Platform</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li class="active"><a href="#"><img
							src="${ctx}/static/images/setting.png" />Change Pwd</a></li>
					<li><a href="#"><img src="${ctx}/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img src="${ctx}/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li class="active"><a href="${ctx}/task/tasklist">Task Manage</a></li>
					<li><a href="${ctx}/algorithm/algorithmlist">Algorithm Manage</a></li>
					<li><a href="${ctx}/source/sourcelist">Source Manage</a></li>
					<li><a href="${ctx}/inter/interlist">Interface Manage</a></li>
					<li><a href="${ctx}/config/configlist">Config Manage</a></li>
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
							<li><a href="${ctx}/task/tasklist">Task List</a></li>
							<li class="active"><a href="${ctx}/task/mininglist">Mining List</a></li>
							<li><a href="${ctx}/task/executeresult">Execute Result</a></li>
							<li><a href="${ctx}/task/miningresult">Mining Result</a></li>
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
						<form:form action="${ctx}/task/savemining" class="form-horizontal" modelAttribute="mining" method="post" id="miningForm">
							<form:hidden path="rowKey" id="rowKey"/>
							<div class="form-group">
								<label class="col-sm-2 control-label">Name：</label>
								<div class="col-sm-10">
									<form:input path="name" class="form-control validate[required]"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Description：</label>
								<div class="col-sm-10">
									<form:textarea path="description" class="form-control validate[required]" rows="3"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Source：</label>
								<div class="col-sm-10">
									<form:select path="source" class="form-control validate[required]">
										<form:option value="">--Please select--</form:option>
										<form:options items="${sources}" itemLabel="taskName" itemValue="rowKey"/>
									</form:select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">When do you want the task to start?</label>
								<div class="col-sm-10" id="radio-type">
									<c:choose>
										<c:when test="${mining.trigger eq 1}">
											<form:radiobutton class="validate[required]" path="trigger" value="1" onclick="radioclick(this)" checked="true"></form:radiobutton>Daily&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
										<c:otherwise>
											<form:radiobutton class="validate[required]" path="trigger" value="1" onclick="radioclick(this)"></form:radiobutton>Daily&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${mining.trigger eq 2}">
											<form:radiobutton class="validate[required]" path="trigger" value="2" onclick="radioclick(this)" checked="true"></form:radiobutton>Weekly&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
										<c:otherwise>
											<form:radiobutton class="validate[required]" path="trigger" value="2" onclick="radioclick(this)"></form:radiobutton>Weekly&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${mining.trigger eq 3}">
											<form:radiobutton class="validate[required]" path="trigger" value="3" onclick="radioclick(this)" checked="true"></form:radiobutton>One Time
										</c:when>
										<c:otherwise>
											<form:radiobutton class="validate[required]" path="trigger" value="3" onclick="radioclick(this)"></form:radiobutton>One Time
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="form-group task-time" style="display:none;" id="start-time">
								<div class="col-sm-offset-2 col-sm-10">
									<label class="col-sm-1 control-label">Start:</label>
									<div class="col-sm-9">
										<input type="text" name="start" value="<fmt:formatDate value="${mining.start}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:'true'})" class="Wdate validate[required] form-control" style="height:34px;"/>
									</div>
								</div>
							</div>
							<div class="form-group task-time" style="display:none;" id="start-day">
								<div class="col-sm-offset-2 col-sm-10">
									<label class="col-sm-2 control-label">Recur every:</label>
									<div class="col-sm-5">
										<form:input path="time" class="form-control validate[min[1],custom[integer]]"/>
									</div>
									<label class="col-sm-1 control-label">days:</label>
								</div>
							</div>
							<%-- <div class="form-group task-time start-week"  style="display:none;">
								<div class="col-sm-offset-2 col-sm-10">
									<label class="col-sm-2 control-label">Recur every:</label>
									<div class="col-sm-5">
										<form:input path="time" class="form-control validate[min[1],custom[integer]]"/>
									</div>
									<label class="col-sm-1 control-label">weeks:</label>
								</div>
							</div> --%>
							<div class="form-group task-time start-week"  style="display:none;">
								<div class="col-sm-offset-2 col-sm-10">
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${mining.weekday eq 7}">
												<form:radiobutton path="weekday" value="7" checked="true"/>Sunday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="7" />Sunday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${mining.weekday eq 1}">
												<form:radiobutton path="weekday" value="1" checked="true"/>Monday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="1" />Monday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline"> 
										<c:choose>
											<c:when test="${mining.weekday eq 2}">
												<form:radiobutton path="weekday" value="2" checked="true"/>TuesDay
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="2" />TuesDay
											</c:otherwise>
										</c:choose>
									</label> 
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${mining.weekday eq 3}">
												<form:radiobutton path="weekday" value="3" checked="true"/>Wednesday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="3" />Wednesday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${mining.weekday eq 4}">
												<form:radiobutton path="weekday" value="4" checked="true"/>Thursday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="4" />Thursday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${mining.weekday eq 5}">
												<form:radiobutton path="weekday" value="5" checked="true"/>Friday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="5" />Friday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${mining.weekday eq 6}">
												<form:radiobutton path="weekday" value="6" />Saturday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="6" />Saturday
											</c:otherwise>
										</c:choose>
									</label>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Algorithm：</label>
								<div class="col-sm-10">
									<form:select id="algorithm" path="action.rowId" class="form-control validate[required]" onchange="stepChange(this)">
										<form:option value="">--Please select--</form:option>
										<form:options items="${algorithms}" itemLabel="name" itemValue="rowKey"/>
									</form:select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Algorithm Description：</label>
								<div class="col-sm-10">
									<form:input path="action.description" class="form-control" readonly="true" id="action-description"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">PathName：</label>
								<div class="col-sm-10">
									<form:input path="action.pathName" class="form-control" readonly="true" id="action-pathName"/>
								</div>
							</div>
							<c:if test="${not empty mining.action}">
								<c:if test="${not empty mining.action.params}">
									<c:forEach items="${mining.action.params}" var="item" varStatus="status">
										<div class="form-group params">
											<label class="col-sm-2 control-label">${item.name}：<span>${item.value}</span></label>
											<div class="col-sm-10">
												<form:hidden path="action.params[${status.index}].name"/>
												<form:input path="action.params[${status.index}].value" class="form-control"/>
											</div>
										</div>
									</c:forEach>
								</c:if>
							</c:if>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10" align="center">
									<button type="submit" class="btn btn-info">Save</button>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<button type="button" class="btn btn-info"  onclick="location='${ctx}/task/mininglist'">Back</button>
								</div>
							</div>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%-- <div id="hidedata">
		<input type="hidden" value="${mining.action.rowId}">
		<c:if test="${not empty mining}">
			<c:if test="${not empty mining.action.action}">
				<c:forEach items="${mining.params}" var="item">
					<input type="hidden" value="${item}">
				</c:forEach>
			</c:if>
		</c:if>
	</div> --%>
	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
	<script src="${ctx}/static/js/jquery-ui.js"></script>
	<script src="${ctx}/static/js/bootstrap.min.js"></script>
	<script src="${ctx}/static/js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/My97DatePicker/WdatePicker.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/jquery.validationEngine-en.js" type="text/javascript"></script>
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
		
		$("#miningForm").validationEngine({
			ajaxFormValidation : true,
			promptPosition : "topLeft",
			ajaxFormValidationMethod:'POST',
			onAjaxFormComplete : function(errorInForm, form, json,options){
				var e = eval(json);
				if (e.msg == "success") {
					alert('save success！');
					window.location.href = "${ctx}/task/mininglist";
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
		
		//$("#datepicker").datepicker();
		$("#radio-type input:radio").each(function(i,item){
			if($(this).attr("checked")){
				radioclick(this);
			}
		});
		
		stepChange($("#algorithm"));
	});
	
	function radioclick(k) {
		var value = $(k).val();
		$(".task-time").hide();
		$(".start-week input[type='radio']").each(function(i, item) {
			$(this).removeClass("validate[required]");
			;
		});
		$("#start-time").show();
		if (value == 1) {
			$("#start-day").find("input").attr("disabled",false);
			$(".start-week:first").find("input").attr("disabled",true);
			$("#start-day").show();
		} else if (value == 2) {
			$("#start-day").find("input").attr("disabled",true);
			$(".start-week:first").find("input").attr("disabled",false);
			$(".start-week").show();
			$(".start-week input[type='radio']").each(function(i, item) {
				$(this).addClass("validate[required]");
				;
			});
		}
	}

	function stepChange(k) {
		$(".params").remove();
		if ($(k).val() == undefined || $(k).val() == "") {
			return false;
		}
		$("#action-description").val("");
		$("#action-pathName").val("");
		$.ajax({
			type : 'post',
			url :  '${ctx}/task/getalgorithmparam',
			data : {"rowKey" : $(k).val()},
			async : false,
			dataType : 'json',
			success : function(data) {
				var e = eval(data);
				if (e.msg == "success") {
					var html = "";
					/* var algorithm = $("#hidedata").find("input:eq(0)").val();
					if(algorithm == $(k).val()){
						$(e.list).each(function(i,item){
							var index  = i +1;
							var value = $("#hidedata").find("input:eq("+index+")").val();
							html = html + "<div class='form-group params'><label class='col-sm-2 control-label'>"+item+"：</label><div class='col-sm-10'>"+
							"<input name='params["+i+"]' class='form-control validate[required,custom[onlyLetterNumber]]' value="+value+"></div></div>";
						});
					}else{
						$(e.list).each(function(i,item){
							html = html + "<div class='form-group params'><label class='col-sm-2 control-label'>"+item+"：</label><div class='col-sm-10'>"+
							"<input name='params["+i+"]' class='form-control validate[required,custom[onlyLetterNumber]]'/></div></div>";
						});
					} */
					var list = e.list;
					if(list != undefined || list != null){
						$(list).each(function(i,item){
							html = html + "<div class='form-group params'><label class='col-sm-2 control-label'>"+item.name+"：</label><div class='col-sm-10'><input type='hidden' name='action.params["+i+"].name' value="+item.name+"><input type='text' name='action.params["+i+"].value' class='form-control'></div></div>";
						});
					}
					$(k).parent().parent().parent().find(".form-group:last").before(html);
					$("#action-description").val(e.description);
					$("#action-pathName").val(e.pathName);
				} else if (e.msg = "failure") {
					alert("search failure");
				}
			}
		});
	}
	</script>
</body>
</html>