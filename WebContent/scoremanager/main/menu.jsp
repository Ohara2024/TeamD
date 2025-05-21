<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="bean.Teacher" %>
<!DOCTYPE html>
<html>
<head>
    <title>メニュー</title>
    <style>
        body {
            text-align: center;
            background-color: #f8f8f8;
            font-family: "游ゴシック体", YuGothic, "ヒラギノ角ゴ ProN W3", Meiryo, sans-serif;
        }
        header {
            display: flex;
            justify-content: flex-end; /* 要素を右端に配置 */
            align-items: center;
            padding: 15px 30px;
            background-color: #fff;
            border-bottom: 1px solid #eee;
        }
        h1 {
            color: #333;
            margin-top: 50px;
            margin-bottom: 30px;
        }
        .menu-item {
            display: block;
            margin: 15px auto;
            padding: 12px 25px;
            width: 200px;
            background-color: #fff;
            border: 1px solid #bbb;
            text-decoration: none;
            color: #333;
            border-radius: 3px;
            transition: background-color 0.3s ease, color 0.3s ease;
        }
        .menu-item:hover {
            background-color: #555;
            color: #fff;
        }
        .user-info {
            margin-right: auto; /* ユーザー情報を左に押し出す */
        }
        .logout-link {
            text-decoration: none;
            color: #d32f2f;
            padding: 8px 15px;
            border: 1px solid #d32f2f;
            border-radius: 3px;
            background-color: #ffebee;
            transition: background-color 0.3s ease, color 0.3s ease;
            margin-left: 20px;
        }
        .logout-link:hover {
            background-color: #d32f2f;
            color: #fff;
        }
    </style>
</head>
<body>
<header>
    <%
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        String teacherName = "ゲスト";
        if (teacher != null && teacher.getName() != null && !teacher.getName().isEmpty()) {
            teacherName = teacher.getName();
        }
    %>
    <div class="user-info"><%= teacherName %> さん</div>
    <a href="<%= request.getContextPath() %>/logout.jsp" class="logout-link">ログアウト</a>
</header>

    <h1>メニュー</h1>
    <a href="<%= request.getContextPath() %>/main/StudentList.action" class="menu-item">学生管理</a>  <%-- ★パス変更 --%>
    <a href="<%= request.getContextPath() %>/main/TestList.action" class="menu-item">成績参照</a>
    <a href="<%= request.getContextPath() %>/main/SubjectList.action" class="menu-item">科目管理</a> <%-- ★パス変更 --%>

</body>
</html>