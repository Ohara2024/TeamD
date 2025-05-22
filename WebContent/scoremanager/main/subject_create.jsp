<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>科目登録</title>
<style>

        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }

        header { background-color: #ddd; padding: 20px; text-align: left; display: flex; justify-content: space-between; align-items: center; }

        .container { display: flex; max-width: 960px; margin: 20px auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1); border-radius: 5px; overflow: hidden; }

        .menu-left { width: 200px; background-color: #f8f8f8; padding: 20px; border-right: 1px solid #eee; }

        .menu-left a { display: block; padding: 10px 0; text-decoration: none; color: #333; border-bottom: 1px solid #eee; }

        .content { flex: 1; padding: 30px; }

        form { max-width: 400px; background: #f9f9f9; padding: 20px; border-radius: 5px; border: 1px solid #ddd; }

        label { font-weight: bold; display: block; margin-top: 15px; }

        input[type="text"] { width: 100%; padding: 8px; margin-top: 5px; border: 1px solid #ccc; border-radius: 4px; }

        button { margin-top: 20px; padding: 10px 20px; background: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; }

        button:hover { background: #0056b3; }

        .footer { background: #ddd; padding: 10px; text-align: center; margin-top: 20px; }

        .error { color: red; }
</style>
</head>
<body>
<header>
<h1>得点管理システム</h1>
<div>
<%

                String username = (String) session.getAttribute("username");

                if (username == null || username.trim().isEmpty()) {

                    username = "ゲスト";

                }

            %>
<%= username %> さん
<a href="<%= request.getContextPath() %>/login/logout.jsp">ログアウト</a>
</div>
</header>
<div class="container">
<div class="menu-left">
<a href="<%= request.getContextPath() %>/scoremanager/main/menu.jsp">メニュー</a>
            <a href="<%= request.getContextPath() %>/StudentListAction">学生管理</a>
            <h4>成績管理</h4>
            <a href="<%= request.getContextPath() %>/main/TestRegist.action">成績登録</a>
            <a href="<%= request.getContextPath() %>/main/TestList.action">成績参照</a>
            <a href="<%= request.getContextPath() %>/main/SubjectListAction">科目管理</a>
</div>            
<div class="content">
<h1>科目情報登録</h1>
<% if (request.getAttribute("message") != null) { %>
<p class="error"><%= request.getAttribute("message") %></p>
<% } %>
 <form action="<%= request.getContextPath() %>/main/SubjectCreate.action" method="post">
<label for="cd">科目ID:</label>
<input type="text" id="cd" name="cd" required>
 
                <label for="name">科目名:</label>
<input type="text" id="name" name="name" required>
 
                <button type="submit">登録</button>
</form>
</div>
</div>
<div class="footer">© 2023 TIC<br>大原学園</div>
</body>
</html>

 