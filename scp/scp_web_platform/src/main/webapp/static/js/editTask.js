/**
 * editTask v1.0.0 by
 * 
 * @tracy.li Copyright 2013 MissionSky, Inc.
 * 
 */
var ctx = $('meta[name="ctx"]').attr('content');
$(document).ready(function(e) {
	var value = $("#rowKey").val();
	if(value == ""){
		$("#showTitle").text("Add Task");
		$("title").text("Add Task");
	}else{
		$("#showTitle").text("Update Task");
		$("title").text("Update Task");
	}
	
	$("#taskForm").validationEngine({
		ajaxFormValidation : true,
		promptPosition : "topLeft",
		ajaxFormValidationMethod:'POST',
		onAjaxFormComplete : function(errorInForm, form, json,options){
			var e = eval(json);
			if (e.msg == "success") {
				alert('save success！');
				window.location.href = ctx+"/task/tasklist";
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
	
	$("#paramForm").validationEngine({  
        validationEventTriggers : "keyup blur",  
        promptPosition : "topLeft",  
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

	$("#dialogDiv").dialog({
		autoOpen : false,
		height : 380,
		width : 800,
		modal : true,
		title : 'Add Action',
		buttons:{
			"OK":function(){
				var flag = jQuery("#paramForm").validationEngine("validate");
				if(flag){
					var index = 0;
					var html = "<div class=\"action-params\"></div>";
					var select = $("#paramForm").find("select");
					var key = $(select).val();
					var value = $(select).find("option:selected").text();
					var description = $("#paramForm").find("div:eq(1)").find("input:eq(0)").val();
					var flag = false;
					if($(".action-params").length>0){
						$(".action-params").each(function(i,item){
							if(key == $(this).find("input:eq(0)").val()){
								flag = true;
								index = parseInt($(this).find("input:eq(0)").attr("indexData"));
								$(this).find(".form-group:eq(1)").find("div").empty();
								var htmls = "";
								$("#paramForm .stepParam").each(function(i,item){
									var name = $(this).find("label").text();
									var value = $(this).find("input").val();
									htmls = htmls + "<dl class='dl-horizontal'><input name='actions["+index+"].params["+i+"].name' type='hidden' value="+name+"><input name='actions["+index+"].params["+i+"].value' type='hidden' value="+value+"><dt>"+name+":</dt><dd>"+value+"</dd></dl>";
								});
								$(this).find(".form-group:eq(1)").find("div").html(htmls);
								return false;
							}
						});
						if(index == 0){
							index = parseInt($(".action-params:last").find("input:eq(0)").attr("indexData") + 1);
						}
					}
					if(flag){
						$(this).dialog("close");
						return false;
					}
					html = html + "<div class='form-group'><input name='actions["+index+"].rowId' type='hidden' value="+key+" indexData="+index+">"
								+ "<label class='col-sm-2 control-label'>Algorithm Name:</label><div class='col-sm-3'><input name='actions["+index+"].name' class='form-control validate[required]' readonly='readonly' type='text' value="+value+"></div>"
								+ "<div class='col-sm-5'><div class='input-group'><span class='input-group-addon'>describe</span><input name='actions["+index+"].description' class='form-control' readonly='readonly' type='text' value="+description+"></div></div>"
								+ "<div class='col-sm-2'><input type='button' class='btn btn-info' value='edit' onclick='editStep(this)'><input type='button' class='btn btn-info' value='del' onclick='delStep(this)'></div></div>";
					if($(".stepParam").length > 0){
						html = html + "<div class='form-group'><label class='col-sm-2 control-label'>Params：</label><div class='col-sm-10'>";
						$("#paramForm .stepParam").each(function(i,item){
							var name = $(this).find("label").text();
							var value = $(this).find("input").val();
							html = html + "<dl class='dl-horizontal'><input name='actions["+index+"].params["+i+"].name' type='hidden' value="+name+"><input name='actions["+index+"].params["+i+"].value' type='hidden' value="+value+"><dt>"+name+":</dt><dd>"+value+"</dd></dl>";
						});
						html = html + "</div></div>";
						alert(html);
					}
					if($(".action-params").length > 0){
						$("#taskForm").find(".action-params:last").after(html);
					}else{
						$("#taskForm").find(".form-group:last").before(html);
					}
					$(this).dialog("close");
				}
			}
		}
	});
});

function addStep() {
	$("#dialogDiv").find("select").attr("disabled",false);
	$("#dialogDiv").find("select").val("");
	$("#dialogDiv").find("input").val("");
	$("#paramForm").validationEngine("hideAll");
	$("#dialogDiv").dialog("open");
}

function editStep(k) {
	var key = $(k).parent().parent().parent().find("input:eq(0)").val();
	$("#dialogDiv").find("select").attr("disabled",false);
	$("#dialogDiv").find("select").val(key);
	stepChange($("#dialogDiv").find("select"));
	$("#dialogDiv").find("select").attr("disabled",true);
	var params = $(k).parent().parent().next();
	if(params != undefined && params != null){
		$(params).find("dl").each(function(i,item){
			$("#paramForm").find(".stepParam:eq("+i+")").find("input").val($(this).find("input:eq(1)").val());
		});
	}
	$("#paramForm").validationEngine("hideAll");
	$("#dialogDiv").dialog("open");
}

function delStep(k) {
	$(k).parent().parent().parent().remove();
}

function radioclick(k){
	var value = $(k).val();
	$(".task-time").hide();
	$(".start-week input[type='radio']").each(function(i,item){
		$(this).removeClass("validate[required]");
	});
	if(value == 1){
		$("#start-time").show();
		$("#start-day").show();
	}else if(value == 2){
		$("#start-time").show();
		$(".start-week").show();
		$(".start-week input[type='radio']").each(function(i,item){
			$(this).addClass("validate[required]");
		});
	}else if(value == 3){
		$("#start-time").show();
	}
}

function stepChange(k) {
	$(".stepParam").remove();
	if($(k).val() == undefined){
		return false;
	}
	$.ajax({
		type:'post',
		url:ctx+'/task/getalgorithmparam',
		data:{"rowKey":$(k).val()},
		async:false,
		dataType:'json',
		success:function(data){
			var e = eval(data);
			if(e.msg == "success"){
				var list = e.list;
				if(list != null){
					var html = "";
					$.each(list,function(i,item){
						html = html + "<br><div class=\"row stepParam\"><label class=\"col-sm-2\" style=\"text-align: right;\">"+item+"</label><input type=\"text\" class='col-sm-10 validate[required,custom[onlyLetterNumber]]'></div>";
					});
					$(k).parent().next().after(html);
				}
				var description = e.description;
				$(k).parent().next().find("input").val(description);
			}else if(e.msg = "failure"){
				alert("search failure");
			}
		}
	});
}