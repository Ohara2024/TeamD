<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目登録</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
        header { background-color: #ddd; color: #333; padding: 20px; text-align: left; display: flex; justify-content: space-between; align-items: center; }
        header h1 { margin: 0; color: #333; }
        header .userinfo { color: #777; }
        header .userinfo a { color: #007bff; text-decoration: none; margin-left: 10px; }
        header .userinfo a:hover { text-decoration: underline; }
        .container { display: flex; max-width: 960px; margin: 20px auto; background-color: white; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); border-radius: 5px; overflow: hidden; }
        .menu-left { width: 200px; background-color: #f8f8f8; padding: 20px; border-right: 1px solid #eee; }
        .menu-left a { display: block; padding: 10px 0; color: #333; text-decoration: none; border-bottom: 1px solid #eee; }
        .menu-left a:hover { background-color: #eee; }
        .menu-left h4 { margin-top: 20px; color: #777; }
        .content { flex: 1; padding: 30px; }
        h1 { text-align: left; color: #333; margin-bottom: 20px; }
        form { width: 100%; max-width: 400px; margin-top: 20px; padding: 20px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9; }
        label { display: block; margin-bottom: 8px; font-weight: bold; color: #555; }
        input[type="text"] { width: calc(100% - 16px); padding: 10px; margin-bottom: 20px; border: 1px solid #ccc; border-radius: 3px; box-sizing: border-box; font-size: 16px; }
        button[type="submit"] { background-color: #007bff; color: white; padding: 12px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
        button[type="submit"]:hover { background-color: #0056b3; }
        .button-container { margin-top: 20px; text-align: left; }
        .button { display: inline-block; background-color: #28a745; color: white; padding: 10px 15px; border: none; border-radius: 5px; text-decoration: none; font-size: 16px; }
        .button:hover { background-color: #1e7e34; }
        .error { color: red; margin-top: 10px; }
        .footer { background-color: #ddd; color: #333; text-align: center; padding: 10px; position: fixed; bottom: 0; width: 100%; }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
        <div class="userinfo">
            <%= session.getAttribute("username") != null ? session.getAttribute("username") + " さん" : "null さん" %>
            <a href="login/logout.jsp">ログアウト</a>
        </div>
    </header>
    <div class="container">
        <nav class="menu-left">
            <a href="<%= request.getContextPath() %>/login/menu.jsp">メニュー</a>
            <a href="<%= request.getContextPath() %>/gakusei/student_list.jsp">学生管理</a>
            <h4>成績管理</h4>
            <a href="<%= request.getContextPath() %>/seiseki/score_register.jsp">成績登録</a>
            <a href="<%= request.getContextPath() %>/seiseki/score_list.jsp">成績参照</a>
            <a href="<%= request.getContextPath() %>/itiran/hyoji.jsp">科目管理</a>
        </nav>
        <div class="content">
            <h1>科目情報登録</h1>
            <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="error"><%= request.getAttribute("errorMessage") %></p>
            <% } %>
            <form action="<%= request.getContextPath() %>/subject/SubjectRegisterServlet" method="post">
                <div>
                    <label for="subjectId">科目ID:</label>
                    <input type="text" id="subjectId" name="subjectId" placeholder="科目コードを入力してください" required>
                </div>
                <div>
                    <label for="subjectName">科目名:</label>
                    <input type="text" id="subjectName" name="subjectName" placeholder="科目名を入力してください" required>
                </div>
                <button type="submit">登録</button>
            </form>
        </div>
    </div>
    <div class="footer">© 2023 TIC<br>大原学園</div>
</body>
</html>