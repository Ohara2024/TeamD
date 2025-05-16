<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>ログイン</title>
    <style>
        body {
            font-family: sans-serif;
            background-color: #f4f4f4;
            color: #333;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .container {
            background-color: #fff;
            padding: 20px;
            border: 1px solid #ccc;
            width: 300px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        h2 {
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 5px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 1.2em;
            font-weight: normal;
        }

        .logout-message {
            color: green;
            background-color: #e6ffe6;
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccffcc;
            border-radius: 3px;
            font-size: 0.9em;
            text-align: center;
        }

        .error-container {
            margin-bottom: 10px;
            padding: 8px;
            border-radius: 3px;
            font-size: 0.9em;
            text-align: center;
        }

        .error {
            color: red;
            background-color: #ffe6e6;
            border: 1px solid #ffcccc;
        }

        .attempts {
            color: orange;
            background-color: #fff3e0;
            border: 1px solid #ffe0b2;
        }

        .locked {
            color: darkred;
            background-color: #ffebee;
            border: 1px solid #ef9a9a;
        }

        form {
            margin-top: 10px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-size: 0.9em;
        }

        input[type="text"],
        input[type="password"] {
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            width: 100%;
            box-sizing: border-box;
            border-radius: 3px;
            font-size: 0.9em;
        }

        input[type="submit"] {
            background-color: #333;
            color: white;
            padding: 10px 15px;
            border: none;
            cursor: pointer;
            font-size: 1em;
            width: 100%;
            box-sizing: border-box;
            border-radius: 5px;
        }

        input[type="submit"]:hover {
            background-color: #555;
        }

        div {
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>ログイン</h2>
        <% if (request.getParameter("logout") != null && request.getParameter("logout").equals("true")) { %>
            <p class="logout-message">ログアウトしました。</p>
        <% } %>
        <div class="error-container">
            <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="error"><%= request.getAttribute("errorMessage") %></p>
            <% } %>
            <% if (request.getAttribute("remainingAttempts") != null) { %>
                <p class="attempts">入力可能回数: <%= request.getAttribute("remainingAttempts") %> 回</p>
            <% } %>
            <% if (request.getAttribute("loginLocked") != null && (Boolean)request.getAttribute("loginLocked")) { %>
                <p class="locked">アカウントがロックされました。しばらく経ってから再度お試しください。</p>
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        const form = document.querySelector('form');
                        const inputs = form.querySelectorAll('input');
                        const submitButton = form.querySelector('input[type="submit"]');
                        inputs.forEach(input = input.disabled = true);
                        submitButton.disabled = true;
                    });
                </script>
            <% } %>
        </div>
        <form action="<%= request.getContextPath() %>/login" method="post">
            <div>
                <label for="username">ユーザー名:</label>
                <input type="text" id="username" name="username">
            </div>
            <div>
                <label for="password">パスワード:</label>
                <input type="password" id="password" name="password">
            </div>
            <div style="margin-top: 10px;">
                <input type="submit" value="ログイン"
                       <% if (request.getAttribute("loginLocked") != null && (Boolean)request.getAttribute("loginLocked")) { %>
                           disabled
                       <% } %>
                >
            </div>
        </form>
    </div>
</body>
</html>