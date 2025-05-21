<%-- logout.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    session.invalidate(); // セッションを無効化
    response.sendRedirect(request.getContextPath() + "/login/login.jsp?logout=true"); // ログイン画面へリダイレクト
%>
<!DOCTYPE html>
<html>
<head>
    <title>ログアウト</title>
</head>
<body>
    <h1>ログアウトしました。</h1>
    <p>ログイン画面へリダイレクトします。</p>
</body>
</html>