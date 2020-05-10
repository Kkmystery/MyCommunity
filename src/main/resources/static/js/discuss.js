var path = localhost + CONTEXT_PATH + "/comment/add/" + [[${post.id}]];
$(function () {
    /* $("#btn_sub1").click(function () {
         $.ajax({
             url:path,
             type:'POST', //GET
             async:true,    //或false,是否异步
             data: $("#replyForm1").serializeArray(),
             contentType: 'application/x-www-form-urlencoded',
             timeout:5000,    //超时时间
             dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text
             success : function(data){
                 alert(data);
                 setTimeout(function(){
                     window.location.reload();
                 }, 1500);
             },
         })
     });*/
    /*$("#btn_sub2").click(function () {
        $.ajax({
            url:path,
            type:'POST', //GET
            async:true,    //或false,是否异步
            data: $("#replyForm2").serializeArray(),
            contentType: 'application/x-www-form-urlencoded',
            timeout:5000,    //超时时间
            dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text
            success : function(data){
                alert(data);
                setTimeout(function(){
                    window.location.reload();
                }, 1500);
            },
        })
    });*/
    $("#btn_sub3").click(function () {
        $.ajax({
            url: path,
            type: 'POST', //GET
            async: true,    //或false,是否异步
            data: $("#replyForm3").serializeArray(),
            contentType: 'application/x-www-form-urlencoded',
            timeout: 5000,    //超时时间
            dataType: 'text',    //返回的数据格式：json/xml/html/script/jsonp/text
            success: function (data) {
                data=$.parseJSON(data);
                alert(data.msg);
                setTimeout(function () {
                    window.location.reload();
                }, 1500);
            },

        });
    })
});
function like(btn,entityType,entityId) {
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId},
        function (data) {
            data=$.parseJSON(data);
            if (data.code==0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
            }else {
                alert(data.msg);
            }

        }
    )


}
function submit1(id) {
    //alert(id);
    var data = document.getElementById(id);
    data=$(data);
    $.ajax({
        url:path,
        type:'POST', //GET
        async:true,    //或false,是否异步
        data: data.serializeArray(),
        contentType: 'application/x-www-form-urlencoded',
        timeout:5000,    //超时时间
        dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text
        success : function(data){
            data=$.parseJSON(data);
            alert(data.msg);
            setTimeout(function(){
                window.location.reload();
            }, 1500);
        }
    })
}

function submit2(id) {
    var data = document.getElementById(id);
    data = $(data);
    $.ajax({
        url: path,
        type: 'POST', //GET
        async: true,    //或false,是否异步
        data: data.serializeArray(),
        contentType: 'application/x-www-form-urlencoded',
        timeout: 5000,    //超时时间
        dataType: 'text',    //返回的数据格式：json/xml/html/script/jsonp/text
        success: function (data) {
            data=$.parseJSON(data);
            alert(data.msg);
            setTimeout(function () {
                window.location.reload();
            }, 1500);
        },
    })
}
