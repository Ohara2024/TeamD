<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>ログアウト完了</title>
    <style>
        body {
            font-family: sans-serif;
            background-color: #f9f9f9;
            color: #333;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
        }

        .message-container {
            background-color: #fff;
            padding: 30px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        h2 {
            color: green;
            margin-bottom: 20px;
        }

        p {
            font-size: 1em;
            margin-bottom: 15px;
        }

        .redirect-message {
            font-size: 0.9em;
            color: #777;
        }
    </style>
    <script>
        // 3秒後にログイン画面へ自動的にリダイレクト
        setTimeout(function() {
            window.location.href = "<%= request.getContextPath() %>/login/login.jsp?logout=true";
        }, 3000);
    </script>
</head>
<body>
    <div class="message-container">
        <h2>ログアウトしました</h2>
        <p>数秒後にログイン画面へ移動します。</p>
        <p class="redirect-message">(自動的に移動します)</p>
    </div>
</body>
</html>