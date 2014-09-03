<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon" href="${ctx}/static/images/favicon.png">
<title>Task List</title>
<link href="${ctx}/static/css/bootstrap.css" rel="stylesheet">
<link href="${ctx}/static/css/navbar.css" rel="stylesheet">
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
					<li><a href="#"><img
							src="${ctx}/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img
							src="${ctx}/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li class="active"><a href="${ctx}/task/tasklist">Task
							Manage</a></li>
					<li><a href="${ctx}/algorithm/algorithmlist">Algorithm
							Manage</a></li>
					<li><a href="${ctx}/source/sourcelist">Source
							Manage</a></li>
					<li><a href="${ctx}/inter/interlist">Interface
							Manage</a></li>
					<li><a href="${ctx}/config/configlist">Config
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
				<div class="panel panel-default" style="min-height: 400px;">
					<div class="panel-body">
						<ul class="nav nav-pills nav-stacked">
							<li class="active"><a href="${ctx}/task/tasklist">Task List</a></li>
							<li><a href="${ctx}/task/mininglist">Mining List</a></li>
							<li><a href="${ctx}/task/executeresult">Execute Result</a></li>
							<li><a href="${ctx}/task/miningresult">Mining Result</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="panel panel-default" id="content-panel">
					<div class="panel-heading">
						<div class="alert alert-info" style="margin-bottom:0px;">Task List</div>
					</div>
					<div class="panel-body">
						<form:form action="${ctx}/task/tasklist" method="get" modelAttribute="task" role="form" id="taskForm">
							<div class="form-group">
								<label class="col-sm-2 control-label" style="text-align:right;">Task Name：</label>
								<div class="col-sm-7">
									<form:input path="taskName" id="taskName" class="form-control" value="${taskName}"/>
								</div>
								<div class="col-sm-3">
									<button type="submit" class="btn btn-default">Search</button>
									&nbsp;
								<button type="button" class="btn btn-default" onClick="addTask();">Add</button>
								</div>
							</div>
						</form:form>
					</div>
					<c:if test="${not empty tasks}">
					<table id="taskTable" class="table table-bordered">
						<tr>
							<th>Task Name</th>
							<th>Standard File Name</th>
							<th>Start Time</th>
							<th>Trigger Type</th>
							<th>Status</th>
							<th>Operation</th>
						</tr>
						<c:forEach items="${tasks}" var="task">
							<tr class="active">
								<td><input type="hidden" value="${task.rowKey}">${task.taskName}</td>
								<td>${task.fileName}</td>
								<td><fmt:formatDate value="${task.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<td>${task.triggerType}</td>
								<td>
									<c:choose>
										<c:when test="${task.status eq 0}">
											ready
										</c:when>
										<c:when test="${task.status eq 1}">
											running
										</c:when>
										<c:when test="${task.status eq 2}">
											interrupt
										</c:when>
										<c:when test="${task.status eq 3}">
											finished
										</c:when>
									</c:choose>
								</td>
								<td>
									<c:if test="${empty task.status}">
										<button type="button" class="btn btn-info" onclick="editTask(this)">mod</button>&nbsp;
									</c:if>
									<button type="button" class="btn btn-info" onclick="deleteTask(this)">del</button>&nbsp;
									<c:if test="${empty task.status}">
										<button type="button" class="btn btn-info" onclick="runTask(this)">run</button>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</table>
					<!-- <ul class="pager">
						<li><a href="#">&laquo;</a></li>
						<li><a href="#">1</a></li>
						<li><a href="#">2</a></li>
						<li><a href="#">3</a></li>
						<li><a href="#">4</a></li>
						<li><a href="#">5</a></li>
						<li><a href="#">&raquo;</a></li>
					</ul> -->
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<script src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
	<script src="${ctx}/static/js/bootstrap.min.js"></script>
	<script type="text/javascript">
    	function addTask(){
    		location.href="${ctx}/task/updatetask";
		}
    	
    	function searchTasks(){
    		$.ajax({
    			type:'post',
    			url:'${ctx}/task/searchtasks',
    			dataType:'json',
    			data:$("#taskForm").serialize(),
    			success:function(data){
    				var e = eval(data);
    				if(e.msg == "success"){
    					$("#taskTable").remove();
    					var html = "<table id=\"taskTable\" class=\"table table-bordered\"><tr><th>Task Name</th><th>Standard File Name</th><th>Update Time</th><th>Schedule Type</th><th>Status</th><th>Operation</th></tr>";
    					$.each(e.list,function(i,item){
    						html = html + "<tr class=\"active\"><td>"+item.taskName+"</td><td>"+item.fileName+"</td><td>"+item.updateTime+"</td><td>"+item.scheduleType+"</td><td>"+item.status+"</td><td>"
								  +"<button type=\"button\" class=\"btn btn-info\">mod</button>&nbsp;<button type=\"button\" class=\"btn btn-info\">del</button>&nbsp;<button type=\"button\" class=\"btn btn-info\">results</button></td></tr>";
    					});
    					html = html + "</table>";
    					$("#content-panel").append(html);
    				}else if(e.msg = "failure"){
    					alert("search failure");
    				}
    			}
    		});
    	}
    	
    	function editTask(k){
    		var rowKey = $(k).parent().parent().find("input[type='hidden']").val();
    		if(rowKey == undefined){
    			return false;
    		}
    		location.href="${ctx}/task/updatetask?rowKey="+rowKey;
    	}
    	
    	function deleteTask(k){
    		var rowKey = $(k).parent().parent().find("input[type='hidden']").val();
    		if(rowKey == undefined){
    			return false;
    		}
    		$.ajax({
    			type:'post',
    			url:'${ctx}/task/deletetask',
    			dataType:'json',
    			data:{"rowKey":rowKey},
    			success:function(data){
    				var e = eval(data);
    				if(e.msg == "success"){
    					alert("delete task success");
    					location.reload();
    				}else if(e.msg = "failure"){
    					alert("delete failure");
    				}
    			}
    		});
    	}
    	
    	function runTask(k){
    		var rowKey = $(k).parent().parent().find("input[type='hidden']").val();
    		if(rowKey == undefined){
    			return false;
    		}
    		$.ajax({
    			type:'post',
    			url:'${ctx}/task/runtask',
    			dataType:'json',
    			data:{"rowKey":rowKey},
    			success:function(data){
    				var e = eval(data);
    				if(e.msg == "success"){
    					alert("start task success");
    					location.reload();
    				}else if(e.msg = "failure"){
    					alert("start task failure");
    				}
    			}
    		});
    	}
    </script>
</body>
</html>