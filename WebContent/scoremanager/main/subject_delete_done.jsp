<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Teacher" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>科目削除結果</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #ffffff; /* ボディ全体の背景色を白に修正 */
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
            text-align: left;
            flex-grow: 1;
            padding-left: 20px;
        }
        .header-right {
            display: flex;
            align-items: center;
            gap: 15px;
            padding-right: 20px;
            font-size: 14px;
        }
        .header-right a {
            color: #007bff;
            text-decoration: none;
        }
        .header-right a:hover {
            text-decoration: underline;
        }

        .main-wrapper { /* bodyとfooterの間の全体エリア */
            display: flex; /* flexboxでサイドとコンテンツを並べる */
            flex-grow: 1;
        }

        .left-panel { /* 左側の「科目情報削除」タイトルと「科目一覧」リンク */
            width: 200px; /* 固定幅 */
            padding: 20px 0 20px 20px; /* 上下左右パディング */
            box-sizing: border-box;
            background-color: #ffffff; /* 白背景 */
            border-right: 1px solid #e0e0e0; /* 右側の線 */
            box-shadow: 2px 0 5px rgba(0,0,0,0.05); /* 軽い影 */
        }
        .left-panel h2 {
            margin-top: 0;
            margin-bottom: 20px;
            font-size: 18px; /* 画像に合わせたサイズ */
            color: #333;
            padding-left: 0; /* 左寄せ */
        }
        .left-panel a { /* 左パネル内のリンクのスタイル */
            display: block; /* ブロック要素にして縦に積む */
            text-decoration: none;
            color: #007bff;
            padding: 5px 0;
            font-size: 14px;
        }
        .left-panel a:hover {
            text-decoration: underline;
        }

        .content-area { /* 右側のメッセージ表示エリア */
            flex-grow: 1; /* 残りのスペースを占める */
            padding: 30px; /* 内側のパディング */
            box-sizing: border-box;
            background-color: #ffffff; /* 白背景 */
        }

        .success-message-box { /* 薄い緑色の「削除が完了しました」ボックス */
            background-color: #e6ffe6; /* 薄い緑色 */
            border: 1px solid #a3e0a3; /* 緑色の枠線 */
            padding: 10px 15px;
            margin-bottom: 20px;
            font-size: 16px;
            color: #333; /* 文字色を黒に */
            text-align: left; /* 左寄せ */
        }
        .error-message-box { /* エラー時のボックス（もしあれば） */
            background-color: #ffe6e6; /* 薄い赤色 */
            border: 1px solid #e0a3a3; /* 赤色の枠線 */
            padding: 10px 15px;
            margin-bottom: 20px;
            font-size: 16px;
            color: #333;
            text-align: left;
        }

        .content-area p { /* content-area内のpタグ（メッセージ本文など） */
            font-size: 16px;
            color: #555;
            line-height: 1.5;
            margin-bottom: 30px;
            text-align: left;
        }

        /* 「科目一覧に戻る」リンクのためのスタイル */
        .return-link {
            display: inline-block;
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
            margin-top: 20px;
            padding: 0;
        }
        .return-link:hover {
            text-decoration: underline;
        }

        .footer { /* フッターのスタイル */
            text-align: center; /* 中央揃え */
            font-size: 12px;
            color: #333;
            padding: 20px;
            border-top: 1px solid #ccc;
            background-color: #f0f0f0; /* 薄いグレーの背景 */
            width: 100%; /* 幅100% */
            box-sizing: border-box; /* paddingを含めて幅を計算 */
            margin-top: auto; /* main-wrapperがコンテンツの高さに応じて伸縮するため、フッターを下に固定 */
        }
    </style>
</head>
<body>
    <header>
        <h1>得点管理システム</h1>
        <div class="header-right">
            <%
                Teacher currentTeacher = (Teacher) session.getAttribute("currentTeacher");
                if (currentTeacher != null) {
            %>
                <span><%= currentTeacher.getName() %>様</span>
            <%
                } else {
            %>
                <span>ゲスト様</span>
            <%
                }
            %>
            <a href="<%= request.getContextPath() %>/LogoutAction">ログアウト</a>
        </div>
    </header>

    <div class="main-wrapper">
        <div class="left-panel">
            <h2>科目情報削除</h2>
            <a href="SubjectListAction">科目管理</a>
        </div>

        <div class="content-area">
            <%
                String message = (String) request.getAttribute("message");
                Boolean isSuccessObj = (Boolean) request.getAttribute("isSuccess");
                boolean isSuccess = (isSuccessObj != null) ? isSuccessObj.booleanValue() : false;
            %>
            <%-- 削除結果メッセージボックス --%>
            <div class="<%= isSuccess ? "success-message-box" : "error-message-box" %>">
                <%= isSuccess ? "削除が完了しました" : "削除に失敗しました" %>
            </div>

            <%-- 詳細メッセージ（もしあれば） --%>
            <% if (message != null && !message.isEmpty()) { %>
                <p><%= message %></p>
            <% } %>

            <a href="SubjectListAction" class="return-link">科目一覧に戻る</a>
        </div>
    </div>

    <div class="footer">
        © 2025 TIC<br>大原学園
    </div>
</body>
</html>