<%@page import="org.json.simple.JSONObject"%>
<%@page import="Admin.Area.PetrolService"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="application/json; charset=UTF-8"
	pageEncoding="UTF-8" info=""%>
<%
request.setCharacterEncoding("UTF-8");
%>
<jsp:useBean id="petDTO" class="Admin.Area.PetrolDTO" scope="page" />
<%
boolean flag = false;
String msg = "";

try {
    String[] nums = request.getParameterValues("chk");
    if (nums != null && nums.length > 0) {
        List<Integer> petNumList = new ArrayList<>();
        for (String numStr : nums) {
        	petNumList.add(Integer.parseInt(numStr));
        } //end for

        PetrolService petService = new PetrolService();
        flag = petService.removePetrol(petNumList);

        if (!flag) {
            throw new Exception("삭제 실패: 일부 또는 전체 삭제 실패");
        } //end if
    } //end if
} catch(Exception e) {
    msg = "예외 발생: " + e.getMessage();
} //end try catch

JSONObject json = new JSONObject();
json.put("success", flag);
json.put("message", msg);

out.print(json.toJSONString());
%>