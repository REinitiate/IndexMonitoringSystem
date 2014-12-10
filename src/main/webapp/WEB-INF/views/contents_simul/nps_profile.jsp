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
            <h1 class="page-header">지수 프로파일 레포트(NPS 제출용)</h1>
        </div>
        <!-- /.col-lg-12 -->
    
    <!--  인풋 컨트롤 박스 -->
    <div id='input_box_1'>    	
	    <table class="table">
	    	<tr>
			    <td><label for="simul_id">시뮬레이션 아이디 : </label></td>
			    <td><input id="simul_id" type="text" style='width: 300px'></td>
		    </tr>
		    <tr>
			    <td><label for="u_cd">지수 코드 : </label></td>
			    <td><input id="u_cd" type="text" style='width: 300px'></td>
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
    <div id="contents" class="row">
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
		   
		   <!-- <div class="panel panel-default">
			   	<div id='chart' class="panel-body">
			   		 <div class="panel-heading">
				        <i class="fa fa-bar-chart-o fa-fw"></i> 차트                            
				     </div>
				     <div id="chart" class="panel-body">                     
			    	</div>
		   		</div>
	   		</div> -->
    	</div>
    </div>
	
	<script>
		$(function(){
			
			// Copy to clipboard 초기화
			initialize_copy_module();
			
			// 인풋 초기화;
			$('#u_cd').val('UNIV.SCR');
			$('#simul_id').val('NPSD9');			
			$('#benchmark_search').val('NPS.BM');
			$('#dt_t0').val('20010102');
			$('#dt_t1').val('20141031');
			
			
			// 지수 선택 자동완성
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
			
			//refresh_data();
			
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
			$('#contents').hide();
			
			var input_simul_id = $('#simul_id').val().trim();
			var input_u_cd = $('#u_cd').val().trim();
			var input_bm = $('#benchmark_search').val().split('(')[0];
			
			$.ajax({
				type: 'post',				
				url: '${pageContext.request.contextPath}/nps/simul/profile/json',
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",				
				dataType: "json",
				beforeSend: function(){
				     $("#loading").show();
				},
				complete: function(){
				     $("#loading").hide();
				},
				data: {simul_id:input_simul_id, u_cd:input_u_cd, u_cd_bm:input_bm, t0:$('#dt_t0').val(), t1:$('#dt_t1').val()},				
				success: function(data){					
					// 지수정보 작업
					var info = '';
					info = info + 'SIMUL_ID : ' + data.header.simul_id + '<br/>';
					info = info + 'U_CD  : ' + data.header.u_cd + '<br/>';
					info = info + '시계열 첫시점 : ' + data.header.t0 + '<br/>';
					info = info + '시계열 마지막  : ' + data.header.t1 + '<br/>';					
					$('#result1_info').html(info);
					
					var div = $('#result2');
					html = '';					 
					html = html + "<thead><th>구분</th><th colspan='2'>주요 항목</th><th>지표값</th><</thead>";
					html = html + "<tr><td rowspan='3'>수익성</td><td colspan='2'>누적 연환산 초과수익률</td><td class='number'>" + data.stats.gr.percent(4) + "</td></tr>";
					html = html + "<tr><td colspan='2'>IR</td><td class='number'>" + data.stats.ir_nps.toFixed(4) + "</td></tr>";
					html = html + "<tr><td colspan='2'>Hit ratio</td><td class='number'>" + data.stats.hit_ratio.toFixed(4) + "</td></tr>";
										
					html = html + "<tr><td rowspan='2'>변동성</td><td colspan='2'>TE</td><td class='number'>" + data.stats.te_nps.percent(4) + "</td></tr>";
					html = html + "<tr><td colspan='2'>1주 Maximum drawdown</td><td class='number'>" + data.stats.maximum_drawdown.percent(4) + "</td></tr>";

					html = html + "<tr><td rowspan='3'>유동성</td><td colspan='2'>포트폴리오 Liquidation 소요 일수</td><td class='number'>" + data.liq.liqu_day.toFixed(2) + "</td></tr>";						
					html = html + "<tr><td colspan='2'>리밸런싱 회전율 평균</td><td class='number'>" + data.turnover.turnover_average.percent(4) + "</td></tr>";					
					html = html + "<tr><td colspan='2'>평균 종목수</td><td class='number'>" + data.liq.avg_cnt.toFixed(2) + "</td></tr>";
										
					html = html + "<tr><td rowspan='4'>지수특성</td><td rowspan='3'>BM초과수익률<br/>상관계수</td><td>1.KOSPI</td><td class='number'>" + data.stats.corr_kospi.toFixed(4) + "</td></tr>";
					html = html + "<tr><td>2.KOSPI100</td><td class='number'>" + data.stats.corr_kospi100.toFixed(4) + "</td></tr>";
					html = html + "<tr><td>3.KOSPI200</td><td class='number'>" + data.stats.corr_kospi200.toFixed(4) + "</td></tr>";
					html = html + "<tr><td colspan='2'>BM 복제율</td><td class='number'>" + data.liq.dup_rate.percent(4) + "</td></tr>";
					/*
					html = html + "<tr><td>VOL</td><td class='number'>" + data.리스크프로파일.인덱스.VOL + "</td><td class='number'>" + data.리스크프로파일.벤치마크.VOL + "</td></tr>";
					html = html + "<tr><td>Sharp-ratio</td><td class='number'>" + data.리스크프로파일.인덱스.SR + "</td><td class='number'>" + data.리스크프로파일.벤치마크.SR + "</td></tr>";
					html = html + "<tr><td>Beta</td><td class='number'>" + data.리스크프로파일.인덱스.BETA + "</td><td class='number'>" + "</td></tr>";
					html = html + "<tr><td>Alpha</td><td class='number'>" + data.리스크프로파일.인덱스.ALPHA + "</td><td class='number'>" + "</td></tr>";
					html = html + "<tr><td>TE</td><td class='number'>" + data.리스크프로파일.인덱스.TE + "</td><td class='number'>" + "</td></tr>";
					html = html + "<tr><td>IR</td><td class='number'>" + data.리스크프로파일.인덱스.IR + "</td><td class='number'>" + "</td></tr>"; */					
					div.html(html);
					
					// 차트 생성										
					//initialize_copy_module();
					
					$('#contents').show('slide', {direction:'up'}, 1000);
					//DrawChart(data.정보.SERIES1, data.정보.지수이름, data.정보.SERIES2, data.정보.벤치마크이름);
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
		
		Number.prototype.percent = function(decimal){
			return (this * 100).toFixed(decimal) + '%';
		}
		
	</script>
    

    
    