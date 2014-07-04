<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="UTF-8"%>
    
<script type="text/javascript" src="/js/jqcloud/jqcloud-1.0.4.js"></script>

<div class="row">

<div class="col-lg-12">

 <div style="text-align: center;">
 	<h3>오늘의 주식 뉴스를 한눈에!</h3>		

<form method="post" action="/servlet/rsm.snc.WordCloudServlet">

	<input id="type_input" type="hidden" name="type" value="<%=request.getAttribute("type")%>"/>

	<p>
		<div class="btn-group btn-group-sm">
		  <button id="btn_b" type="submit" class="btn btn-default" onclick="$('#type_input').val('b');">유난히 튀는</button>
		  <button id="btn_a" type="submit" class="btn btn-success" onclick="$('#type_input').val('a');">비중이 높은</button>
		  <!-- <button id="btn_c" type="submit" class="btn btn-default" onclick="$('#type_input').val('c');">공시 관련</button> -->
		</div>		
	</p>

	<p>
		<input name="dt" type="text" id="datepicker">
		<button type="submit" class="btn btn-default">Refresh</button>		
	</p>
	 
	<div id="my_favorite_latin_words" style="width: 100%; height: 500px; border: 0px solid #ccc; margin-top: 30px;"></div>
	
</form>

</div>

<div id="div_news" style="margin-top: 30px;">
	<button id="btn_back" class="btn btn-default"><span class="glyphicon glyphicon-refresh"> </span> Wordcloud로 돌아가기</button>
	<table id="table_news" class="table table-striped" style="margin-top: 50px;">
	</table>
</div>

</div>

</div>

<script type="text/javascript">
	      var word_list = [
		];	      
	      $(function() {
	         
	    	 $("#my_favorite_latin_words").jQCloud(word_list);
	         $("#datepicker").datepicker(
	        		 	{
	        		 		dateFormat: 'yymmdd',
	        		 		onSelect: function(){
	        		 			$("form").submit();
	        		 		}
	        			 }
	        		 );
	         $("#datepicker").val("<%=request.getAttribute("dt")%>");
	         
	         var type = $("#type_input").val();
	         
	         $("button[class='btn btn-success']").attr("class", "btn btn-default");
	         
	         if(type == "a") {
	        	 $("#btn_a").attr("class", "btn btn-success");
	        } else if(type == "b") {
	        	 $("#btn_b").attr("class", "btn btn-success");
	        };
	        
	        $("#btn_back").bind('click', function(){
	        	
	        	
	            var effect = 'slide';
	            var options = { direction: 'left'};
	            var duration = 1000;
	            
	        	$("#div_news").hide();
	        	$("#div_cloud").toggle(effect, options, duration);
	        });
	        
	       });
	      
	      $(function(){		
	  		$("li").removeClass("active");
	  		$("#li_snc").addClass("active");
	  		$("#div_news").hide();
	  	});	      
	 </script>	
	 
	 
	 <script type="text/javascript">
	 
	 	function loadData(gicode)
	 	{
	 		var dt = $('#datepicker').val();
	 		var gicode2 = gicode;
	 		
	 		$.ajax({                
                type: "post",
                data: {
                    dt: dt,
                    gicode: gicode2                    
                },
                url: "renacer.cafe24.com/servlet/rsm.snc.NewsListServlet",
                dataType: 'xml',
                success: function (data) {                	
                	var records = $(data).find("news");                    
                    var newsSize = records.size();                    
                    $("#table_news").html("");                    
                    $("#table_news").append("<thead><th>시각</th><th>뉴스 제목</th><th>출처</th></thead>");
                    $("#table_news").append("<tbody>");
                    for(i=0; i<newsSize; i++) {
                    	var row = "<tr>" + "<td>" + $(records[i]).find("pub_dt").text() + "</td>" + "<td><a href='" +  $(records[i]).find("url").text() + "' target='_blank'>" + $(records[i]).find("title").text() + "</a></td>" + "<td>" + $(records[i]).find("provider").text() + "</td>" + "</tr>";
                    	$("#table_news").append(row);                    	                    	
                    }
                    $("#table_news").append("</tbody>");
                    
                    var effect = 'slide';
                    var options_right = { direction: 'right' };
                    var duration = 1000;            
                    
                    $("#div_cloud").hide();
                    $("#div_news").toggle(effect, { direction: 'right' }, duration);
                }
            });
	 		
	 	}
	 
	 </script>