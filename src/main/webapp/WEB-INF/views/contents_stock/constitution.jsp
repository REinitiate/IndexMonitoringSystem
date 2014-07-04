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
          
   	<div class="btn-group">      
	  <button type="button" class="btn btn-default">FNI_STYLE_UNIV</button>
	  <button type="button" class="btn btn-default">RES_STYLE_UNIV</button>
	  <button type="button" class="btn btn-default">RES_J_CAP_HIST</button>
	  <button type="button" class="btn btn-default">FNI_MFI_U_MAP_HIST</button>
	</div>
    
	
	    
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