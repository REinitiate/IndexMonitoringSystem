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
            <h1 class="page-header">Information</h1>
        </div>
        <!-- /.col-lg-12 -->
    </div>
    
    <div class="row">
        <div class="col-md-12 col-lg-11">
	        <div class="panel panel-default">
		        <div class="panel-heading">
		            <i class="fa fa-bar-chart-o fa-fw"></i> ${idx_nm} 시계열                            
		        </div>
		        <div id="chart" class="panel-body">
		        </div>                	
	        </div>
    	</div>
    </div>
    
    
    <div class="row">
          <div class="col-md-12 col-lg-11">		
				<div class="panel panel-default">
			        <div class="panel-heading">
			            <i class="fa fa-bar-chart-o fa-fw"></i> ${idx_nm} 보유 종목 (in <small>${dt} 기준 VA_BND_IDX_ITEM_HIST</small>)                            
			        </div>                        
			        <div class="panel-body">
			            <table class="table table-striped table-bordered">
						    <thead>
						    	<th>채권이름</th>
						    	<th>채권코드</th>
						    	<th>비중</th>
						    	<th>YIELD</th>
						    	<th>평가가격</th>
						    	<th>NAV평가가격</th>
						    	<th>만기일</th>
						    	<th>이자지급일</th>
						    	<th>지급이자</th>
						    </thead>
						    
						    <tbody>    	
						    	<c:forEach items="${outItemHist}" var="map">
						    		<c:choose>
						    			<c:when test="${map['경고'].equals('경고')}">
						    				<tr class="danger">
						    			</c:when>
						    			<c:otherwise>
						    				<tr>
						    			</c:otherwise>
						    		</c:choose>						    								    								    		
						    	
						    		<c:if test="">			    		
						    		</c:if>    		
						    		<td>${map['채권이름']}</td>    		
						    		<td>${map['채권코드']}</td>
						    		<td class="text-right"><fmt:formatNumber value="${map['비중']}" pattern="#.00"/></td>
						    		<td class="text-right"><fmt:formatNumber value="${map['YIELD']}" pattern="#.00"/></td>
						    		<td class="text-right"><fmt:formatNumber value="${map['평가가격']}" pattern="#.0000"/></td>    		
						    		<td class="text-right"><fmt:formatNumber value="${map['NAV평가가격']}" pattern="#.0000"/></td>
						    		<td class="text-right">${map['만기일']}</td>
						    		<td clas0s="text-right">${map['최근이자지급일']}</td>
						    		<td class="text-right"><fmt:formatNumber value="${map['이자']}" pattern="#.00"/></td>						    		
						    	</tr>    		    		
						    	</c:forEach>
						    </tbody>			    
					    </table>
			        </div>                        
			    </div>
		    </div>
	    	<div class="col-md-12">		    
			    <h5>모니터링 주안점</h5>
			    <ul>
			    	<li>1) 이자락 날짜 체크</li>
			    </ul>
		    </div>		    
	    </div>
	    
	    ${outBndTimeSeries}
	    
  <script type="text/javascript">
  
  	var seriesOptions = [];
  	seriesOptions[0] = {
  			name : '총수익 지수',
  			data : ${timeseries1}  			
  	};
  	
  	seriesOptions[1] = {
	 		name : '시장 지수',	 		
	 		data : ${timeseries2},
	 		yAxis : 1
  	};
  
  	$(function(){
  		
  		
  		$("#chart").highcharts(
  				'StockChart', {
  					chart:{  						
  					},
  					title:{
  						text : '${idx_nm}'
  					},
  					yAxis:[
						{title: {
						 text: '총수익 지수'
						 },  					    
						 offset: 0
					}
  					 ,{
  						title: {
  					        text: '시장 지수'
  					    },  					    
  					    offset: 0,  					    
  					    opposite: true
  					}],
  					series : seriesOptions
  				});
  				
  	});
  	
  	
  </script>