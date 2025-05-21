<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>エラー</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f4f7f6;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            justify-content: center;
            align-items: center;
        }
        header {
            background: linear-gradient(to right, #dfefff, #eef5ff);
            padding: 20px;
            border-bottom: 1px solid #ccc;
            width: 100%;
            position: fixed;
            top: 0;
            left: 0;
            z-index: 1000;
            box-sizing: border-box;
        }
        header h1 {
            margin: 0;
            font-size: 24px;
            text-align: center;
        }
        .error-container {
            background-color: #ffffff;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 40px;
            text-align: center;
            max-width: 600px;
            width: 90%;
            margin-top: 100px;
            margin-bottom: 50px;
            box-sizing: border-box;
        }
        .error-container h2 {
            color: #dc3545;
            margin-bottom: 20px;
            font-size: 28px;
        }
        .error-container p {
            font-size: 18px;
            color: #555;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .return-link {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 25px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        .return-link:hover {
            background-color: #0056b3;
        }
        .footer {
            text-align: center;
            font-size: 12px;
            color: #333;
            padding: 20px;
            border-top: 1px solid #ccc;
            background-color: #f0f0f0;
            width: 100%;
            position: fixed;
            bottom: 0;
            left: 0;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
    </header>

    <div class="error-container">
        <h2>エラーが発生しました</h2>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "不明なエラーが発生しました。システム管理者にお問い合わせください。";
            }
        %>
        <p><%= errorMessage %></p>
        <%
            // ここでリンク先を修正
            String backLink = request.getContextPath() + "/scoremanager/login.jsp";
        %>
        <a href="<%= backLink %>" class="return-link">ログイン画面に戻る</a>
    </div>

    <div class="footer">
        © 2025 TIC<br>大原学園
    </div>
</body>
</html>