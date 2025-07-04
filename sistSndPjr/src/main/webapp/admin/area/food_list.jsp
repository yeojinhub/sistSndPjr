<%@page import="Admin.Area.FoodDTO"%>
<%@page import="java.util.List"%>
<%@page import="Admin.Area.FoodService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    info=""%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="../common/jsp/login_chk.jsp" %>
<%
String paramNum=request.getParameter("areaNum");

int num=0;
try{
	num=Integer.parseInt(paramNum);
} catch(NumberFormatException nfe) {
	nfe.printStackTrace();
} //end try catch

FoodService service = new FoodService();
List<FoodDTO> foodList = service.searchAllFood(num);
		
request.setAttribute("num", num);
request.setAttribute("foodList", foodList);
		
if (!foodList.isEmpty()) {
    request.setAttribute("areaName", foodList.get(0).getAreaName());
} //end if
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 대시보드</title>

<!-- 사용자 정의 css 로드 -->
<link rel="stylesheet" href="/sistSndPjr/admin/common/css/styles.css">

<!-- Font Awesome for icons -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<!-- jQuery 로드 -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- 사용자 정의 JS 로드 -->
<script src="/sistSndPjr/admin/script.js"></script>
<script src="/sistSndPjr/admin/common/js/food_manage.js"></script>

</head>
<body>
    <div class="container">
        <!-- Sidebar -->
        <jsp:include page="/admin/common/jsp/admin_sidebar.jsp" />

		<!-- Main Content -->
		<div class="main-content">
			<div class="header">
				<h1>먹거리 수정</h1>
			</div>

			<div class="content">
				<div>
					<div>
						<h1><c:out value="${ areaName }"></c:out> </h1>
					</div>
				</div>
				
				<table class="data-table">
					<thead>
						<tr>
							<th><input type="checkbox" class="label-checkbox" name="chkAll" id="chkAll" /></th>
							<th>번호</th>
							<th>음식명</th>
							<th>가격</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${ empty foodList }">
						<tr>
							<td colspan="4">먹거리 정보가 존재하지 않습니다.</td>
						</tr>
						</c:if>
						<c:forEach var="foodDTO" items="${ foodList }"  varStatus="i">
						<tr>
							<td><input type="checkbox" class="label-checkbox" name="chk" id="chk" value="${ foodDTO.foodNum }" /></td>
							<td><c:out value="${ i.count }" /></td>
							<td class="onclickbtn" onclick="location.href='food_detail.jsp?foodNum=${ foodDTO.foodNum }'">
								<c:out value="${ foodDTO.foodName }" />
							</td>
							<td class="onclickbtn" onclick="location.href='food_detail.jsp?foodNum=${ foodDTO.foodNum }'">
								<fmt:formatNumber type="number" value="${ foodDTO.foodPrice }" maxFractionDigits="3" />원
							</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>

				<div class="button-group-area-detail">
					<button class="btn btn-add" id="btnFoodAddFrm2" onclick="location.href='food_add_frm.jsp?areaNum=${ num }'">등록</button>
					<button class="btn btn-remove" id="btnFoodRemove">삭제</button>
					<button class="btn btn-back" id="btnFoodBack">뒤로</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
