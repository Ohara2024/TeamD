<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>成績参照結果 - 得点管理システム</title>
    <%-- 共通スタイルを使用する想定 --%>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f7fa; display: flex; flex-direction: column; align-items: center; min-height: 100vh; }
        .main-wrapper { display: flex; width: 90%; max-width: 1200px; background-color: #ffffff; box-shadow: 0 4px 8px rgba(0,0,0,0.1); border-radius: 8px; margin: 20px 0; }
        .menu-bar { width: 180px; min-width: 180px; padding: 20px; background-color: #e9eff5; border-right: 1px solid #dee2e6; border-radius: 8px 0 0 8px; }
        .menu-bar h3 { color: #34495e; margin-top: 0; padding-bottom: 10px; border-bottom: 1px solid #c9d6e4; }
        .menu-bar ul { list-style: none; padding: 0; margin: 0; }
        .menu-bar li { margin-bottom: 10px; }
        .menu-bar a { text-decoration: none; color: #34495e; display: block; padding: 8px 10px; border-radius: 4px; transition: background-color 0.3s ease; }
        .menu-bar a:hover, .menu-bar a.active { background-color: #d1e0ed; }
        .content-area { flex-grow: 1; padding: 30px; width: calc(100% - 220px); }
        .header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20px; margin-bottom: 30px; border-bottom: 2px solid #007bff; }
        .header h1 { color: #2c3e50; margin: 0; font-size: 1.8em; }
        .header .user-info { font-size: 0.9em; color: #555; }
        .header .user-info a { color: #007bff; text-decoration: none; }
        .header .user-info a:hover { text-decoration: underline; }
        .search-section { background-color: #fcfcfc; border: 1px solid #e0e0e0; padding: 25px; margin-bottom: 25px; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .search-section h2 { color: #34495e; font-size: 1.2em; margin-top: 0; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 1px dashed #e0e0e0; }
        .form-group { margin-bottom: 15px; display: flex; align-items: center; }
        label { display: inline-block; width: 100px; min-width: 100px; text-align: left; margin-right: 15px; color: #555; font-weight: bold; }
        select, input[type="text"] { flex-grow: 1; padding: 10px 12px; border: 1px solid #ccc; border-radius: 5px; font-size: 1em; max-width: 250px; }
        button[type="submit"] { padding: 10px 20px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 1em; transition: background-color 0.3s ease; margin-left: 115px; }
        button[type="submit"]:hover { background-color: #0056b3; }
        .table { width: 100%; margin-bottom: 20px; border-collapse: collapse; background-color: white; }
        .table th, .table td { border: 1px solid #dee2e6; padding: 10px 12px; text-align: left; }
        .table th { background-color: #e9ecef; color: #495057; font-weight: bold; }
        .table tbody tr:nth-child(odd) { background-color: #f8f9fa; }
        .table tbody tr:hover { background-color: #e2e6ea; }
        .error-message, .info-message, .no-data-message { margin-top: 20px; padding: 15px; border-radius: 5px; font-size: 0.9em; text-align: center; }
        .error-message { background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; }
        .info-message { background-color: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; }
        .no-data-message { background-color: #fff3cd; border: 1px solid #ffeeba; color: #856404; }
        .footer { width: 100%; text-align: center; margin-top: 40px; padding: 20px 0; border-top: 1px solid #e0e0e0; color: #777; font-size: 0.8em; background-color: #f4f7fa; }
        h3.results-title { color: #34495e; font-size: 1.3em; margin-top: 30px; margin-bottom: 15px; padding-bottom:10px; border-bottom:1px solid #e0e0e0;}
        .student-info-section { margin-bottom: 20px; padding:15px; border: 1px solid #e0e0e0; border-radius: 6px; background-color: #f9f9f9;}
        .student-info-section p { margin: 5px 0; font-size: 1.0em;}
        .student-info-section strong { display: inline-block; width: 100px; }
        .back-link { display: inline-block; margin-top: 20px; padding: 8px 15px; background-color: #6c757d; color: white; text-decoration: none; border-radius: 4px; transition: background-color 0.3s ease; }
        .back-link:hover { background-color: #5a6268; }
    </style>
</head>
<body>
    <div class="main-wrapper">
        <div class="menu-bar">
            <h3>メニュー</h3>
            <ul>
                <li><a href="#">学生管理</a></li>
                <li><a href="test_regist.jsp">成績登録</a></li>
                <li><a href="${pageContext.request.contextPath}/main/TestList.action" class="active">成績参照</a></li>
                <li><a href="#">成績照会</a></li>
                <li><a href="#">科目管理</a></li>
            </ul>
        </div>
        <div class="content-area">
            <div class="header">
                <h1>成績参照結果</h1>
                 <span class="user-info">
                    <c:if test="${not empty sessionScope.teacher}">${sessionScope.teacher.name}さん</c:if>
                    <c:if test="${empty sessionScope.teacher}">ゲストさん</c:if>
                    <a href="<%= request.getContextPath() %>/logout.jsp" class="logout-link">ログアウト</a>
                </span>
            </div>

            <%-- 検索条件入力フォーム (test_list.jsp と同様のものをここに表示し、再検索可能にする) --%>
            <form action="${pageContext.request.contextPath}/main/TestList.action" method="post">
                <div class="search-section">
                    <h2>検索条件</h2>
                    <div class="form-group">
                        <label for="entYear">入学年度:</label>
                        <select id="entYear" name="entYear">
                            <option value="">すべて</option>
                            <c:forEach var="year" items="${requestScope.entYearSet}">
                                <option value="${year}" ${year == requestScope.fEntYear ? 'selected' : ''}>${year}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="classNum">クラス:</label>
                        <select id="classNum" name="classNum">
                            <option value="">すべて</option>
                            <c:forEach var="classVal" items="${requestScope.classNumSet}">
                                <option value="${classVal}" ${classVal == requestScope.fClassNum ? 'selected' : ''}>${classVal}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="subjectCd">科目:</label>
                        <select id="subjectCd" name="subjectCd">
                            <option value="">すべて</option>
                            <c:forEach var="subject" items="${requestScope.subjects}">
                                <option value="${subject.cd}" ${subject.cd == requestScope.fSubjectCd ? 'selected' : ''}>${subject.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div style="text-align: center; margin-bottom: 15px;"> または </div>
                     <div class="form-group">
                        <label for="studentNo">学生番号:</label>
                        <input type="text" id="studentNo" name="studentNo" placeholder="学生番号で検索" value="${requestScope.fStudentNo}">
                    </div>
                    <button type="submit">検索</button>
                </div>
            </form>


            <c:if test="${not empty requestScope.errorMessage}">
                <p class="error-message"><c:out value="${requestScope.errorMessage}"/></p>
            </c:if>

            <%-- 科目別成績一覧の表示 --%>
            <c:if test="${requestScope.searchMode == 'subject'}">
                <c:if test="${not empty requestScope.scoreList}">
                    <h3 class="results-title">
                        科目別 成績一覧
                        <c:if test="${not empty requestScope.searchSubjectName}">
                            ： ${requestScope.searchSubjectName}
                        </c:if>
                    </h3>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>入学年度</th>
                                <th>クラス</th>
                                <th>学生番号</th>
                                <th>氏名</th>
                                <th>1回</th>
                                <th>2回</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${requestScope.scoreList}">
                                <tr>
                                    <td><c:out value="${item.entYear}"/></td>
                                    <td><c:out value="${item.classNum}"/></td>
                                    <td><c:out value="${item.studentNo}"/></td>
                                    <td><c:out value="${item.studentName}"/></td>
                                    <td><c:out value="${item.getPoint(1)}"/></td>
                                    <td><c:out value="${item.getPoint(2)}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${empty requestScope.scoreList and empty requestScope.errorMessage and not empty requestScope.fEntYear and not empty requestScope.fClassNum and not empty requestScope.fSubjectCd}">
                    <p class="no-data-message">指定された科目・条件での成績情報は見つかりませんでした。</p>
                </c:if>
            </c:if>

            <%-- 学生個別成績の表示 --%>
            <c:if test="${requestScope.searchMode == 'student'}">
                <c:if test="${not empty requestScope.student}">
                    <h3 class="results-title">学生別 成績詳細</h3>
                    <div class="student-info-section">
                        <p><strong>学生番号:</strong> <c:out value="${requestScope.student.no}"/></p>
                        <p><strong>氏〠   名:</strong> <c:out value="${requestScope.student.name}"/></p>
                        <p><strong>入学年度:</strong> <c:out value="${requestScope.student.entYear}"/></p>
                        <p><strong>クラス:</strong> <c:out value="${requestScope.student.classNum}"/></p>
                    </div>

                    <c:if test="${not empty requestScope.studentScores}">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>科目コード</th>
                                    <th>科目名</th>
                                    <th>回数</th>
                                    <th>得点</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="score" items="${requestScope.studentScores}">
                                    <tr>
                                        <td><c:out value="${score.subjectCd}"/></td>
                                        <td><c:out value="${score.subjectName}"/></td>
                                        <td><c:out value="${score.num}"/></td>
                                        <td><c:out value="${score.point}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                    <c:if test="${empty requestScope.studentScores and empty requestScope.errorMessage}">
                        <p class="no-data-message">この学生の成績情報は見つかりませんでした。</p>
                    </c:if>
                </c:if>
                 <c:if test="${empty requestScope.student and empty requestScope.errorMessage and not empty requestScope.fStudentNo}">
                    <%-- このメッセージはサーブレット側でerrorMessageとして設定されるため、通常は不要 --%>
                    <%-- <p class="no-data-message">指定された学生番号の学生が見つかりませんでした。</p> --%>
                </c:if>
            </c:if>
            
            <%-- 検索が実行されず、エラーもない場合の初期メッセージ (主にdoGetからの遷移時を想定するが、POSTで条件不備でここにきた場合も考慮) --%>
            <c:if test="${empty requestScope.searchMode and empty requestScope.errorMessage and not empty requestScope.infoMessage}">
                 <p class="info-message"><c:out value="${requestScope.infoMessage}"/></p>
            </c:if>

            <%-- 検索画面に戻るリンクは不要（このページ自体が検索フォームと結果を兼ねるため） --%>
            <%-- <a href="${pageContext.request.contextPath}/main/TestList.action" class="back-link">検索画面に戻る</a> --%>
        </div>
    </div>
    <div class="footer">
        © <%= java.time.Year.now().getValue() %> TIC<br>
        大原学園
    </div>
