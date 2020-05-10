$(function(){
	CKEDITOR.replace('messagetext');
	CKEDITOR.instances.messagetext.setData( '<p>This is the editor data.</p>' );
	$("#publishBtn").click(publish);

});
function publish() {
	$("#publishModal").modal("hide");
	/*var token=$("mata[name='_csrf']").attr("content");
	var header=$("mata[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e,xhr,option) {
		xhr.setRequestHeader(header,token);
	});*/
	//获取标题和内容
	var title=$("#recipient-name").val();
	var content=CKEDITOR.instances.messagetext.getData();
	//发送异步请求(POST)
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function(data) {
			data=$.parseJSON(data);
			//提示框显示返回的消息
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			//显示提示框，两秒后自动隐藏
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if (data.code==0){
					//alert(data.msg);
					window.location.reload();
				}
			}, 2000);
		}
	);
}
