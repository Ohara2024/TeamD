package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Subject;
import bean.TestListSubject;
import dao.ClassNumDao;
import dao.SchoolDao; // SchoolDao を使用してデフォルトの学校情報を取得するためにインポート
import dao.SubjectDao;
import dao.TestListSubjectDao;

@WebServlet("/main/TestList.action")
public class TestListAction extends HttpServlet {

    // ★★★ 重要: ログインなしで参照する場合、どの学校の情報を表示するかのデフォルト学校コード ★★★
    // ★★★ この値を、データベースに存在する実際の有効な学校コードに置き換えてください。 ★★★
    private static final String DEFAULT_SCHOOL_CD = "YOUR_DEFAULT_SCHOOL_CODE_HERE"; // 例: "101" など

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null; // エラーメッセージ変数をメソッド冒頭に移動

        // ログインしていなくても参照できるようにするための変更
        if (loginSchool == null) {
            // セッションに学校情報がない場合、デフォルトの学校情報を取得する
            SchoolDao schoolDao = new SchoolDao();
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    // 指定したデフォルト学校コードの学校が見つからない場合
                    errorMessage = "デフォルトの学校情報がシステムに設定されていません。管理者に確認してください。";
                    request.setAttribute("errorMessage", errorMessage);
                    // ★エラーページを作成し、そちらへフォワードすることを推奨します (例: /error.jsp)
                    // ここではひとまず成績一覧表示JSPへフォワードしますが、表示がおかしくなる可能性があります。
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました。";
                request.setAttribute("errorMessage", errorMessage);
                // ★エラーページへフォワード
                RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
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
        // String errorMessage = null; // メソッド冒頭に移動済み

        TestListSubjectDao testListSubjectDao = new TestListSubjectDao();
        SubjectDao subjectDao = new SubjectDao();
        ClassNumDao classNumDao = new ClassNumDao();

        try {
            int currentYear = LocalDate.now().getYear();
            List<Integer> entYearSet = new ArrayList<>();
            for (int i = currentYear - 10; i <= currentYear + 1; i++) {
                entYearSet.add(i);
            }
            request.setAttribute("entYearSet", entYearSet);

            // loginSchool が null でないことを保証（上記でデフォルト設定処理済み）
            List<String> classNumSet = classNumDao.filter(loginSchool);
            request.setAttribute("classNumSet", classNumSet);

            List<Subject> subjects = subjectDao.filter(loginSchool);
            request.setAttribute("subjects", subjects);

            request.setAttribute("fEntYear", entYearStr);
            request.setAttribute("fClassNum", classNum);
            request.setAttribute("fSubjectCd", subjectCd);

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
                    errorMessage = "指定された科目が見つかりません。";
                }
            } else if (studentNo != null && !studentNo.isEmpty()) {
                errorMessage = "学生番号での検索は現在未実装です。科目情報で検索してください。";
            } else if ((entYearStr != null && !entYearStr.isEmpty()) ||
                       (classNum != null && !classNum.isEmpty()) ||
                       (subjectCd != null && !subjectCd.isEmpty())) {
                errorMessage = "科目情報で検索する場合は、入学年度、クラス、科目をすべて選択してください。";
            }

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

        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null; // エラーメッセージ変数をメソッド冒頭に移動
        String infoMessage = null; // 情報メッセージ用

        // ログインしていなくても参照できるようにするための変更
        if (loginSchool == null) {
            // セッションに学校情報がない場合、デフォルトの学校情報を取得する
            SchoolDao schoolDao = new SchoolDao();
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報がシステムに設定されていません。管理者に確認してください。";
                    request.setAttribute("errorMessage", errorMessage);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました。";
                request.setAttribute("errorMessage", errorMessage);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }

        ClassNumDao classNumDao = new ClassNumDao();
        SubjectDao subjectDao = new SubjectDao();
        // String errorMessage = null; // メソッド冒頭に移動済み

        try {
            int currentYear = LocalDate.now().getYear();
            List<Integer> entYearSet = new ArrayList<>();
            for (int i = currentYear - 10; i <= currentYear + 1; i++) {
                entYearSet.add(i);
            }
            request.setAttribute("entYearSet", entYearSet);

            // loginSchool が null でないことを保証（上記でデフォルト設定処理済み）
            List<String> classNumSet = classNumDao.filter(loginSchool);
            request.setAttribute("classNumSet", classNumSet);

            List<Subject> subjects = subjectDao.filter(loginSchool);
            request.setAttribute("subjects", subjects);

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        if (errorMessage == null) {
            infoMessage = "検索条件を入力または選択して検索ボタンを押してください。";
        }
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage);


        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
        dispatcher.forward(request, response);
    }
}