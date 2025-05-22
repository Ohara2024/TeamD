<%@ page language="java" contentType="text/html; charset=UTF8" pageEncoding="UTF8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>成績参照 - 得点管理システム</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f7fa;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
        }
        .main-wrapper {
            display: flex;
            width: 90%;
            max-width: 1200px;
            background-color: #ffffff;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            border-radius: 8px;
            margin: 20px 0;
        }
        .menu-bar {
            width: 180px;
            min-width: 180px;
            padding: 20px;
            background-color: #e9eff5;
            border-right: 1px solid #dee2e6;
            border-radius: 8px 0 0 8px;
        }
        .menu-bar h3 {
            color: #34495e;
            margin-top: 0;
            padding-bottom: 10px;
            border-bottom: 1px solid #c9d6e4;
        }
        .menu-bar ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .menu-bar li {
            margin-bottom: 10px;
        }
        .menu-bar a {
            text-decoration: none;
            color: #34495e;
            display: block;
            padding: 8px 10px;
            border-radius: 4px;
            transition: background-color 0.3s ease;
        }
        .menu-bar a:hover, .menu-bar a.active {
            background-color: #d1e0ed;
        }
        .content-area {
            flex-grow: 1;
            padding: 30px;
            width: calc(100% - 220px); /* メニューバーの幅を考慮 */
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-bottom: 20px;
            margin-bottom: 30px;
            border-bottom: 2px solid #007bff;
        }
        .header h1 {
            color: #2c3e50;
            margin: 0;
            font-size: 1.8em;
        }
        .header .user-info {
            font-size: 0.9em;
            color: #555;
        }
        .header .user-info a {
            color: #007bff;
            text-decoration: none;
        }
        .header .user-info a:hover {
            text-decoration: underline;
        }
        .search-section {
            background-color: #fcfcfc;
            border: 1px solid #e0e0e0;
            padding: 25px;
            margin-bottom: 25px;
            border-radius: 6px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .search-section h2 {
            color: #34495e;
            font-size: 1.2em;
            margin-top: 0;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px dashed #e0e0e0;
        }
        .form-group {
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }
        label {
            display: inline-block;
            width: 100px;
            min-width: 100px;
            text-align: left;
            margin-right: 15px;
            color: #555;
            font-weight: bold;
        }
        select, input[type="text"] {
            flex-grow: 1;
            padding: 10px 12px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1em;
            max-width: 300px;
        }
        input[type="text"]::placeholder {
            color: #aaa;
        }
        button[type="submit"] {
            padding: 10px 25px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1em;
            transition: background-color 0.3s ease;
            margin-left: 115px;
        }
        button[type="submit"]:hover {
            background-color: #0056b3;
        }
        .separator {
            border-top: 1px solid #e0e0e0;
            margin: 30px 0;
        }
        .caution, .info-message, .error-message {
            margin-top: 20px;
            padding: 15px;
            border-radius: 5px;
            font-size: 0.9em;
            text-align: center;
        }
        .caution {
            background-color: #fff3cd;
            border: 1px solid #ffeeba;
            color: #856404;
        }
        .info-message {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
        }
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        .footer {
            width: 100%;
            text-align: center;
            margin-top: 40px;
            padding: 20px 0;
            border-top: 1px solid #e0e0e0;
            color: #777;
            font-size: 0.8em;
            background-color: #f4f7fa;
        }
    </style>
</head>
<body>
    <div class="main-wrapper">
        <div class="menu-bar">
            <h3>メニュー</h3>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main/StudentList.action" class="active">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/main/TestRegist.action" class="active">成績登録</a></li>
                <li><a href="${pageContext.request.contextPath}/main/TestList.action" class="active">成績参照</a></li>
                <li><a href="${pageContext.request.contextPath}/main/SubjectListAction" class="active">科目管理</a></li>
            </ul>
        </div>

        <div class="content-area">
            <div class="header">
                 <h1>成績参照</h1>
                <span class="user-info">
                    <c:if test="${not empty sessionScope.teacher}">
                        ${sessionScope.teacher.name}さん
                    </c:if>
                    <c:if test="${empty sessionScope.teacher}">
                        ゲストさん <%-- ログイン機能がないため、当面はゲスト表示 --%>
                    </c:if>
                    <a href="<%= request.getContextPath() %>/login/logout" class="logout-link">ログアウト</a>
                </span>
            </div>

            <%-- エラーメッセージ表示 --%>
            <c:if test="${not empty requestScope.errorMessage}">
                <p class="error-message"><c:out value="${requestScope.errorMessage}"/></p>
            </c:if>
            <%-- 情報メッセージ表示 --%>
            <c:if test="${not empty requestScope.infoMessage && empty requestScope.errorMessage}">
                <p class="info-message"><c:out value="${requestScope.infoMessage}"/></p>
            </c:if>

            <form action="${pageContext.request.contextPath}/main/TestList.action" method="post">
                <div class="search-section">
                    <h2>科目情報で検索</h2>
                    <div class="form-group">
                        <label for="entYear">入学年度</label>
                        <select id="entYear" name="entYear">
                            <option value="">選択してください</option>
                            <c:forEach var="year" items="${requestScope.entYearSet}">
                                <option value="${year}" ${year == requestScope.fEntYear ? 'selected' : ''}>${year}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="classNum">クラス</label>
                        <select id="classNum" name="classNum">
                            <option value="">選択してください</option>
                            <c:forEach var="classVal" items="${requestScope.classNumSet}">
                                <option value="${classVal}" ${classVal == requestScope.fClassNum ? 'selected' : ''}>${classVal}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="subjectCd">科目</label>
                        <select id="subjectCd" name="subjectCd">
                            <option value="">選択してください</option>
                             <c:forEach var="subject" items="${requestScope.subjects}">
                                <option value="${subject.cd}" ${subject.cd == requestScope.fSubjectCd ? 'selected' : ''}>${subject.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit">検索</button>
                </div>
            </form>

            <div class="separator"></div>

            <form action="${pageContext.request.contextPath}/main/TestList.action" method="post">
                <div class="search-section">
                    <h2>学生情報で検索</h2>
                    <div class="form-group">
                        <label for="studentNo">学生番号</label>
                        <input type="text" id="studentNo" name="studentNo" placeholder="学生番号を入力してください" value="${requestScope.fStudentNo}">
                    </div>
                    <button type="submit">検索</button>
                </div>
            </form>

            <%-- 初期表示時の案内メッセージはサーブレットから infoMessage として渡される想定 --%>
            <%-- <p class="caution">科目情報を選択または学生情報を入力して検索ボタンをクリックしてください。</p> --%>
        </div>
    </div>
    <div class="footer">
        © <%= java.time.Year.now().getValue() %> TIC<br>
        大原学園
    </div>
</body>
</html>