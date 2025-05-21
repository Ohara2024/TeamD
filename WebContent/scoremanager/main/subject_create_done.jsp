<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>登録成功</title>
    <style>
        body { font-family: Arial, sans-serif; }
        h1 { text-align: center; color: green; }
        p { text-align: center; margin-top: 20px; }
        a { color: #007bff; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>登録が完了しました。</h1>
    <p>新しい科目の登録が完了しました。</p>
    <p><a href="scoremanager/main/subject.jsp">続けて登録する</a> | <a href="<%= request.getContextPath() %>/itiran/hyoji.jsp">科目一覧に戻る</a></p>
</body>
</html>