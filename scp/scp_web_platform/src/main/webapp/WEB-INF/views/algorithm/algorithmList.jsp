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
<title>Algorithm List</title>
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
					<li><a href="#"><img src="${ctx}/static/images/help.png" />Help</a></li>
					<li><a href="<%=request.getContextPath()%>/logout"><img src="${ctx}/static/images/logout.png" />Exit</a></li>
				</ul>
				<ul class="nav navbar-nav">
					<li><a href="${ctx}/task/tasklist">Task
							Manage</a></li>
					<li class="active"><a href="${ctx}/algorithm/algorithmlist">Algorithm
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
						<div class="alert alert-info" style="margin-bottom:0px;">Algorithm List</div>
					</div>
					<div class="panel-body">
						<form method="get"
							action="${ctx}/algorithm/algorithmlist">
							<div class="form-group">
								<label for="algorithmName" class="col-sm-3"
									style="text-align: right;">Algorithm Name：</label>
								<div class="cpl-sm-offset-3 col-sm-6">
									<input name="name" type="text" id="algorithmName"
										class="form-control" value="${algorithmName}"/>
								</div>
								<button type="submit" class="btn btn-default">Search</button>
								&nbsp;
								<button type="button" class="btn btn-default"
									onClick="addTask();">Add New</button>
							</div>
						</form>
					</div>
					<c:if test="${not empty algorithms}">
					<table class="table table-bordered">
						<tr class="active">
							<th class="col-md-2">Algorithm Name</th>
							<th class="col-md-5">Description</th>
							<th class="col-md-1">type</th>
							<th class="col-md-2">Operation</th>
						</tr>
							<c:forEach items="${algorithms}" var="item" varStatus="status"
								begin="0" step="1">
								<tr class="success">
									<td>
										<input type="hidden" value="${item.rowKey}">
										${item.name}
									</td>
									<td>${item.description}</td>
									<td>
									<c:choose>
										<c:when test="${item.type eq 1}">
											BasicAlgothm
										</c:when>
										<c:when test="${item.type eq 2}">
											MinerAlgorithm
										</c:when>
										<c:otherwise>
											&nbsp;
										</c:otherwise>
									</c:choose>
									</td>
									<td>
										<button type="button" class="btn btn-info"
											onclick="modAlgorithm(this)">mod</button>&nbsp;
										<button type="button" class="btn btn-info" onclick="delAlgorithm(this)">del</button>
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
			location.href="${ctx}/algorithm/updatealgorithm";
		}
    	
    	function modAlgorithm(k){
    		var rowkey = $(k).parent().parent().find("input[type='hidden']").val();
    		location.href="${ctx}/algorithm/updatealgorithm?rowkey="+rowkey;
    	}
    	
    	function delAlgorithm(k){
    		var rowkey = $(k).parent().parent().find("input[type='hidden']").val();
    		$.ajax({
    			type:'post',
    			url:'${ctx}/algorithm/deletealgorithm',
    			data:{"rowKey":rowkey},
    			dataType:'json',
    			success:function(data){
    				var e = eval(data);
    				if(e.msg == "success"){
    					alert("delete successful!");
    					location.reload();
    				}else if(e.msg == "failure"){
    					alert("delete failure!");
    				}
    			}
    		});
    	}
    </script>
</body>
</html>