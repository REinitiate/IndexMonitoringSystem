<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	
	<style>
	table thead th
	{
		text-align: center;	
	}
	</style>
	
	<!--  인풋 컨트롤 박스 -->
	<div class="row">
		<div class="col-lg-12">
            <h1 class="page-header">지수 구성 정보</h1>
        </div>
	    <div id='input_box_1'>    	
		    <table class="table">    
		    <tr>
			    <td><label for="index_search">지수 선택 : </label></td>
			    <td><input id="index_search" type="text" class="typeahead" style='width: 300px'></td>
		    </tr>
		    <tr>
			    <td><label for=" dt">주식수기준 날짜 선택 : </label></td>
			    <td><a><input name="dt" type="text" id="dt_stk" style='width: 300px'></a></td>
		    </tr>
		    <tr>
			    <td><label for=" dt">주가기준 날짜 선택 : </label></td>
			    <td><a><input name="dt" type="text" id="dt_prc" style='width: 300px'></a></td>
		    </tr>
		    <tr>
			    <td><label for=" dt">유니브기준 날짜 선택 : </label></td>
			    <td><a><input name="dt" type="text" id="dt_univ" style='width: 300px'></a></td>
		    </tr>	    
		    <tr>
		    	<td><label>가격 기준 : </label></td>
		    	<td>
		    		<div class="btn-group" style="margin-bottom: 10px">		
						<button type="button" class="btn btn-default btn_radio_prc">종가기준</button>
				  		<button type="button" class="btn btn-default btn_radio_prc">기준가기준</button>
				  		<input id='input_prc_type' type="text" class="btn btn-info"/>
					</div>
				</td>		
		    </tr>
		    <tr>
		    	<td><label>유니버스 테이블 기준 : </label></td>
		    	<td>
		    		<div class="btn-group" style="margin-bottom: 10px">
		    			<button type="button" class="btn btn-default btn_radio_univ">FNI_MFI_U_MAP_HIST</button>
		    			<button type="button" class="btn btn-default btn_radio_univ">FNI_STYLE_UNIV</button>
				  		<input id='input_univ_type' type="text" class="btn btn-info"/>
					</div>
				</td>		
		    </tr>    
		    </table>
		    <div style="margin-bottom: 10px">
		    	<input type="button" value="테이블 갱신" class="btn btn-success" onclick="refresh_data();"/>
		    	<input id="btn_copy" type="button" value="Copy to clipboard" class="btn btn-default"/>
	    	</div>
	    </div>  
	</div>
	
	<div id='contents' class="row">
        
        <!-- /.col-lg-12 -->   
      
	    <div class="row">
	          <div class="col-md-12 col-lg-11">
				<div class="panel panel-default">
			        <div id='result1_info' class="panel-heading">		        	                                       
			        </div>		                                
			        <div id="constitution_result" class="panel-body">		        	
			            <table id="result1" class="table table-striped table-bordered">		            	
			            </table>
				    </div>
			   </div>
			   
		   </div>
	    </div>
    
    <div id='result_table' class="panel-body"></div>
	
	</div>
	
	<script>
		$(function(){
			
			// Copy to clipboard 초기화
			initialize_copy_module();
			
			// 인풋 초기화
			$('#dt_univ').val('20140709');			
			$('#dt_prc').val('20140709');
			$('#dt_stk').val('20140709');
			$('#index_search').val('FI00(MKF500)');
			$('#input_prc_type').val('종가기준');
			$('#input_univ_type').val('FNI_MFI_U_MAP_HIST');
			
			//refresh_data();
			
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
        	$('#dt_univ, #dt_prc, #dt_stk').datepicker(
        				{
	        				dateFormat: 'yymmdd',
	        				onSelect: function(){
	        					if($('#service').val() == "")
        						{
	        						action_menu($('/home').val());
        						}
	        					else
        						{
	        						//action_menu($('#service').val());
        						}	        					
	        				}
        				}
        			);
        	
        	// 주가 타입 라디오 버튼 클릭 이벤트
        	$('.btn_radio_prc').click(function(data){        		
        		$('#input_prc_type').val($(this).text());
        	});
        	// 유니버스 테이블 라디오 버튼 클릭 이벤트        	
        	$('.btn_radio_univ').click(function(data){        		
        		$('#input_univ_type').val($(this).text());
        	});
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
			
			var input_dt_univ = $('#dt_univ').val();			
			var input_dt_prc = $('#dt_prc').val();
			var input_dt_stk = $('#dt_stk').val();
			var input_index_name = $('#index_search').val().split('(')[0];
			var input_prc_type = null;
			var input_univ_type = null;
			
			if($('#input_prc_type').val() == '종가기준'){
				input_prc_type = 'cls_prc';
			}
			else if($('#input_prc_type').val() == '기준가기준'){
				input_prc_type = 'std_prc';
			}
			
			if($('#input_univ_type').val() == 'FNI_MFI_U_MAP_HIST'){
				input_univ_type = 'FNI_MFI_U_MAP_HIST';
			}
			else if($('#input_univ_type').val() == 'FNI_STYLE_UNIV'){
				input_univ_type = 'FNI_STYLE_UNIV';
			}
			
			$.ajax({
				type: 'post',				
				url: '${pageContext.request.contextPath}/stock/constitution/json',
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",				
				dataType: "json",
				beforeSend: function(){
				     $("#loading").show();
				},
				complete: function(){
				     $("#loading").hide();
				},
				data: {u_cd : input_index_name, dt_univ : input_dt_univ, dt_prc : input_dt_prc, dt_stk : input_dt_stk, prc_type : input_prc_type, univ_type : input_univ_type},				
				success: function(data){					
					// 지수정보 작업
					var info = '';
					info = info + '지수코드 : ' + data.정보.지수코드 + '<br/>';
					info = info + '지수이름 : ' + data.정보.지수이름 + '<br/>';
					info = info + '종목개수 : ' + data.정보.종목개수 + '개<br/>';
					info = info + '주가구분 : ' + data.정보.주가구분 + '<br/>';
					info = info + '유니브기준일자 : ' + data.정보.유니브기준일자 + '<br/>';
					info = info + '주가기준일자 : ' + data.정보.주가기준일자;
					$('#result1_info').html(info);
										
					// 테이블 작업
					var size = data.종목.length;
					var div = $('#result1');
					
					var html = '';
					if(input_univ_type == 'FNI_STYLE_UNIV'){
						
					}
					else if(input_univ_type == 'FNI_MFI_U_MAP_HIST'){
						
					}
					
					var html = data.헤더;
					
					for(i=0; i<size; i++){
						html = html + '<tr>';						
						html = html + '<td>' + data.종목[i].종목코드 + '</td>';
						html = html + '<td>' + data.종목[i].종목이름 + '</td>';
						html = html + '<td>' + data.종목[i].상장주식수 + '</td>';
						html = html + '<td>' + data.종목[i].상장예정주식수 + '</td>';
						html = html + '<td>' + data.종목[i].유동비율 + '</td>';
						
						if(input_univ_type == 'FNI_STYLE_UNIV'){
							html = html + '<td>' + data.종목[i].IIF + '</td>';
						}
						
						html = html + '<td align="right">' + data.종목[i].지수채용주식수 + '</td>';
						html = html + '<td align="right">' + data.종목[i].지수채용시가총액 + '</td>';
						html = html + '<td align="right">' + data.종목[i].주가 + '</td>';
						html = html + '<td align="right">' + data.종목[i].비중 + '</td>';
						html = html + '</tr>';
					}										
					div.html(html);
					initialize_copy_module();
					$('#contents').show('slide', {direction:'up'}, 1000);
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown) {	            	  
	              alert("Status: " + textStatus); alert("Error: " + errorThrown); 
	            }
			});
		}
	</script>
    

    
    