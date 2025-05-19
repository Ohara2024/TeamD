package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Student;
import bean.Subject;
import bean.TestListSubject;
import dao.ClassNumDao;
import dao.SchoolDao;
import dao.StudentDao;
import dao.SubjectDao;
import dao.TestListSubjectDao;

@WebServlet("/main/TestList.action")
public class TestListAction extends HttpServlet {

    // ★★★ 重要: ログインなしで参照する場合、どの学校の情報を表示するかのデフォルト学校コード ★★★
    // ★★★ この値を、データベースに存在する実際の有効な学校コードに置き換えてください。 ★★★
    private static final String DEFAULT_SCHOOL_CD = "oom"; // 例: "tky" や "oom" など、DBに存在する値

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;

        if (loginSchool == null) {
            SchoolDao schoolDao = new SchoolDao();
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報がシステムに設定されていません。管理者に確認してください。";
                    request.setAttribute("errorMessage", errorMessage);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp"); // エラー時は成績一覧へ（または専用エラーページへ）
                    dispatcher.forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました。";
                request.setAttribute("errorMessage", errorMessage);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp"); // エラー時は成績一覧へ
                dispatcher.forward(request, response);
                return;
            }
        }

        String entYearStr = request.getParameter("entYear");
        String classNum = request.getParameter("classNum");
        String subjectCd = request.getParameter("subjectCd");
        String studentNo = request.getParameter("studentNo");

        List<TestListSubject> scoreList = null;
        String searchSubjectName = null;

        TestListSubjectDao testListSubjectDao = new TestListSubjectDao();
        SubjectDao subjectDao = new SubjectDao();
        ClassNumDao classNumDao = new ClassNumDao();
        StudentDao studentDao = new StudentDao();

        try {
            // 入学年度リストの準備 (DBから取得)
            List<Student> studentListForYears = studentDao.filter(loginSchool, false);
            Set<Integer> distinctYears = new HashSet<>();
            if (studentListForYears != null) {
                for (Student student : studentListForYears) {
                    distinctYears.add(student.getEntYear());
                }
            }
            List<Integer> entYearSet = new ArrayList<>(distinctYears);
            Collections.sort(entYearSet, Collections.reverseOrder());
            request.setAttribute("entYearSet", entYearSet);

            // クラスリストの準備
            List<String> classNumSet = classNumDao.filter(loginSchool);
            request.setAttribute("classNumSet", classNumSet);

            // 科目リストの準備
            List<Subject> subjects = subjectDao.filter(loginSchool);
            request.setAttribute("subjects", subjects);

            // 検索条件の保持
            request.setAttribute("fEntYear", entYearStr);
            request.setAttribute("fClassNum", classNum);
            request.setAttribute("fSubjectCd", subjectCd);

            // 科目情報での検索
            if (entYearStr != null && !entYearStr.isEmpty() &&
                classNum != null && !classNum.isEmpty() &&
                subjectCd != null && !subjectCd.isEmpty()) {

                int entYear = Integer.parseInt(entYearStr);
                Subject subject = subjectDao.get(subjectCd, loginSchool);
                if (subject != null) {
                    searchSubjectName = subject.getName();
                    scoreList = testListSubjectDao.filter(entYear, classNum, subject, loginSchool);
                    if (scoreList == null || scoreList.isEmpty()) {
                        errorMessage = "指定された条件に合致する成績情報は見つかりませんでした。";
                    }
                } else {
                    errorMessage = "指定された科目 ("+ subjectCd +") が見つかりません。"; //エラーメッセージに科目コード追加
                }
            } else if (studentNo != null && !studentNo.isEmpty()) {
                errorMessage = "学生番号での検索は現在未実装です。科目情報で検索してください。";
            } else if (request.getMethod().equalsIgnoreCase("POST") && // POSTリクエストの場合のみ「全て選択」をチェック
                       ((entYearStr != null && !entYearStr.isEmpty()) ||
                       (classNum != null && !classNum.isEmpty()) ||
                       (subjectCd != null && !subjectCd.isEmpty()))) {
                errorMessage = "科目情報で検索する場合は、入学年度、クラス、科目をすべて選択してください。";
            }
            // GETリクエスト時や、POSTでも全ての検索条件が空の場合は、エラーメッセージは設定しない（infoMessageに任せる）

        } catch (NumberFormatException e) {
            e.printStackTrace();
            errorMessage = "入学年度が不正な形式です。";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "データの取得中にエラーが発生しました：" + e.getMessage();
        }

        request.setAttribute("scoreList", scoreList);
        request.setAttribute("searchSubjectName", searchSubjectName);
        request.setAttribute("errorMessage", errorMessage);

        // POSTリクエストの場合は結果表示ページへフォワード
        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String infoMessage = null;

        if (loginSchool == null) {
            SchoolDao schoolDao = new SchoolDao();
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報（コード: " + DEFAULT_SCHOOL_CD + "）がシステムに設定されていません。管理者に確認してください。";
                    request.setAttribute("errorMessage", errorMessage);
                    // doGetで初期表示するJSPにフォワード (修正点)
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました。";
                request.setAttribute("errorMessage", errorMessage);
                // doGetで初期表示するJSPにフォワード (修正点)
                RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }

        ClassNumDao classNumDao = new ClassNumDao();
        SubjectDao subjectDao = new SubjectDao();
        StudentDao studentDao = new StudentDao();

        try {
            // 入学年度リストの準備 (DBから取得)
            List<Student> studentListForYears = studentDao.filter(loginSchool, false);
            Set<Integer> distinctYears = new HashSet<>();
            if (studentListForYears != null) {
                for (Student student : studentListForYears) {
                    distinctYears.add(student.getEntYear());
                }
            }
            List<Integer> entYearSet = new ArrayList<>(distinctYears);
            Collections.sort(entYearSet, Collections.reverseOrder());
            request.setAttribute("entYearSet", entYearSet);

            // クラスリストの準備
            List<String> classNumSet = classNumDao.filter(loginSchool);
            request.setAttribute("classNumSet", classNumSet);

            // 科目リストの準備
            List<Subject> subjects = subjectDao.filter(loginSchool);
            request.setAttribute("subjects", subjects);

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        if (errorMessage == null) { // エラーがない場合のみ情報メッセージを設定
            infoMessage = "検索条件を選択または入力して検索ボタンをクリックしてください。";
        }
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage); // infoMessageもセット

        // 初期表示は検索条件入力画面 (test_list.jsp) へフォワード (修正点)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list.jsp");
        dispatcher.forward(request, response);
    }
}