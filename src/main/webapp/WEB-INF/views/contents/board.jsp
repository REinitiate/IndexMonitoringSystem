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
        <h1 class="page-header">게시판</h1>
    </div>
    <!-- /.col-lg-12 -->    
    <div class="col-md-12 col-lg-11">
    
    					
			<div class="panel panel-default">			        
		        <div class="panel-heading">
		            <i class="fa fa-bar-chart-o fa-fw"></i> 개편종목 및 이벤트처리 리스트 <button class="btn btn-default" onclick="$('#new').toggle(1000);">신규</button>                       
		        </div>
		        
		        <div id="new" class="panel-body" style="display: none;">
					<div class="input-group">
						<input type="text" class="form-control" placeholder="등록일자" style="width: 100px;"/>
			   			<input type="text" class="form-control" placeholder="작업일자" style="width: 100px;"/>
			   			<input type="text" class="form-control" placeholder="등록자" style="width: 100px;"/>
			   			<input type="text" class="form-control" placeholder="작업자" style="width: 100px;"/>
						<div class="btn-group">
						  <button id="btn_work_choice" class="btn btn-default btn-md dropdown-toggle" type="button" data-toggle="dropdown" style="width: 100px;">
						    작업선택 <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						  		<li><a href="#" onclick="$('#btn_work_choice').html($(this).html());">합병</a></li>
							    <li><a href="#" onclick="$('#btn_work_choice').html($(this).html());">분할</a></li>
							    <li><a href="#" onclick="$('#btn_work_choice').html($(this).html());">합병분할</a></li>										    
							    <li class="divider"></li>
							    <li><a href="#" onclick="$('#btn_work_choice').html($(this).html());">정기개편</a></li>
							    <li class="divider"></li>										    								    
						  </ul>						  
						</div>
					</div>
					   			   		
   					<textarea rows="" cols="" style="width: 100%"></textarea>
					
	   				<button class="btn btn-success">등록</button>
	   				<button class="btn btn-failure">초기화</button>
	   				
	   			</div>
		        
		        <div class="panel-body">
				    <table class="table table-striped table-bordered">    	
						   	<tbody>
						   		
						   		<tr>
						   			<th>등록일자</th>
						   			<th>작업일자</th>
						   			<th>작업구분</th>						   			
						   			<th>내용</th>
						   			<th>등록자</th>
						   			<th>작업자</th>
						   			<th></th>
						   		</tr>
						   		
							   	<%-- <c:forEach items="${events}" var="map">						    				    		
								</c:forEach> --%>
						   	</tbody>
				    </table>
			    </div>
			</div>
	</div>
</div>

<script type="text/javascript">

        $(function(){        	
        	$('#datepicker2').datepicker(
        				{
	        				dateFormat: 'yymmdd'	        				
        				}
        			);
        	$('#datepicker3').datepicker(
    				{
        				dateFormat: 'yymmdd'	        				
    				}
    			);
        });
        
</script>
                    
<!-- /.row -->        
<!-- /#page-wrapper -->
