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
            flex-direction: column; /* menu-bar と content-area を縦に並べるため、そして中央寄せのため */
            align-items: center; /* 中央寄せ */
            min-height: 100vh;
        }
        .main-wrapper { /* このクラスは元のHTMLにはありませんでしたが、構造上追加した方が管理しやすいです */
            display: flex;
            width: 90%;
            max-width: 1200px;
            background-color: #ffffff;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            border-radius: 8px;
            margin: 20px 0;
        }
        .menu-bar {
            width: 180px; /* 固定幅 */
            min-width: 180px; /* 縮小しないように */
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
        .menu-bar a:hover, .menu-bar a.active { /* activeクラスのスタイルを追加 */
            background-color: #d1e0ed;
        }
        .content-area {
            flex-grow: 1;
            padding: 30px;
            width: calc(100% - 220px); /* menu-barの幅を考慮 */
        }
        .header {
            display: flex;
            justify-content: space-between; /* タイトルとユーザー情報を両端に */
            align-items: center; /* 縦方向中央揃え */
            padding-bottom: 20px;
            margin-bottom: 30px;
            border-bottom: 2px solid #007bff;
        }
        .header h1 { /* H1のスタイルをヘッダー内に移動 */
            color: #2c3e50;
            margin: 0; /* 余白をリセット */
            font-size: 1.8em; /* 少し小さく調整 */
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
            max-width: 300px; /* 最大幅を設定 */
        }
        input[type="text"]::placeholder {
            color: #aaa;
        }
        button[type="submit"] { /* buttonではなくbutton[type="submit"]に限定 */
            padding: 10px 25px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1em;
            transition: background-color 0.3s ease;
            margin-left: 115px; /* ラベルの幅 + margin-right に合わせる */
        }
        button[type="submit"]:hover {
            background-color: #0056b3;
        }
        .separator {
            border-top: 1px solid #e0e0e0;
            margin: 30px 0;
        }
        .caution, .info-message, .error-message { /* メッセージ表示用の共通スタイル */
            margin-top: 20px;
            padding: 15px;
            border-radius: 5px;
            font-size: 0.9em;
            text-align: center;
        }
        .caution { /* 元のスタイル */
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

        .footer { /* body直下から移動 */
            width: 100%;
            text-align: center;
            margin-top: 40px;
            padding: 20px 0; /* 上下のパディング */
            border-top: 1px solid #e0e0e0;
            color: #777;
            font-size: 0.8em;
            background-color: #f4f7fa; /* 背景色をbodyと合わせる */
        }
    </style>
</head>
<body>
    <div class="main-wrapper">
        <div class="menu-bar">
            <h3>メニュー</h3>
            <ul>
                <li><a href="#">学生管理</a></li>
                <li><a href="#">成績登録</a></li>
                <li><a href="#" class="active">成績参照</a></li> <%-- 現在のページなので active --%>
                <li><a href="#">成績照会</a></li>
                <li><a href="#">科目管理</a></li>
            </ul>
        </div>

        <div class="content-area">
            <div class="header">
                 <h1>成績参照</h1>
                <span class="user-info">
                    <c:if test="${not empty sessionScope.teacher}"> <%-- Teacher Beanがセッションに teacher という名前で格納されている想定 --%>
                        ${sessionScope.teacher.name}さん
                    </c:if>
                    <c:if test="${empty sessionScope.teacher}">
                        ゲストさん
                    </c:if>
                    <a href="#">ログアウト</a> <%-- ログアウト処理へのリンク --%>
                </span>
            </div>

            <%-- エラーメッセージ表示 --%>
            <c:if test="${not empty requestScope.errorMessage}">
                <p class="error-message">${requestScope.errorMessage}</p>
            </c:if>
            <%-- 情報メッセージ表示 --%>
            <c:if test="${not empty requestScope.infoMessage}">
                <p class="info-message">${requestScope.infoMessage}</p>
            </c:if>

            <%-- 科目情報で検索フォーム --%>
            <%-- このフォームは TestListAction の doGet で準備されたデータで成績一覧画面(test_list_student.jsp)を初期表示し、
                 そこから科目等を絞り込むUIを想定しているため、この test_list.jsp は本来不要かもしれません。
                 もしこの画面を成績参照機能のトップページとするなら、action先は TestListAction で、
                 TestListActionのdoGetはこの画面にフォワードし、doPostで検索条件を受け取る形になります。
                 ここでは、image_0d1170.png のUIを再現する形で残します。 --%>
            <form action="${pageContext.request.contextPath}/main/TestList.action" method="post">
                <div class="search-section">
                    <h2>科目情報で検索</h2>
                    <div class="form-group">
                        <label for="entYear">入学年度</label>
                        <select id="entYear" name="entYear">
                            <option value="">選択してください</option>
                            <c:if test="${not empty requestScope.entYearSet}">
                                <c:forEach var="year" items="${requestScope.entYearSet}">
                                    <option value="${year}" ${year == requestScope.fEntYear ? 'selected' : ''}>${year}</option>
                                </c:forEach>
                            </c:if>
                            <c:if test="${empty requestScope.entYearSet}">
                                <%-- フォールバックまたは静的データ（例） --%>
                                <option value="2024">2024</option>
                                <option value="2023">2023</option>
                                <option value="2022">2022</option>
                            </c:if>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="classNum">クラス</label>
                        <select id="classNum" name="classNum">
                            <option value="">選択してください</option>
                            <c:if test="${not empty requestScope.classNumSet}">
                                <c:forEach var="classVal" items="${requestScope.classNumSet}">
                                    <option value="${classVal}" ${classVal == requestScope.fClassNum ? 'selected' : ''}>${classVal}</option>
                                </c:forEach>
                            </c:if>
                             <c:if test="${empty requestScope.classNumSet}">
                                <option value="101">101</option>
                                <option value="201">201</option>
                            </c:if>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="subjectCd">科目</label>
                        <select id="subjectCd" name="subjectCd">
                            <option value="">選択してください</option>
                             <c:if test="${not empty requestScope.subjects}">
                                <c:forEach var="subject" items="${requestScope.subjects}">
                                    <option value="${subject.cd}" ${subject.cd == requestScope.fSubjectCd ? 'selected' : ''}>${subject.name}</option>
                                </c:forEach>
                            </c:if>
                            <c:if test="${empty requestScope.subjects}">
                                <option value="S001">情報処理基礎</option>
                                <option value="S002">プログラミング</option>
                            </c:if>
                        </select>
                    </div>
                    <button type="submit">検索</button>
                </div>
            </form>

            <div class="separator"></div>

            <%-- 学生情報で検索フォーム --%>
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

            <p class="caution">科目情報を選択または学生情報を入力して検索ボタンをクリックしてください。</p>
        </div>
    </div>
    <div class="footer">
        © <%= java.time.Year.now().getValue() %> TIC<br> <%-- 現在の年を動的に表示 --%>
        大原学園
    </div>
</body>
</html>