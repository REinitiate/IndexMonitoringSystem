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
	
	<div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">지수 구성 정보</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    
    <div style="margin-bottom: 10px">
	    <input type="button" value="REFRESH" onclick="refresh_data();"/>
    </div>
    
    <table class="table">    
    <tr>
	    <td><label for="index_search">지수 선택 : </label></td>
	    <td><input id="index_search" type="text" class="typeahead" style='width: 300px'></td>
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
    	<td colspan="2"><label>가격 기준 : </label>
		<div class="btn-group" style="margin-bottom: 10px">		
			<button type="button" class="btn btn-default">종가기준</button>
	  		<button type="button" class="btn btn-default">기준가기준</button>
		</div></td>
    </tr>
    </table>
	
	<div class="row">
          <div class="col-md-12 col-lg-11">		
				<div class="panel panel-default">
			        <div class="panel-heading">
			        	보유종목 결과		                                        
			        </div>                        
			        <div id="constitution_result" class="panel-body">
			            <table id="result1" class="table table-striped table-bordered">
			            	<thead>
			            		<th>종목코드</th>
			            		<th>종목이름</th>
			            		<th>지수채용주식수</th>
			            		<th>지수채용시총</th>
			            		<th>주가</th>
			            		<th>비중</th>
			            	</thead>
			            </table>
				    </div>
			    </div>
		   </div>
    </div>
		
	<div id='result_table' class="panel-body"></div>
	
	<script>
		$(function(){
			
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
        	$('#dt_univ').datepicker(
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
        	$('#dt_prc').datepicker(
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
        });
		
		function refresh_data()
		{
			
			$('constitution_result').hide();
			
			var input_dt_univ = $('#dt_univ').val();			
			var input_dt_prc = $('#dt_prc').val();
			var input_index_name = $('#index_search').val().split('(')[0];
			
			
			alert('dt_univ : '+ input_dt_univ);
			alert('dt_prc : '+ input_dt_prc);
			alert('index_name : ' + input_index_name);
			
			$.ajax({
				type: 'post',
				//url: '${pageContext.request.contextPath}/stock/constitution/json?u_cd=FI00.WLT.LVL&dt_univ=20140708&dt_prc=20140708&prc_type=cls_prc',
				url: '${pageContext.request.contextPath}/stock/constitution/json',
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",				
				dataType: "json",
				data: {u_cd : input_index_name, dt_univ : input_dt_univ, dt_prc : input_dt_prc, prc_type : 'cls_prc'},
				success: function(data){					
					//var size = $('#result_table').append(data.종목.length);
					var size = data.종목.length;
					var div = $('#result1');
					var html = '<thead><th>종목코드</th><th>종목이름</th><th>지수채용주식수</th><th>지수채용시총</th><th>주가</th><th>비중</th></thead>';
					for(i=0; i<size; i++){
						html = html + '<tr>';						
						html = html + '<td>' + data.종목[i].종목코드 + '</td>';
						html = html + '<td>' + data.종목[i].종목이름 + '</td>';
						html = html + '<td align="right">' + data.종목[i].지수채용주식수 + '</td>';
						html = html + '<td align="right">' + data.종목[i].지수채용시가총액 + '</td>';
						html = html + '<td align="right">' + data.종목[i].주가 + '</td>';
						html = html + '<td align="right">' + data.종목[i].비중 + '</td>';
						html = html + '</tr>';
					}										
					div.html(html);
					$('constitution_result').show(5000);
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown) {	            	  
	              alert("Status: " + textStatus); alert("Error: " + errorThrown); 
	            }
			});
		}
	</script>
	