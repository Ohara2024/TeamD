<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>成績登録完了 - 得点管理システム</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f7fa;
            display: flex;
            flex-direction: column; /* ヘッダーとコンテナを縦に並べる */
            align-items: center;
            min-height: 100vh;
        }
        .page-header { /* ヘッダー用のスタイルを追加 */
            width: 100%;
            background-color: #fff;
            padding: 15px 30px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-between; /* タイトルとユーザー情報を両端に */
            align-items: center;
            box-sizing: border-box;
        }
        .page-header h1 {
            margin: 0;
            font-size: 1.5em; /* 少し小さく */
            color: #2c3e50;
        }
        .user-info { /* ユーザー情報表示用 */
            font-size: 0.9em;
            color: #555;
        }
        .container {
            margin-top: 50px; /* ヘッダーとの間隔 */
            background-color: #fff;
            padding: 30px 40px; /* パディングを調整 */
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
            display: inline-block;
        }
        .container h2 { /* メッセージタイトル用のスタイル */
            font-size: 1.8em; /* 少し大きく */
            margin-top: 0;
            margin-bottom: 20px;
        }
        .message {
            font-size: 1.1em;
            margin-bottom: 30px; /* ボタンとの間隔を調整 */
            padding: 15px;
            border-radius: 5px;
        }
        .success {
            color: #155724;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
        }
        .error {
            color: #721c24;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
        }
        .nav-links a {
            text-decoration: none;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            margin: 0 10px;
            transition: background-color 0.3s ease;
            font-size: 0.9em;
        }
        .nav-links a:hover {
            background-color: #0056b3;
        }
        .nav-links a.to-menu { /* メニューへ戻るボタンの色を少し変える */
            background-color: #6c757d;
        }
        .nav-links a.to-menu:hover {
            background-color: #5a6268;
        }
        .footer {
             width: 100%;
             text-align: center;
             margin-top: auto; /* フッターをページ下部に押し出す */
             padding: 20px 0;
             border-top: 1px solid #e0e0e0;
             color: #777;
             font-size: 0.8em;
             background-color: #f4f7fa;
        }
    </style>
</head>
<body>
    <div class="page-header">
        <h1>得点管理システム</h1>
        <span class="user-info">
            <c:if test="${not empty sessionScope.teacher}">${sessionScope.teacher.name}さん</c:if>
            <c:if test="${empty sessionScope.teacher}">ゲストさん</c:if>
            <%-- ログアウト機能はmenu.jsp等にある想定なのでここでは省略 --%>
        </span>
    </div>

    <div class="container">
        <c:choose>
            <c:when test="${not empty requestScope.successMessage}">
                <h2 style="color: #28a745;">処理完了</h2>
                <p class="message success"><c:out value="${requestScope.successMessage}"/></p>
            </c:when>
            <c:when test="${not empty requestScope.errorMessage}">
                <h2 style="color: #dc3545;">処理失敗</h2>
                <p class="message error"><c:out value="${requestScope.errorMessage}"/></p>
            </c:when>
            <c:otherwise>
                <h2>メッセージ</h2>
                <p class="message">指定されたメッセージはありません。</p>
            </c:otherwise>
        </c:choose>

        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/main/TestRegist.action">続けて登録/変更する</a>
            <a href="${pageContext.request.contextPath}/scoremanager/main/menu.jsp" class="to-menu">メニューに戻る</a>
        </div>
    </div>

    <div class="footer">
        © <%= java.time.Year.now().getValue() %> TIC<br>
        大原学園
    </div>
</body>
</html>