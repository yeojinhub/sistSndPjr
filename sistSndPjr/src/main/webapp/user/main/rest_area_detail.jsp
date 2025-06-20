<%@page import="user.account.login.LoginDTO"%>
<%@page import="user.restarea.detail.AreaDetailDTO"%>
<%@page import="user.restarea.detail.RestAreaDetailService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" info=""%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String id = request.getParameter("id");

RestAreaDetailService rads = new RestAreaDetailService();

AreaDetailDTO adDTO = rads.searchRestAreaDetail(Integer.parseInt(id));

// 휴게소명, 방향 설정
String name = adDTO.getName();
String direction = adDTO.getDirection() == null ? "" : adDTO.getDirection();

// 로그인 세션 확인 (즐겨찾기 추가 버튼 활성화 유무)
boolean loginChk = session.getAttribute("userData") != null;

boolean favoriteChk = false;

// 로그인 체크 후 확인되면 DTO에 담기 (즐겨찾기 추가 버튼 사용 위해)
if (loginChk) {
	LoginDTO lDTO = (LoginDTO) session.getAttribute("userData");
	pageContext.setAttribute("lDTO", lDTO);
	favoriteChk = rads.searchFavorite(lDTO.getUser_email(), adDTO.getArea_num());
} // end if

// EL문 사용을 위한 Attribute 설정
pageContext.setAttribute("adDTO", adDTO);
pageContext.setAttribute("loginChk", loginChk);
pageContext.setAttribute("favoriteChk", favoriteChk);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>휴게소 편의시설 정보</title>
<jsp:include page="../common/jsp/external_file.jsp" />
<style>
</style>

<script>
$(function(){
	// 즐겨찾기 버튼
	var email = '${lDTO.user_email}';
	var id = '${adDTO.area_num}';
	var originalName = '${adDTO.originalName }';
	var accNum = '${lDTO.acc_num}';
	var param = { email : email , id : id , originalName : originalName , accNum : accNum };
	
    $('#btnFavoriteAdd').click(()=>{
		if (${loginChk}) {
			$.ajax({
				url:'ajax_rest_area_favorite.jsp',
				type:'post',
				data: param,
				dataType:'json',
				error:function(xhr) {
					console.log(xhr.status);
				},
				success:function(jsonObj) {					
					if (jsonObj.result) {
						alert('[' + originalName + '] 즐겨찾기 추가 하였습니다.');
						location.reload();
					};// end if
				}
			});
			return;
		};// end if
		
		if (confirm('로그인 후 이용가능합니다.\n로그인 하시겠습니까?')) {
			location.href = '../login/login.jsp';
		};// end if
    });// click
    
    $('#btnFavoriteRemove').click(()=>{
		$.ajax({
			url:'ajax_rest_area_remove_favorite.jsp',
			type:'post',
			data: param,
			dataType:'json',
			error:function(xhr) {
				console.log(xhr.status);
			},
			success:function(jsonObj) {
				if (jsonObj.result) {
					alert('즐겨찾기 해제 되었습니다.');
					location.reload();
				};// end if
			}
		});// ajax
    });// click
	
	// 페이지 최초 로딩 시
	$("#tabContent").load("../common/component/restarea/rest_area_info.jsp?id=${adDTO.area_num}");

    // 탭 클릭 시
    $(".tab-btn").click(function () {
      $(".tab-btn").removeClass("active");
      $(this).addClass("active");

      const tabName = $(this).data("tab");
      let file = "";
      if (tabName === "info") {
		file = "../common/component/restarea/rest_area_info.jsp?id=${adDTO.area_num}";
      }
      else if (tabName === "food") { 
		file = "../common/component/restarea/rest_area_food.jsp?id=${adDTO.area_num}";
      }
      else if (tabName === "review") {
		file = "../common/component/restarea/rest_area_review.jsp?id=${adDTO.area_num}";
      }

      $("#tabContent").load(file);
    });// ready    	 
});
</script>
</head>
<body>
	<header>
		<jsp:include page="../common/jsp/header.jsp" />
	</header>
	<div class="container"
		style="display: flex; flex-direction: column; height: auto; min-height: 100vh;">

		<div>
			<hr class="line_blue" style="text-align: center;" />
		</div>

		<div>
			<h2>
				<span style="font-size: 40px;">휴게소/영업소_</span> <span
					style="font-size: 30px;">휴게소</span>
			</h2>
		</div>

		<hr class="rest-title-divider">

		<div class="rest-title-row">
			<h2 class="rest-name"><%=name%>
				<%=direction%></h2>
			<c:choose>
				<c:when test="${favoriteChk }">
					<input type="button" class="favorite-btn" id="btnFavoriteRemove"
						style="background-color: #ff0000;" value="★ 즐겨찾기 해제" />
				</c:when>
				<c:otherwise>
					<input type="button" class="favorite-btn" id="btnFavoriteAdd"
						value="★ 즐겨찾기 추가" />
				</c:otherwise>
			</c:choose>

		</div>

		<div class="detail-tab">
			<button class="tab-btn active" data-tab="info">상세정보</button>
			<button class="tab-btn" data-tab="food">먹거리</button>
			<button class="tab-btn" data-tab="review">리뷰</button>
		</div>

		<div id="tabContent"></div>

		<div class="agent-container" style="margin-top: 20px;">
			<div class="agent-box">담당자 정보</div>
			<div class="agent-call">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;모두쉼
				콜센터 : 02-1234-5678</div>
		</div>
	</div>
	<!-- END div(container) -->

</body>
<script
	src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=bd811b0f31f210496827de3ea38119ae&autoload=false"></script>
<div class="footer" style="width: 100%;">
	<footer>
		<jsp:include page="../common/jsp/footer.jsp" />
	</footer>
</div>
</html>