<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目削除結果</title> <%-- タイトルを「科目削除結果」に変更 --%>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f4f7f6;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        header {
            background: linear-gradient(to right, #dfefff, #eef5ff);
            padding: 20px;
            border-bottom: 1px solid #ccc;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        header h1 {
            margin: 0;
            font-size: 24px;
        }
        .container {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 30px;
        }
        .message-box {
            background-color: #ffffff;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            padding: 40px;
            text-align: center;
            max-width: 500px;
            width: 100%;
        }
        /* メッセージのスタイルを調整 */
        .message-box h2 {
            margin-bottom: 20px;
            font-size: 28px;
        }
        .success-title {
            color: #28a745; /* 成功メッセージの色 */
        }
        .error-title {
            color: #dc3545; /* エラーメッセージの色 */
        }
        .message-box p {
            font-size: 18px;
            color: #555;
            margin-bottom: 30px;
        }
        .button-link {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 25px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        .button-link:hover {
            background-color: #0056b3;
        }
        .footer {
            text-align: center;
            font-size: 12px;
            color: #333;
            padding: 20px;
            border-top: 1px solid #ccc;
            background-color: #f0f0f0;
        }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
    </header>

    <div class="container">
        <div class="message-box">
            <%
                // サーブレットから渡された属性を取得
                String message = (String) request.getAttribute("message");
                Boolean isSuccessObj = (Boolean) request.getAttribute("isSuccess");
                boolean isSuccess = (isSuccessObj != null) ? isSuccessObj.booleanValue() : false; // nullチェックを追加
            %>
            <h2 class="<%= isSuccess ? "success-title" : "error-title" %>">
                <%= isSuccess ? "削除が完了しました" : "削除に失敗しました" %>
            </h2>
            <p><%= message != null ? message : "" %></p> <%-- nullチェックを追加 --%>
            <a href="SubjectListAction" class="button-link">科目一覧に戻る</a>
        </div>
    </div>

    <div class="footer">
        © 2025 TIC<br>大原学園
    </div>
</body>
</html>