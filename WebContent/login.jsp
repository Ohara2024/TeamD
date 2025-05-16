<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログイン</title>
</head>
<body>
<h1>ログインページ</h1>
<p>ここにログインフォームが表示されます。</p>
<c:if test="${not empty requestScope.errorMessage}">
<p style="color: red;">${requestScope.errorMessage}</p>
</c:if>
</body>
</html>