<%@page import="user.util.SMTP"%>
<%@page import="user.account.forgot.ForgotService"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" info=""%>
<% 
// 6-1. 비밀번호 재설정 남용 방지 플래그
for (Cookie coo : request.getCookies()) {
	if (coo.getName().equals("notAbuseEmailSend")) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("notAbuseEmailSend", true);
		%> 
		<%= jsonObj.toJSONString()%>
		<%
		return;
	}// end if
}// end for

// 1. ajax로 보낸 파라미터(이메일) 받기
String email = request.getParameter("email");


// 2. DB 'ACCOUNT' 테이블에 접근, 입력받은 이메일이 존재하는지 확인.
// 2-1. Service 객체 생성
ForgotService fs = new ForgotService();
// 2-2. searchEmailCheck 메소드 호출
boolean emailChk = fs.searchEmailCheck(email);


// 3. JSON 형태로 변수들을 보내기 위해 JSONObject 객체 생성
JSONObject jsonObj = new JSONObject();
jsonObj.put("emailChk", emailChk); // 'emailChk'변수 JSON 형태로 저장 ( { "emailChk" : false } )


// 4. 유효성 검사
// 4-1. 입력받은 이메일이 존재하지 않을 경우 emailChk만 JSON으로 보내고 얼리 리턴 (보낸곳에서 alert로 처리)
if (!emailChk) {
%> 
<%= jsonObj.toJSONString() %>
<%
	return;
}// end if

// 5. SMTP를 이용한 이메일 전송
// 5-1. 사용자에게 전송된 '비밀번호 재설정'링크 접속 유저와 사용중인 유저가 일치하는지 유효성 검사를 위한 sessionID 변수 설정
String sessionId = request.getSession().getId();
// 5-2. SMTP 객체 생성 및 이메일 전송
SMTP smtp = SMTP.getInstance();
boolean sendChk = smtp.sendChangePassMail(email, sessionId, "localhost"); //
// 5-3. JSON에 이메일 전송 성공 여부 넣기
jsonObj.put("sendChk", sendChk);

// 6. 잦은 비밀번호 재설정 사용을 방지하기 위한 10분짜리 쿠키 생성
Cookie cookie = null;
if (sendChk) { // 비밀번호 재설정 이메일이 정상적으로 전송됬을 경우에만 쿠키 생성.
	cookie = new Cookie("notAbuseEmailSend", "true");

	cookie.setMaxAge(60*1); // 1분
	cookie.setPath("/");
	response.addCookie(cookie);
}// end if
%>
<%= jsonObj.toJSONString() %>