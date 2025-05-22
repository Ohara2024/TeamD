<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>登録・変更成功</title>
    <style>
        body { font-family: Arial, sans-serif; }
        h1 { text-align: center; color: green; }
        p { text-align: center; margin-top: 20px; }
        a { color: #007bff; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>登録・変更が完了しました。</h1>
    <p>新しい科目の登録・変更が完了しました。</p>
    <p><a href="<%= request.getContextPath() %>/scoremanager/main/subject_create.jsp">続けて登録・変更する</a>
     | <a href="<%= request.getContextPath() %>/main/SubjectListAction">科目管理</a></p>
</body>
</html>