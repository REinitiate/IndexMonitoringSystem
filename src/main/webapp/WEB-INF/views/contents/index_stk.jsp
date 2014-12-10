<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	
<style>
	th{
		text-align: center;
	}
</style>
	         
<div class="row">
    <div class="col-lg-12">
        <h1 class="page-header">지수채용주식수 변경종목</h1>
    </div>
    <!-- /.col-lg-12 -->    
    <div class="col-md-12 col-lg-11">		
				<div class="panel panel-default">
			        <div class="panel-heading">
			            <i class="fa fa-bar-chart-o fa-fw"></i> ${dt} 기준 이벤트 발생 종목                         
			        </div>         
			        <div class="panel-body">
				    	<table class="table table-striped table-bordered">    	
						   	<tbody>
						   		<tr>
						   			<th>종목코드</th>
						   			<th>종목이름</th>
						   			<th>지수코드</th>
						   			<th>업종</th>
						   			<th>이벤트(신주상장)</th>
						   			<th>주식수</th>
						   			<th>이벤트(권리락)</th>
						   			<th>주식수</th>
						   		</tr>
							   	<c:forEach items="${events}" var="map">
						    		<c:choose>						    			
						    			<c:when test="${map['만기경고'].equals('경고')}"><tr class="danger"></c:when>
						    			<c:otherwise>
						    				<c:choose>
						    					<c:when test="${map['이자경고'].equals('경고')}"><tr class="info"></c:when>
								    			<c:otherwise>	
								    				<c:choose>
									    				<c:when test="${map['편입여부'].equals('편출')}"><tr class="warning"></c:when>
									    				<c:otherwise>
									    					<tr>
									    				</c:otherwise>
								    				</c:choose>								    				
								    			</c:otherwise>
						    				</c:choose>
						    			</c:otherwise>
						    		</c:choose>
						    		
							    	<td>${map['GICODE']}</td>    		
							    	<td>${map['ITEMABBRNM']}</td>
							    		<td class="text-right">${map['U_CD']}</td>
							    		<td class="text-right">${map['U_NM']}</td>
							    		<td class="text-right">${map['CD_NM1']}</td>
							    		<td class="text-right"><fmt:formatNumber value="${map['STK1']}" type="number" maxFractionDigits="3"/></td>    		
							    		<td class="text-right">${map['CD_NM2']}</td>
							    		<td class="text-right"><fmt:formatNumber value="${map['STK2']}" type="number" maxFractionDigits="3"/></td>						    		
							    	</tr>    		    		
								</c:forEach>
						   	</tbody>
				    	</table>
				    </div>
			</div>
    </div>
</div>            
<!-- /.row -->        
<!-- /#page-wrapper -->
