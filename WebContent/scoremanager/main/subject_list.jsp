<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目一覧</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; }
        header { background: linear-gradient(to right, #dfefff, #eef5ff); padding: 20px; border-bottom: 1px solid #ccc; display: flex; justify-content: space-between; align-items: center; }
        header h1 { margin: 0; font-size: 24px; }
        header .userinfo { font-size: 14px; }
        .container { display: flex; }
        .menu-left { width: 200px; background-color: #f9f9f9; padding: 20px; border-right: 1px solid #ccc; }
        .menu-left a { display: block; color: #0033cc; text-decoration: none; margin-bottom: 10px; font-size: 14px; }
        .content { flex-grow: 1; padding: 30px; }
        .form-header { background-color: #f0f0f0; padding: 10px; font-size: 18px; font-weight: bold; margin-bottom: 20px; border-left: 5px solid #333; }
        table { border-collapse: collapse; width: 100%; }
        th, td { padding: 8px 5px; text-align: left; border-bottom: 1px solid #eee; }
        th { border-bottom: 2px solid #ddd; }
        .actions { white-space: nowrap; }
        .actions a { margin-right: 5px; }
        .button { display: inline-block; padding: 5px 10px; border: none; border-radius: 5px; color: #007bff; text-decoration: none; font-size: small; background-color: white; }
        .footer { text-align: center; font-size: 12px; color: #666; padding: 20px; border-top: 1px solid #ccc; }
    </style>
</head>
<body>

<header>
    <h1>得点管理システム</h1>
    <div class="userinfo">大原 太郎さん　<a href="<%= request.getContextPath() %>/login/logout.jsp">ログアウト</a></div>
</header>

<div class="container">
    <nav class="menu-left">
        <a href="<%= request.getContextPath() %>/login/menu.jsp">メニュー</a>
        <a href="<%= request.getContextPath() %>/KensakuServlet">学生管理</a>
        <h4>成績管理</h4>
        <a href="#">成績登録</a>
        <a href="#">成績参照</a>
        <a href="<%= request.getContextPath() %>/SubjectList">科目管理</a>
    </nav>

    <div class="content">
        <div class="form-header">科目管理</div>
        <p style="text-align: right;">
            <a href="<%= request.getContextPath() %>/scoremanager/subject_create.jsp" class="button">新規登録</a>
        </p>

        <table>
            <thead>
            <tr>
                <th>科目コード</th>
                <th>科目名</th>
                <th style="width: 150px;">操作</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Map<String, String>> subjectList = (List<Map<String, String>>) request.getAttribute("subjectList");
                if (subjectList != null) {
                    for (Map<String, String> subject : subjectList) {
                        String cd = subject.get("CD");
                        String name = subject.get("NAME");
            %>
            <tr>
                <td><%= cd %></td>
                <td><%= name %></td>
                <td class="actions">
                    <a href="<%= request.getContextPath() %>/SubjectDeleteConfirm?cd=<%= cd %>" class="button">削除</a>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="3">表示するデータがありません。</td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>

<div class="footer">© 2023 TIC<br>大原学園</div>

</body>
</html>
