<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	
	<style>
	table thead th
	{
		text-align: center;	
	}
	
	#result1 .number
	{
		text-align: right;
	}
	
	#result2 .number
	{
		text-align: right;
	}
	
	#result1 .text
	{
		text-align: left;
	}	
	</style>
	
	<div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">지수 프로파일 레포트</h1>
        </div>
        <!-- /.col-lg-12 -->
    
    <!--  인풋 컨트롤 박스 -->
    <div id='input_box_1'>    	
	    <table class="table">
		    <tr>
			    <td><label for="index_search">지수 선택 : </label></td>
			    <td><input id="index_search" type="text" class="typeahead" style='width: 300px'></td>
		    </tr>
		    <tr>
			    <td><label for="benchmark_search">벤치마크 선택 : </label></td>
			    <td><input id="benchmark_search" type="text" class="typeahead" style='width: 300px'></td>
		    </tr>
		    <tr>
			    <td><label for=" dt">기준일(T0) 선택 : </label></td>
			    <td><a><input name="dt" type="text" id="dt_t0" style='width: 300px'></a></td>
		    </tr>
		    <tr>
			    <td><label for=" dt">기준일(T1) 선택 : </label></td>
			    <td><a><input name="dt" type="text" id="dt_t1" style='width: 300px'></a></td>
		    </tr>  
	    </table>
	    <div style="margin-bottom: 10px">
	    	<input type="button" value="결과 출력" class="btn btn-success" onclick="refresh_data();"/>
	    	<input id="btn_copy" type="button" value="Copy to clipboard" class="btn btn-default"/>
    	</div>
    </div>    
    <div class="row">
          <div class="col-md-12 col-lg-11">
			<div class="panel panel-default">
		        <div id='result1_info' class="panel-heading">		        	                                       
		        </div>
		        		        
		        <div id="constitution_result" class="panel-body">		        	
		            <table id="result1" class="table table-striped table-bordered" style="max-width:500px">		            	
		            </table>
		            
		            <table id="result2" class="table table-striped table-bordered" style="max-width:500px">		            	
		            </table>
			    </div>
		   </div>
		   
		   <div class="panel panel-default">
			   	<div id='chart' class="panel-body">
			   		<div class="panel-heading">
				            <i class="fa fa-bar-chart-o fa-fw"></i> 차트                            
				     </div>
				     <div id="chart" class="panel-body">                     
			    </div>
		   </div>
	   </div>
    </div>
	
	<script>
		$(function(){
			
			// Copy to clipboard 초기화
			initialize_copy_module();
			
			// 인풋 초기화;
			$('#index_search').val('FI00.WLT.LVL(low vol)');
			$('#benchmark_search').val('I.101(KOSPI200)');
			$('#dt_t0').val('20010102');
			$('#dt_t1').val('20140709');
			
			
			$('#index_search').val('FI00.WLT.LVL(low vol)');
			
			
			$.ajax({
				  url: '${pageContext.request.contextPath}/stock/ucdlist',
				  contentType: "application/x-www-form-urlencoded; charset=UTF-8",
				  dataType: "json",
				  success: function(data){
					  $('.typeahead').typeahead(data);
	              },
	              error: function(XMLHttpRequest, textStatus, errorThrown) {	            	  
	                  alert("Status: " + textStatus); alert("Error: " + errorThrown); 
	               }
				});
		});
		
		$(function(){
        	$('#dt_t0, #dt_t1').datepicker(
        				{
	        				dateFormat: 'yymmdd',
	        				onSelect: function(){
	        					if($('#service').val() == "")
        						{
	        						action_menu($('/home').val());
        						}	        					
	        				}
        				}
        			);
        });
		
		function initialize_copy_module()
		{
			var clipBoard = new ZeroClipboard.Client();
						
			//cursor hand type 
			clipBoard.setHandCursor( true );   
			//swf화일 경로 설정.
			ZeroClipboard.setMoviePath("${pageContext.request.contextPath}/resources/js/ZeroClipboard.swf");
			//버튼 활성화
			clipBoard.glue('btn_copy');
			//마우스 클릭시에 이벤트 발생. - 저장한다.
			clipBoard.addEventListener('mouseOver', function (client) {				
				clipBoard.setText($('#constitution_result').html());		
			});
			
			//카피 완료시에. - 저장한다.
			clipBoard.addEventListener('complete', function (client) {
				alert('클립보드에 저장 완료');		
			});
		}
		
		function refresh_data()
		{	
			$('#constitution_result').hide();
			
			var input_u_cd = $('#index_search').val().split('(')[0];
			var input_bm = $('#benchmark_search').val().split('(')[0];
			
			$.ajax({
				type: 'post',				
				url: '${pageContext.request.contextPath}/stock/profile/json',
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",				
				dataType: "json",
				beforeSend: function(){
				     $("#loading").show();
				},
				complete: function(){
				     $("#loading").hide();
				},
				data: {u_cd:input_u_cd, u_cd_bm:input_bm, t0:$('#dt_t0').val(), t1:$('#dt_t1').val()},				
				success: function(data){					
					// 지수정보 작업
					var info = '';
					info = info + '시계열 첫시점 : ' + data.정보.T0 + '<br/>';
					info = info + '시계열 마지막  : ' + data.정보.T1 + '<br/>';					
					$('#result1_info').html(info);
										
					// 테이블 작업
					var div = $('#result1');					
					var html = '';
					html = html + "<thead><th colspan='3'>리턴 프로파일</th></thead>";
					html = html + "<thead><th style='width:200px;'>기간</th><th style='width:150px;'>" + data.리턴프로파일.정보.지수코드 + "</th><th style='width:150px;'>" + data.리턴프로파일.정보.벤치마크지수코드 + "</th></thead>"
					html = html + "<tr><td>1주일</td><td class='number'>" + data.리턴프로파일.인덱스.W1 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.W1 + "</td></tr>";										
					html = html + "<tr><td>1개월</td><td class='number'>" + data.리턴프로파일.인덱스.M1 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.M1 + "</td></tr>";
					html = html + "<tr><td>3개월</td><td class='number'>" + data.리턴프로파일.인덱스.M3 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.M3 + "</td></tr>";
					html = html + "<tr><td>6개월</td><td class='number'>" + data.리턴프로파일.인덱스.M6 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.M6 + "</td></tr>";
					html = html + "<tr><td>1년</td><td class='number'>" + data.리턴프로파일.인덱스.Y1 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.Y1 + "</td></tr>";
					html = html + "<tr><td>3년</td><td class='number'>" + data.리턴프로파일.인덱스.Y3 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.Y3 + "</td></tr>";
					html = html + "<tr><td>5년</td><td class='number'>" + data.리턴프로파일.인덱스.Y5 + "</td><td class='number'>" + data.리턴프로파일.벤치마크.Y5 + "</td></tr>";
					html = html + "<tr><td>전체</td><td class='number'>" + data.리턴프로파일.인덱스.TOTAL + "</td><td class='number'>" + data.리턴프로파일.벤치마크.TOTAL + "</td></tr>";
					div.html(html);
					
					div = $('#result2');
					html = '';
					html = html + "<thead><th colspan='3'>리스크 프로파일</th></thead>";
					html = html + "<thead><th style='width:200px;'></th><th style='width:150px;'>" + data.리턴프로파일.정보.지수코드 + "</th><th style='width:150px;'>" + data.리턴프로파일.정보.벤치마크지수코드 + "</th></thead>"
					html = html + "<tr><td>GR</td><td class='number'>" + data.리스크프로파일.인덱스.GR + "</td><td class='number'>" + data.리스크프로파일.벤치마크.GR + "</td></tr>";										
					html = html + "<tr><td>VOL</td><td class='number'>" + data.리스크프로파일.인덱스.VOL + "</td><td class='number'>" + data.리스크프로파일.벤치마크.VOL + "</td></tr>";
					html = html + "<tr><td>Sharp-ratio</td><td class='number'>" + data.리스크프로파일.인덱스.SR + "</td><td class='number'>" + data.리스크프로파일.벤치마크.SR + "</td></tr>";
					html = html + "<tr><td>Beta</td><td class='number'>" + data.리스크프로파일.인덱스.BETA + "</td><td class='number'>" + "</td></tr>";
					html = html + "<tr><td>Alpha</td><td class='number'>" + data.리스크프로파일.인덱스.ALPHA + "</td><td class='number'>" + "</td></tr>";
					html = html + "<tr><td>TE</td><td class='number'>" + data.리스크프로파일.인덱스.TE + "</td><td class='number'>" + "</td></tr>";
					html = html + "<tr><td>IR</td><td class='number'>" + data.리스크프로파일.인덱스.IR + "</td><td class='number'>" + "</td></tr>";					
					div.html(html);
					
					// 차트 생성
					DrawChart(data.정보.SERIES1, data.정보.지수이름, data.정보.SERIES2, data.정보.벤치마크이름);
					
					initialize_copy_module();
					$('#constitution_result').show('slide', {direction:'up'}, 1000);
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown) {	            	  
	              alert("Status: " + textStatus); alert("Error: " + errorThrown); 
	            }
			});
		}
		
		function DrawChart(series1, u_nm, series2, u_nm2)
		{
			var seriesOptions = [];
		  	seriesOptions[0] = {
		  			name : u_nm,
		  			data : series1  			
		  	};		  	
		  	seriesOptions[1] = {
			 		name : u_nm2,	 		
			 		data : series2,
			 		yAxis : 1
		  	};
		  	
			$("#chart").highcharts(
	  				'StockChart', {
	  					chart:{  						
	  					},
	  					title:{
	  						text : u_nm + ' - ' + u_nm2
	  					},
	  					xAxis:{
	  						type: 'datetime',
	  						labels:{
	  							formatter: function(){
	  								return Highcharts.dateFormat('%Y/%m/%d', this.value);
	  							}
	  						}
	  					},
	  					tooltip:{
	  						xDateFormat: '%Y/%m/%d',
	  						shared: true
	  					},
	  					yAxis:[
							{title: {
							 text: u_nm
							 },  					    
							 offset: 0
							}
	  					   ,{
	  						title: {
	  					        text: u_nm2
	  					    },  					    
	  					    offset: 0,  					    
	  					    opposite: true
	  					}],
	  					series : seriesOptions
	  				}
	  			);
		}
	</script>
    

    
    