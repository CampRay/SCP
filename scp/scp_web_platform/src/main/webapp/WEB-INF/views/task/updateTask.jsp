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
<title>Update Task</title>
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
							<li class="active"><a href="${ctx}/task/tasklist">Task List</a></li>
							<li><a href="${ctx}/task/tasklist">Mining List</a></li>
							<li><a href="${ctx}/task/executeresult">Execute Result</a></li>
							<li><a href="${ctx}/task/miningresult">Mining Result</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="panel panel-default">
					<div class="panel-heading">
						<div class="alert alert-info" id="showTitle" style="margin-bottom:0px;">Add/Edit/View Task</div>
					</div>
					<div class="panel-body" style="min-height: 390px;">
						<form:form action="${ctx}/task/savetask" class="form-horizontal" modelAttribute="task" method="post" id="taskForm">
							<form:hidden path="rowKey" id="rowKey"/>
							<div class="form-group">
								<label class="col-sm-2 control-label">Name：</label>
								<div class="col-sm-10">
									<form:input path="taskName" class="form-control validate[required]"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Description：</label>
								<div class="col-sm-10">
									<form:textarea path="description" class="form-control validate[required]" rows="3"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Files：</label>
								<div class="col-sm-10">
									<form:select path="fileName" class="form-control validate[required]">
										<form:option value="">--Please select--</form:option>
										<c:if test="${not empty files}">
											<c:forEach items="${files}" var="item">
												<form:option value="${item.key}">${item.value}</form:option>
											</c:forEach>
										</c:if>
									</form:select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">Store Path：</label>
								<div class="col-sm-10">
									<form:input path="storePath" class="form-control validate[required]"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">When do you want the task to start?</label>
								<div class="col-sm-10" id="radio-type">
									<c:choose>
										<c:when test="${task.triggerType eq 1}">
											<form:radiobutton class="validate[required]" path="triggerType" value="1" onclick="radioclick(this)" checked="true"></form:radiobutton>Daily&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
										<c:otherwise>
											<form:radiobutton class="validate[required]" path="triggerType" value="1" onclick="radioclick(this)"></form:radiobutton>Daily&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${task.triggerType eq 2}">
											<form:radiobutton class="validate[required]" path="triggerType" value="2" onclick="radioclick(this)" checked="true"></form:radiobutton>Weekly&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
										<c:otherwise>
											<form:radiobutton class="validate[required]" path="triggerType" value="2" onclick="radioclick(this)"></form:radiobutton>Weekly&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${task.triggerType eq 3}">
											<form:radiobutton class="validate[required]" path="triggerType" value="3" onclick="radioclick(this)" checked="true"></form:radiobutton>One Time
										</c:when>
										<c:otherwise>
											<form:radiobutton class="validate[required]" path="triggerType" value="3" onclick="radioclick(this)"></form:radiobutton>One Time
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="form-group task-time" style="display:none;" id="start-time">
								<div class="col-sm-offset-2 col-sm-10">
									<label class="col-sm-1 control-label">Start:</label>
									<div class="col-sm-9">
										<input type="text" name="startTime" value="<fmt:formatDate value="${task.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:'true'})" class="Wdate validate[required] form-control" style="height:34px;"/>
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
<!-- 							<div class="form-group task-time start-week"  style="display:none;"> -->
<!-- 								<div class="col-sm-offset-2 col-sm-10"> -->
<!-- 									<label class="col-sm-2 control-label">Recur every:</label> -->
<!-- 									<div class="col-sm-5"> -->
<%-- 										<form:input path="time" class="form-control validate[min[1],custom[integer]]"/> --%>
<!-- 									</div> -->
<!-- 									<label class="col-sm-1 control-label">weeks:</label> -->
<!-- 								</div> -->
<!-- 							</div> -->
							<div class="form-group task-time start-week"  style="display:none;">
								<div class="col-sm-offset-2 col-sm-10">
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${task.weekday eq 7}">
												<form:radiobutton path="weekday" value="7" checked="true"/>Sunday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="7" />Sunday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${task.weekday eq 1}">
												<form:radiobutton path="weekday" value="1" checked="true"/>Monday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="1" />Monday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline"> 
										<c:choose>
											<c:when test="${task.weekday eq 2}">
												<form:radiobutton path="weekday" value="2" checked="true"/>TuesDay
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="2" />TuesDay
											</c:otherwise>
										</c:choose>
									</label> 
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${task.weekday eq 3}">
												<form:radiobutton path="weekday" value="3" checked="true"/>Wednesday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="3" />Wednesday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${task.weekday eq 4}">
												<form:radiobutton path="weekday" value="4" checked="true"/>Thursday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="4" />Thursday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${task.weekday eq 5}">
												<form:radiobutton path="weekday" value="5" checked="true"/>Friday
											</c:when>
											<c:otherwise>
												<form:radiobutton path="weekday" value="5" />Friday
											</c:otherwise>
										</c:choose>
									</label>
									<label class="checkbox-inline">
										<c:choose>
											<c:when test="${task.weekday eq 6}">
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
								<div class="col-sm-offset-2 col-sm-10">
									<label for="radio3" class="col-sm-2 control-label"
										style="float: right;">
										<button type="button" class="btn btn-default"
											onClick="addStep();">Set&nbsp;&nbsp;&nbsp;Algorithm</button>
									</label>
								</div>
							</div>
							<c:if test="${not empty task.actions}">
									<c:forEach items="${task.actions}" var="item" varStatus="status">
									<div class="action-params">
										<div class="form-group">
											<form:hidden path="actions[${status.index}].rowId" indexData="${status.index}"/>
											<label class="col-sm-2 control-label">Algorithm Name:</label>
											<div class="col-sm-3">
												<form:input path="actions[${status.index}].name" class="form-control validate[required]" readonly="true"/>
											</div>
											<div class="col-sm-5">
												<div class="input-group">
													<span class="input-group-addon">describe</span>
													<form:input path="actions[${status.index}].description" class="form-control" readonly="true"/>
												</div>
											</div>
											<div class="col-sm-2">
												<input type="button" class="btn btn-info" value="edit" onclick="editStep(this)"/>
												<input type="button" class="btn btn-info" value="del" onclick="delStep(this)"/>
											</div>
										</div>
										<c:if test="${not empty item.params}">
										<div class="form-group">
											<label class="col-sm-2 control-label">Params：</label>
											<div class="col-sm-10">
											<c:forEach items="${item.params}" var="paramItem" varStatus="paramStatus">
												<dl class="dl-horizontal">
													<form:hidden path="actions[${status.index}].params[${paramStatus.index}].name"/>
													<form:hidden path="actions[${status.index}].params[${paramStatus.index}].value"/>
													<dt>${paramItem.name}:</dt>
													<dd>${paramItem.value}</dd>
												</dl>
											</c:forEach>
											</div>
										</div>
										</c:if>
									</div>
								</c:forEach>
							</c:if>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10" align="center">
									<button type="submit" class="btn btn-info">Save</button>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<button type="button" class="btn btn-info"  onclick="location='${ctx}/task/tasklist'">Back</button>
								</div>
							</div>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- /container -->
	<div id="dialogDiv" class="container">
		<form id="paramForm">
			<br>
			<div class="row">
				<label class="col-sm-2" style="text-align: right;">algorithm
					name</label>
				<select onchange="stepChange(this)" class="col-sm-10 validate[required]">
					<option value="">--please select --</option>
					<c:if test="${not empty algorithms}">
						<c:forEach items="${algorithms}" var="item" varStatus="status">
							<option value="${item.rowKey}">${item.name}</option>
						</c:forEach>
					</c:if>
				</select>
			</div>
			<div class="row">
				<label class="col-sm-2" style="text-align: right;">Description</label><input type="text" class='col-sm-10 validate[required]' readonly="readonly">
			</div>
		</form>
	</div>

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
	<script src="${ctx}/static/js/jquery-ui.js"></script>
	<script src="${ctx}/static/js/bootstrap.min.js"></script>
	<script src="${ctx}/static/js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/My97DatePicker/WdatePicker.js" type="text/javascript" charset="utf-8"></script>
	<script src="${ctx}/static/js/jquery.validationEngine-en.js" type="text/javascript"></script>
	<script src="${ctx}/static/js/editTask.js" type="text/javascript" charset="utf-8"></script>
</body>
</html>