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
// → loginSchoolは (School)session.getAttribute("loginSchool"); でキャストされており、bean.Schoolはインポート済なのでSchoolDao自体のインポートはここでは不要でした。
import dao.SubjectDao;
import dao.TestListSubjectDao;

@WebServlet("/main/TestList.action")
public class TestListAction extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");

        if (loginSchool == null) {
            request.setAttribute("errorMessage", "学校情報が取得できませんでした。再度ログインしてください。");
            // login.jspの正確なパスが不明なため、ルート相対パスとしています。
            // プロジェクト構成に合わせて修正してください。例: /TeamD/login.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("/login/login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String entYearStr = request.getParameter("entYear");
        String classNum = request.getParameter("classNum");
        String subjectCd = request.getParameter("subjectCd");
        String studentNo = request.getParameter("studentNo"); // 学生番号での検索用

        List<TestListSubject> scoreList = null;
        String searchSubjectName = null;
        String errorMessage = null;

        TestListSubjectDao testListSubjectDao = new TestListSubjectDao();
        SubjectDao subjectDao = new SubjectDao();
        ClassNumDao classNumDao = new ClassNumDao();
        // SchoolDao schoolDao = new SchoolDao(); // doGetで学校名などを取得しない限り、現時点のdoPostでは未使用

        try {
            // ドロップダウンリスト用のデータを準備 (doGetと共通化またはここで取得)
            int currentYear = LocalDate.now().getYear();
            List<Integer> entYearSet = new ArrayList<>();
            for (int i = currentYear - 10; i <= currentYear + 1; i++) {
                entYearSet.add(i);
            }
            request.setAttribute("entYearSet", entYearSet);

            List<String> classNumSet = classNumDao.filter(loginSchool);
            request.setAttribute("classNumSet", classNumSet);

            List<Subject> subjects = subjectDao.filter(loginSchool);
            request.setAttribute("subjects", subjects);

            // 検索条件の保持
            request.setAttribute("fEntYear", entYearStr);
            request.setAttribute("fClassNum", classNum);
            request.setAttribute("fSubjectCd", subjectCd);

            // 科目情報での検索の場合
            if (entYearStr != null && !entYearStr.isEmpty() &&
                classNum != null && !classNum.isEmpty() &&
                subjectCd != null && !subjectCd.isEmpty()) {

                int entYear = Integer.parseInt(entYearStr);

                Subject subject = subjectDao.get(subjectCd, loginSchool);
                if (subject != null) {
                    searchSubjectName = subject.getName();
                    scoreList = testListSubjectDao.filter(entYear, classNum, subject, loginSchool); //
                    if (scoreList == null || scoreList.isEmpty()) {
                        errorMessage = "指定された条件に合致する成績情報は見つかりませんでした。";
                    }
                } else {
                    errorMessage = "指定された科目が見つかりません。";
                }

            }
            // 学生番号での検索（今回は対象外としますが、必要であればここにロジックを追加）
            else if (studentNo != null && !studentNo.isEmpty()) {
                // TestListStudentDaoを使用して学生個人の成績リストを取得するロジックをここに追加
                // (例) TestListStudentDao testListStudentDao = new TestListStudentDao();
                //      StudentDao studentDao = new StudentDao();
                //      Student student = studentDao.get(studentNo);
                //      if (student != null && student.getSchool().getCd().equals(loginSchool.getCd())) {
                //          List<TestListStudent> studentScores = testListStudentDao.filter(student);
                //          request.setAttribute("studentScores", studentScores); // 別途JSPで表示
                //          request.setAttribute("searchStudent", student);
                //      } else {
                //          errorMessage = "指定された学生番号の学生が見つからないか、学校が異なります。";
                //      }
                // 今回は科目検索の結果と同じ 'scoreList' を使う想定ではないため、別の属性名でセットするか、
                // JSP側で表示を分ける必要があります。
                // ここでは、提供されたコードに基づき、科目検索を主とし、学生番号検索はメッセージのみとします。
                errorMessage = "学生番号での検索は現在未実装です。科目情報で検索してください。";
            } else if ((entYearStr != null && !entYearStr.isEmpty()) || // いずれかのフィールドが入力されたが全てではない場合
                       (classNum != null && !classNum.isEmpty()) ||
                       (subjectCd != null && !subjectCd.isEmpty())) {
                errorMessage = "科目情報で検索する場合は、入学年度、クラス、科目をすべて選択してください。";
            }
            // 初期表示時や、検索ボタンが押されたが何も入力/選択されていない場合はメッセージなし
            // (doGetで初期メッセージが設定されるため、ここでは敢えて設定しない)

        } catch (NumberFormatException e) {
            e.printStackTrace();
            errorMessage = "入学年度が不正な形式です。";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "データの取得中にエラーが発生しました：" + e.getMessage();
        }

        request.setAttribute("scoreList", scoreList);
        request.setAttribute("searchSubjectName", searchSubjectName); // 検索した科目名をJSPで表示する場合
        request.setAttribute("errorMessage", errorMessage);

        // 結果表示JSPにフォワード (ユーザー指定のパスに修正)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");

        if (loginSchool == null) {
            request.setAttribute("errorMessage", "学校情報が取得できませんでした。再度ログインしてください。");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp"); // ログインページへのパスに修正
            dispatcher.forward(request, response);
            return;
        }

        ClassNumDao classNumDao = new ClassNumDao();
        SubjectDao subjectDao = new SubjectDao();
        String errorMessage = null;

        try {
            int currentYear = LocalDate.now().getYear();
            List<Integer> entYearSet = new ArrayList<>();
            for (int i = currentYear - 10; i <= currentYear + 1; i++) {
                entYearSet.add(i);
            }
            request.setAttribute("entYearSet", entYearSet);

            List<String> classNumSet = classNumDao.filter(loginSchool);
            request.setAttribute("classNumSet", classNumSet);

            List<Subject> subjects = subjectDao.filter(loginSchool);
            request.setAttribute("subjects", subjects);

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        // 初期表示時は、検索条件が未入力である旨のメッセージは `test_list.jsp` 側で表示するため、
        // サーブレットからは特に設定しないか、必要に応じて設定します。
        // 提供されたコードでは TestList.jsp 側にメッセージがあったため、ここではクリアまたは上書きしない。
        // ただし、doPostから遷移してきた際にエラーがない場合、エラーメッセージはnullのままとなる。
        // doGetで明示的にメッセージを設定する場合
        if (errorMessage == null) { // 他のエラーがなければ
             request.setAttribute("infoMessage", "検索条件を入力または選択して検索ボタンを押してください。");
        }
        request.setAttribute("errorMessage", errorMessage);


        // 初期表示は検索条件入力画面 (ユーザー指定のパス /TeamD/WebContent/seiseki/test_list.jsp)
        // doGetで直接科目別成績一覧(/seiseki/test_list_student.jsp)を表示するのか、
        // それとも検索入力画面(/seiseki/test_list.jsp)を表示するのか、
        // 元のコードでは TestList.action (このサーブレット) の doGet は score_list_subject.jsp (成績一覧画面) にフォワードしていました。
        // しかし、一般的に初期アクセス(doGet)では検索条件入力画面を表示することが多いため、
        // ここでは test_list.jsp (検索条件入力画面) にフォワードするように変更するのが自然かもしれません。
        //
        // 元のTestListAction.javaのdoGetは、ドロップダウンの準備をしてから
        // `RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/score_list_subject.jsp");`
        // へフォワードしていました。これは、成績一覧表示画面(score_list_subject.jsp / 現 test_list_student.jsp)を
        // 初期表示し、その画面内で検索条件を再選択して同じ画面にPOSTするUIを想定しているようです。
        // この動作を踏襲します。
        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list_student.jsp");
        dispatcher.forward(request, response);
    }
}