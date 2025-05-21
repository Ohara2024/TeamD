<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>成績登録・変更</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f7fa; display: flex; flex-direction: column; align-items: center; min-height: 100vh; }
        .main-wrapper { display: flex; width: 95%; max-width: 1200px; background-color: #ffffff; box-shadow: 0 4px 8px rgba(0,0,0,0.1); border-radius: 8px; margin: 20px 0; }
        .menu-bar { width: 180px; min-width: 180px; padding: 20px; background-color: #e9eff5; border-right: 1px solid #dee2e6; border-radius: 8px 0 0 8px; }
        .menu-bar h3 { color: #34495e; margin-top: 0; padding-bottom: 10px; border-bottom: 1px solid #c9d6e4; }
        .menu-bar ul { list-style: none; padding: 0; margin: 0; }
        .menu-bar li { margin-bottom: 10px; }
        .menu-bar a { text-decoration: none; color: #34495e; display: block; padding: 8px 10px; border-radius: 4px; transition: background-color 0.3s ease; }
        .menu-bar a:hover, .menu-bar a.active { background-color: #d1e0ed; }
        .content-area { flex-grow: 1; padding: 30px; width: calc(100% - 220px); } /* メニューバーの幅を考慮 */
        .header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20px; margin-bottom: 30px; border-bottom: 2px solid #007bff; }
        .header h1 { color: #2c3e50; margin: 0; font-size: 1.8em; }
        .header .user-info { font-size: 0.9em; color: #555; }
        .search-section, .results-section { background-color: #fcfcfc; border: 1px solid #e0e0e0; padding: 25px; margin-bottom: 25px; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .search-section h2, .results-section h2 { color: #34495e; font-size: 1.2em; margin-top: 0; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 1px dashed #e0e0e0; }
        .form-group { margin-bottom: 15px; display: flex; align-items: center; }
        label { display: inline-block; width: 100px; min-width: 100px; text-align: left; margin-right: 15px; color: #555; font-weight: bold; }
        select, input[type="text"], input[type="number"] { flex-grow: 1; padding: 8px 10px; border: 1px solid #ccc; border-radius: 5px; font-size: 0.9em; max-width: 280px; box-sizing: border-box;}
        input[type="number"] { width: 80px; flex-grow: 0;} /* 点数入力欄の幅調整 */
        .button-group { margin-top: 20px; text-align: center; }
        .button-group button { padding: 10px 25px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 1em; transition: background-color 0.3s ease; margin: 0 10px; }
        .button-group button:hover { background-color: #0056b3; }
        .button-group button.register { background-color: #28a745; } /* 登録ボタンの色 */
        .button-group button.register:hover { background-color: #218838; }
        .table { width: 100%; margin-top: 20px; border-collapse: collapse; }
        .table th, .table td { border: 1px solid #dee2e6; padding: 8px 10px; text-align: left; font-size: 0.9em;}
        .table th { background-color: #e9ecef; font-weight: bold;}
        .table input[type="number"] { width: 60px; padding: 5px; } /* テーブル内の点数入力欄 */
        .error-message, .info-message, .success-message { margin-bottom: 15px; padding: 10px; border-radius: 4px; text-align: center; }
        .error-message { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb;}
        .info-message { background-color: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb;}
        .success-message { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb;}
        .footer { width: 100%; text-align: center; margin-top: 40px; padding: 20px 0; border-top: 1px solid #e0e0e0; color: #777; font-size: 0.8em; background-color: #f4f7fa; }
    </style>
</head>
<body>
    <div class="main-wrapper">
        <%-- menu.jsp をインポートするか、メニューのHTMLを直接記述 --%>
        <%-- 例として、menu.jsp が /scoremanager/main/menu.jsp にあると仮定 --%>
        <jsp:include page="/scoremanager/main/menu.jsp" />

        <div class="content-area">
            <div class="header">
                <h1>成績登録・変更</h1>
                <span class="user-info">
                    <c:if test="${not empty sessionScope.teacher}">${sessionScope.teacher.name}さん</c:if>
                    <c:if test="${empty sessionScope.teacher}">ゲストさん</c:if>
                    <%-- ログアウト機能はmenu.jsp側にある想定 --%>
                </span>
            </div>

            <c:if test="${not empty requestScope.errorMessage}">
                <p class="error-message"><c:out value="${requestScope.errorMessage}"/></p>
            </c:if>
            <c:if test="${not empty requestScope.infoMessage && empty requestScope.errorMessage}">
                <p class="info-message"><c:out value="${requestScope.infoMessage}"/></p>
            </c:if>
            <%-- 登録/更新成功メッセージは test_regist_done.jsp で表示するため、ここでは不要 --%>

            <div class="search-section">
                <h2>検索条件</h2>
                <form id="searchForm" action="${pageContext.request.contextPath}/main/TestRegist.action" method="post">
                    <input type="hidden" name="action" value="search_students_for_score">
                    <div class="form-group">
                        <label for="fEntYear">入学年度:</label>
                        <select id="fEntYear" name="fEntYear">
                            <option value="">選択してください</option>
                            <c:forEach var="year" items="${requestScope.entYearSet}">
                                <option value="${year}" ${year == requestScope.fEntYear ? 'selected' : ''}>${year}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="fClassNum">クラス:</label>
                        <select id="fClassNum" name="fClassNum">
                            <option value="">選択してください</option>
                            <c:forEach var="classVal" items="${requestScope.classNumSet}">
                                <option value="${classVal}" ${classVal == requestScope.fClassNum ? 'selected' : ''}>${classVal}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="fSubjectCd">科目:</label>
                        <select id="fSubjectCd" name="fSubjectCd">
                            <option value="">選択してください</option>
                             <c:forEach var="subject" items="${requestScope.subjectList}">
                                <option value="${subject.cd}" ${subject.cd == requestScope.fSubjectCd ? 'selected' : ''}>${subject.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="fTestNo">回数:</label>
                        <select id="fTestNo" name="fTestNo">
                            <option value="">選択してください</option>
                            <option value="1" ${"1" == requestScope.fTestNo ? 'selected' : ''}>1</option>
                            <option value="2" ${"2" == requestScope.fTestNo ? 'selected' : ''}>2</option>
                            <%-- 必要なら3回目以降も固定で追加するか、DBから取得する仕組みを検討 --%>
                        </select>
                    </div>
                    <div class="button-group">
                        <button type="submit">検索</button>
                    </div>
                </form>
            </div>

            <c:if test="${not empty requestScope.students}">
                <div class="results-section">
                    <h2>成績入力: 
                        <c:out value="${requestScope.searchedSubject.name}"/>
                        (第<c:out value="${requestScope.searchedTestNo}"/>回)
                    </h2>
                    <form id="scoreForm" action="${pageContext.request.contextPath}/main/TestRegist.action" method="post">
                        <input type="hidden" name="action" value="register_scores">
                        <%-- 検索条件をhiddenフィールドで再度送信（登録処理で必要になるため） --%>
                        <input type="hidden" name="hidden_fEntYear" value="${requestScope.fEntYear}">
                        <input type="hidden" name="hidden_fClassNum" value="${requestScope.fClassNum}">
                        <input type="hidden" name="hidden_fSubjectCd" value="${requestScope.fSubjectCd}">
                        <input type="hidden" name="hidden_fTestNo" value="${requestScope.searchedTestNo}">

                        <table class="table">
                            <thead>
                                <tr>
                                    <th>学生番号</th>
                                    <th>氏名</th>
                                    <th>点数 (0-100)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="student" items="${requestScope.students}" varStatus="loop">
                                    <tr>
                                        <td><c:out value="${student.no}"/></td>
                                        <td><c:out value="${student.name}"/></td>
                                        <td>
                                            <input type="number" name="point_${student.no}" min="0" max="100"
                                                   value="${requestScope.pointsMap[student.no]}" placeholder="未入力">
                                            <%-- 登録処理のために学生番号も送信 --%>
                                            <input type="hidden" name="studentNos" value="${student.no}">
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <div class="button-group">
                            <button type="submit" class="register">登録/更新</button>
                        </div>
                    </form>
                </div>
            </c:if>
            <c:if test="${empty requestScope.students && not empty requestScope.fEntYear && not empty requestScope.fClassNum && not empty requestScope.fSubjectCd && not empty requestScope.fTestNo && empty requestScope.errorMessage}">
                 <p class="info-message">指定された条件に合致する学生情報は見つかりませんでした。検索条件を変更して再度お試しください。</p>
            </c:if>

        </div>
    </div>
    <div class="footer">
        © <%= java.time.Year.now().getValue() %> TIC<br>
        大原学園
    </div>
</body>
</html>