<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>メニュー</title>
    <style>
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
        .logout-link {
            margin-top: 30px;
        }
        .logout-link a {
            text-decoration: none;
            color: #d32f2f; /* 少し落ち着いた赤 */
            padding: 8px 15px;
            border: 1px solid #d32f2f;
            border-radius: 3px;
            background-color: #ffebee; /* 薄いピンク */
            transition: background-color 0.3s ease;
        }
        .logout-link a:hover {
            background-color: #f4cdd0;
        }
    </style>
</head>
<body>
<header>
        <div class="user-info"><%= session.getAttribute("username") %> さん</div>
        <a href="logout.jsp" class="logout-link">ログアウト</a>
    </header>

    <h1>メニュー</h1>
    <a href="<%= request.getContextPath() %>/gakusei/student_list.jsp" class="menu-item">学生管理</a>
    <a href="<%= request.getContextPath() %>/seiseki/score_list.jsp" class="menu-item">成績管理</a>
    <a href="<%= request.getContextPath() %>/itiran/hyoji.jsp" class="menu-item">科目管理</a>

</body>
</html>