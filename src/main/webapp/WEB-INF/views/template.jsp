<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    
<%
	String contentsType = (String)request.getAttribute("type"); // url, str
	String contents = (String)request.getAttribute("contents"); // 위 타입이 url 일 경우에는 위와 같겠다.
%>
<!DOCTYPE html>
<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	
    <title>FnIndex Monitoring System 1.0 Demo</title>

    <!-- Core CSS - Include with every page -->
    <!--  <link href="resources/css/bootstrap.min.css" rel="stylesheet"> -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <link href="${pageContext.request.contextPath}/resources/font-awesome/css/font-awesome.css" rel="stylesheet">

    <!-- Page-Level Plugin CSS - Dashboard -->
    <link href="${pageContext.request.contextPath}/resources/css/plugins/morris/morris-0.4.3.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/plugins/timeline/timeline.css" rel="stylesheet">

    <!-- SB Admin CSS - Include with every page -->
    <link href="${pageContext.request.contextPath}/resources/css/sb-admin.css" rel="stylesheet">    
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.js"></script>    
    <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>            
    
    <script>

	if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
		  var msViewportStyle = document.createElement("style")
		  msViewportStyle.appendChild(
		    document.createTextNode(
		      "@-ms-viewport{width:auto!important}" )
		  )
		  document.getElementsByTagName("head")[0].appendChild(msViewportStyle)
		}
	
	</script>
</head>
<body>	

    <div id="wrapper">
        <jsp:include page="nav/nav_top.jsp"/>    <!-- 상단 탑 메뉴 -->
        <jsp:include page="nav/nav_right.jsp"/>  <!--오른쪽 메뉴 -->
        <jsp:include page="nav/nav_left.jsp"/>   <!-- 오른쪽 메뉴 -->
        
        <div id="page-wrapper">
        <%if(contentsType != null){%>                
	        <%if(contentsType.compareTo("url") == 0){ %>        
	            <jsp:include page="<%=contents%>"/>
	        <%}else if(contentsType.compareTo("str") == 0){ %>
	            <%=contents%>
	        <%}%>
         <%} else{ %>
         	<img src='${pageContext.request.contextPath}/resources/img/security_camera.jpg'/>
         <%} %>                  
        </div>
        <!-- /#page-wrapper -->
    </div>
    <!-- /#wrapper -->

    <!-- Core Scripts - Include with every page -->    
    
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/plugins/metisMenu/jquery.metisMenu.js"></script>

    <!-- Page-Level Plugin Scripts - Dashboard -->
    <script src="${pageContext.request.contextPath}/resources/js/plugins/morris/raphael-2.1.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/plugins/morris/morris.js"></script>

    <!-- SB Admin Scripts - Include with every page -->
    <script src="${pageContext.request.contextPath}/resources/js/sb-admin.js"></script>

    <!-- Page-Level Demo Scripts - Dashboard - Use for reference -->
    <!-- <script src="/js/demo/dashboard-demo.js"></script>-->
    
    <script src="${pageContext.request.contextPath}/resources/js/highstock.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modules/exporting.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modules/bootstrap3-typeahead.js"></script>
    
    
