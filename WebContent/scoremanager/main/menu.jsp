<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="bean.Teacher" %> <%-- Teacherクラスをインポート --%>
<!DOCTYPE html>
<html>
<head>
    <title>メニュー</title>
    <style>
        /* ... スタイルは変更なし ... */
        body {
            text-align: center;
            background-color: #f8f8f8; /* 薄い灰色 */
            font-family: "游ゴシック体", YuGothic, "ヒラギノ角ゴ ProN W3", Meiryo, sans-serif; /* 少し和風な印象のフォント */
        }
        header {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            padding: 15px 30px;
            background-color: #fff;

        }
        h1 {
            color: #333;
            margin-bottom: 30px;
        }
        .menu-item {
            display: block;
            margin: 15px auto;
            padding: 12px 25px;
            width: 180px;
            background-color: #fff;
            border: 1px solid #bbb; /* 細いグレーの罫線 */
            text-decoration: none;
            color: #333;
            border-radius: 3px;
            transition: background-color 0.3s ease;
        }
        .menu-item:hover {
            background-color: #eee;
        }
        .logout-link { /* このクラスはヘッダー内のリンクに適用されていませんでした */
            text-decoration: none;
            color: #d32f2f;
            padding: 8px 15px;
            border: 1px solid #d32f2f;
            border-radius: 3px;
            background-color: #ffebee;
            transition: background-color 0.3s ease;
            margin-left: 20px; /* ユーザー情報との間隔 */
        }
        .logout-link:hover {
            background-color: #f4cdd0;
        }
        .user-info {
            /* 必要であればスタイルを追加 */
        }
    </style>
</head>
<body>
<header>
    <%
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        String teacherName = "ゲスト"; // デフォルト名
        if (teacher != null) {
            teacherName = teacher.getName(); // Teacherオブジェクトから名前を取得
        }
    %>
    <div class="user-info"><%= teacherName %> さん</div>
    <a href="logout.jsp" class="logout-link">ログアウト</a>
</header>

    <h1>メニュー</h1>
    <%-- リンク先は現状のままとしていますが、実際のファイル構成に合わせてください --%>
    <a href="<%= request.getContextPath() %>/gakusei/student_list.jsp" class="menu-item">学生管理</a>
    <a href="<%= request.getContextPath() %>/main/TestList.action" class="menu-item">成績参照</a> <%-- 成績参照機能へのリンクに変更 --%>
    <a href="<%= request.getContextPath() %>/path_to_subject_management/subject_list.jsp" class="menu-item">科目管理</a> <%-- 科目管理機能への実際のパスに変更してください --%>

</body>
</html>