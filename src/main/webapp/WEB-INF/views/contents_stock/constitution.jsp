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
    
    <div style="margin-bottom: 30px">
	    <label for="index_search">지수 선택 : </label>
	    <input id="index_search" type="text" class="typeahead" style='width: 400px'>
	</div>
          
   	<div class="btn-group">      
	  <button type="button" class="btn btn-default">FNI_STYLE_UNIV</button>
	  <button type="button" class="btn btn-default">RES_STYLE_UNIV</button>
	  <button type="button" class="btn btn-default">RES_J_CAP_HIST</button>
	  <button type="button" class="btn btn-default">FNI_MFI_U_MAP_HIST</button>
	</div>
	
	<div id='test'></div>
	
	<script>
		$(function(){
			
			
			$.ajax({
				  url: '/monitoring/stock/ucdlist',
				  contentType: "application/x-www-form-urlencoded; charset=UTF-8",
				  dataType: "json",
				  success: function(data){
					  $('.typeahead').typeahead(data);  
	              },
	              error: function(XMLHttpRequest, textStatus, errorThrown) {	            	  
	                  alert("Status: " + textStatus); alert("Error: " + errorThrown); 
	               }
				})
		});
	</script>