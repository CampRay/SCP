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
<link rel="shortcut icon" href="/scp_web_platform/static/images/favicon.png">
<title>Mining List</title>
<link href="/scp_web_platform/static/css/bootstrap.css" rel="stylesheet">
<link href="/scp_web_platform/static/css/navbar.css" rel="stylesheet">
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
							src="/scp_web_platform/static/images/setting.png" />Change Pwd</a></li>
					<li><a href="#"><img
							src="/scp_web_platform/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img
							src="/scp_web_platform/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li class="active"><a href="/scp_web_platform/task/tasklist">Task
							Manage</a></li>
					<li><a href="/scp_web_platform/algorithm/algorithmlist">Algorithm
							Manage</a></li>
					<li><a href="/scp_web_platform/source/sourcelist">Source
							Manage</a></li>
					<li><a href="/scp_web_platform/inter/interlist">Interface
							Manage</a></li>
					<li><a href="/scp_web_platform/config/configlist">Config
							Manage</a></li>
					<li><a href="/scp_web_platform/user/userlist">User Manager</a></li>
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
							<li><a href="${ctx}/task/tasklist">Task List</a></li>
							<li class="active"><a href="${ctx}/task/mininglist">Mining List</a></li>
							<li><a href="${ctx}/task/executeresult">Execute Result</a></li>
							<li><a href="${ctx}/task/miningresult">Mining Result</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-md-10">
				<div class="panel panel-default" id="content-panel">
					<div class="panel-heading">
						<div class="alert alert-info" style="margin-bottom: 0px;">Mining
							List</div>
					</div>
					<div class="panel-body">
						<form:form action="/scp_web_platform/task/mininglist" method="get" modelAttribute="mining" role="form" id="taskForm">
							<div class="form-group">
								<label class="col-sm-2 control-label" style="text-align:right;">Mining Name：</label>
								<div class="col-sm-7">
									<form:input path="name" id="name" class="form-control" value="${name}"/>
								</div>
								<div class="col-sm-3">
									<button type="submit" class="btn btn-default">Search</button>
									&nbsp;
								<button type="button" class="btn btn-default" onClick="addMining();">Add</button>
								</div>
							</div>
						</form:form>
					</div>
					<c:if test="${not empty minings}">
						<table class="table table-bordered">
							<tr class="success">
								<th>Name</th>
								<th>Source</th>
								<th>Start Time</th>
								<th>Trigger Type</th>
								<th>Status</th>
								<th>Operation</th>
							</tr>
							<c:forEach items="${minings}" var="item" varStatus="status">
								<tr class="warning">
									<td><input type="hidden" value="${item.rowKey}">${item.name}</td>
									<td>${item.source}</td>
									<td><fmt:formatDate value="${item.start}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td>${item.trigger}</td>
									<td>
										<c:choose>
											<c:when test="${item.status eq 0}">
												ready
											</c:when>
											<c:when test="${item.status eq 1}">
												running
											</c:when>
											<c:when test="${item.status eq 2}">
												interrupt
											</c:when>
											<c:when test="${item.status eq 3}">
												finished
											</c:when>
										</c:choose>
									</td>
									<td>
										<c:if test="${empty item.status}">
											<button type="button" class="btn btn-info" onclick="editMining(this)">mod</button>&nbsp;
										</c:if>
										<button type="button" class="btn btn-info" onclick="deleteMining(this)">del</button>&nbsp;
										<c:if test="${empty item.status}">
											&nbsp;<button type="button" class="btn btn-info" onclick="runMining(this)">run</button>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<script src="/scp_web_platform/static/js/jquery-1.10.2.min.js"></script>
	<script src="/scp_web_platform/static/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		function addMining(){
			location.href="${ctx}/task/updatemining";
		}
		
		function editMining(k){
    		var rowKey = $(k).parent().parent().find("input[type='hidden']").val();
    		if(rowKey == undefined){
    			return false;
    		}
    		location.href="${ctx}/task/updatemining?rowKey="+rowKey;
    	}
		
    	function deleteMining(k){
    		var rowKey = $(k).parent().parent().find("input[type='hidden']").val();
    		if(rowKey == undefined){
    			return false;
    		}
    		$.ajax({
    			type:'post',
    			url:'${ctx}/task/deletemining',
    			dataType:'json',
    			data:{"rowKey":rowKey},
    			success:function(data){
    				var e = eval(data);
    				if(e.msg == "success"){
    					alert("delete mining schedule success");
    					location.reload();
    				}else if(e.msg = "failure"){
    					alert("delete mining schedule failure");
    				}
    			}
    		});
    	}
    	
    	function runMining(k){
    		var rowKey = $(k).parent().parent().find("input[type='hidden']").val();
    		if(rowKey == undefined){
    			return false;
    		}
    		$.ajax({
    			type:'post',
    			url:'${ctx}/task/runmining',
    			dataType:'json',
    			data:{"rowKey":rowKey},
    			success:function(data){
    				var e = eval(data);
    				if(e.msg == "success"){
    					alert("start mining schedule success");
    					location.reload();
    				}else if(e.msg = "failure"){
    					alert("start mining schedule failure");
    				}
    			}
    		});
    	}
	</script>
</body>
</html>