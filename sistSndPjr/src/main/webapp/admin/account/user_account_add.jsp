<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    info=""%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>관리자 대시보드</title>
    <link rel="stylesheet" href="/sistSndPjr/admin/common/css/styles.css">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="/sistSndPjr/admin/script.js"></script>
</head>
<body>
    <div class="container">
        <!-- Sidebar -->
        <jsp:include page="/admin/common/jsp/admin_sidebar.jsp" />
         
        <!-- Main Content -->
        <div class="main-content">
            <div class="header">
                <h1>회원 계정 등록</h1>
            </div>
            
            <div class="content">
            	<div>
            		<table class="account-table account-content">
            			<tbody>
            				<tr>
            					<td>이름</td>
            					<td><input type="text" value="" readonly="readonly" /></td>
            				</tr>
            				<tr>
            					<td>이메일</td>
            					<td><input type="text" value="" /></td>
            				</tr>
            				<tr>
            					<td>비밀번호</td>
            					<td><input type="password" value="" /></td>
            				</tr>
            				<tr>
            					<td>전화번호</td>
            					<td><input type="text" value="" /></td>
            				</tr>
            				<tr>
            					<td>가입일</td>
            					<td><input type="text" value="2025-05-29" readonly="readonly" /></td>
            				</tr>
            			</tbody>
            		</table>
            	</div>
            </div>
            
            <div class="button-group">
            	<button class="btn btn-add">등록</button>
            	<button class="btn btn-back" onclick="location.href='user_accounts.jsp'">뒤로</button>
            </div>

        </div>
    </div>
</body>
</html>
