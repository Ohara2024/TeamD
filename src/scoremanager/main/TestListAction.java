package scoremanager.main;

import java.io.IOException;
// LocalDateは直接使用しなくなったため、もし他の箇所でも使っていなければ削除可能
// import java.time.LocalDate;
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
import bean.Student; // StudentDaoの新しいfilterメソッドの戻り値がStudentなので必要
import bean.Subject;
import bean.TestListStudent;
import bean.TestListSubject;
import dao.ClassNumDao;
import dao.SchoolDao;
import dao.StudentDao;
import dao.SubjectDao;
import dao.TestListStudentDao;
import dao.TestListSubjectDao;

@WebServlet("/main/TestList.action")
public class TestListAction extends HttpServlet {

    private static final String DEFAULT_SCHOOL_CD = "oom"; // 有効な学校コードに置き換えてください

    // DAOのインスタンス化はメソッド内で行うか、init()で行うのが一般的です
    // ここでは各メソッド内で必要に応じてインスタンス化します。

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String searchMode = null;

        SchoolDao schoolDao = null;
        StudentDao studentDao = null;
        ClassNumDao classNumDao = null;
        SubjectDao subjectDao = null;
        TestListSubjectDao testListSubjectDao = null;
        TestListStudentDao testListStudentDao = null;

        try {
            schoolDao = new SchoolDao();
            studentDao = new StudentDao(); // コンストラクタでNamingExceptionの可能性は減った（内部でRuntimeException）
            classNumDao = new ClassNumDao(); // 同上
            subjectDao = new SubjectDao();   // 同上
            testListSubjectDao = new TestListSubjectDao(); // このDAOのコンストラクタも同様に確認
            testListStudentDao = new TestListStudentDao();   // このDAOのコンストラクタも同様に確認
        } catch (RuntimeException re) { // NamingExceptionをラップしたRuntimeExceptionをキャッチ
            re.printStackTrace();
            errorMessage = "システム設定エラー: " + re.getMessage();
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/scoremanager/main/test_list.jsp").forward(request, response);
            return;
        } catch (Exception e) { // その他の予期せぬ初期化エラー
            e.printStackTrace();
            errorMessage = "システム初期化中に予期せぬエラーが発生しました。";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/scoremanager/main/test_list.jsp").forward(request, response);
            return;
        }


        if (loginSchool == null) {
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報（コード: " + DEFAULT_SCHOOL_CD + "）がシステムに設定されていません。";
                }
            } catch (Exception e) { // SchoolDao.get() はRuntimeExceptionをスローするようになったのでそれでキャッチ
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました: " + e.getMessage();
            }

            if (loginSchool == null) { // loginSchoolが依然としてnullの場合（デフォルト校取得失敗）
                request.setAttribute("errorMessage", errorMessage);
                try {
                    // loginSchoolがnullでも、他のDAOが初期化されていればドロップダウンを試みる
                    prepareDropdownData(request, null, studentDao, classNumDao, subjectDao);
                } catch (Exception innerEx) {
                    innerEx.printStackTrace();
                    if (errorMessage == null) errorMessage = "プルダウンデータの準備中にエラーが発生しました。";
                    else errorMessage += " また、プルダウンデータの準備中にもエラーが発生しました。";
                    request.setAttribute("errorMessage", errorMessage);
                }
                request.getRequestDispatcher("/scoremanager/main/test_list.jsp").forward(request, response);
                return;
            }
        }

        String entYearStr = request.getParameter("entYear");
        String classNum = request.getParameter("classNum");
        String subjectCd = request.getParameter("subjectCd");
        String studentNo = request.getParameter("studentNo");

        try {
            prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
        } catch (Exception e) {
            e.printStackTrace();
            if (errorMessage == null) errorMessage = "プルダウンデータの取得中にエラーが発生しました。";
        }

        request.setAttribute("fEntYear", entYearStr);
        request.setAttribute("fClassNum", classNum);
        request.setAttribute("fSubjectCd", subjectCd);
        request.setAttribute("fStudentNo", studentNo);

        if (studentNo != null && !studentNo.isEmpty()) {
            searchMode = "student";
            try {
                // StudentDao.get() は学校コードも引数に取るように変更された
                Student student = studentDao.get(studentNo, loginSchool.getCd());
                if (student != null) { // 学校コードのチェックはgetメソッド内部で行われるか、別途確認が必要
                    List<TestListStudent> studentScores = testListStudentDao.filter(student);
                    request.setAttribute("student", student);
                    request.setAttribute("studentScores", studentScores);
                } else {
                    if (errorMessage == null) errorMessage = "指定された学生番号の学生が見つかりません。";
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (errorMessage == null) errorMessage = "学生成績の検索中にエラーが発生しました：" + e.getMessage();
            }
        }
        else if (entYearStr != null && !entYearStr.isEmpty() &&
                 classNum != null && !classNum.isEmpty() &&
                 subjectCd != null && !subjectCd.isEmpty()) {
            searchMode = "subject";
            try {
                int entYear = Integer.parseInt(entYearStr);
                // SubjectDao.get() は学校コード(String)を引数に取るように変更された
                Subject subject = subjectDao.get(subjectCd, loginSchool.getCd());

                if (subject != null) {
                    List<TestListSubject> scoreList = testListSubjectDao.filter(entYear, classNum, subject, loginSchool);
                    request.setAttribute("scoreList", scoreList);
                    request.setAttribute("searchSubjectName", subject.getName());
                    if (scoreList == null || scoreList.isEmpty()) {
                        if (errorMessage == null) errorMessage = "指定された条件に合致する成績情報は見つかりませんでした。";
                    }
                } else {
                    if (errorMessage == null) errorMessage = "指定された科目が見つかりません。";
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                if (errorMessage == null) errorMessage = "入学年度が不正な形式です。";
            } catch (Exception e) {
                e.printStackTrace();
                if (errorMessage == null) errorMessage = "科目別成績の検索中にエラーが発生しました：" + e.getMessage();
            }
        }
        else {
            if (request.getMethod().equalsIgnoreCase("POST")) {
                 if (errorMessage == null) errorMessage = "検索条件を正しく入力または選択してください。";
            }
        }

        request.setAttribute("searchMode", searchMode);
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/scoremanager/main/test_list_student.jsp").forward(request, response);
    }

    private void prepareDropdownData(HttpServletRequest request, School loginSchool,
                                     StudentDao studentDao, ClassNumDao classNumDao, SubjectDao subjectDao) throws Exception {
        // loginSchool が null の場合は、学校コードも null になる。各DAOのメソッドがnullを扱えるか確認。
        // 新しいDAOは schoolCd (String) を引数に取るものが増えた。
        String schoolCdForFilter = (loginSchool != null) ? loginSchool.getCd() : null;

        if (schoolCdForFilter == null && loginSchool != null) { // loginSchoolはあるがCDが取れない異常ケース
             System.err.println("prepareDropdownData: loginSchool object exists but its CD is null.");
             request.setAttribute("entYearSet", new ArrayList<Integer>());
             request.setAttribute("classNumSet", new ArrayList<String>());
             request.setAttribute("subjects", new ArrayList<Subject>());
             return;
        }
         if (schoolCdForFilter == null) { // ログイン校情報が全くない場合
             System.err.println("prepareDropdownData: No school information available to filter dropdowns.");
             request.setAttribute("entYearSet", new ArrayList<Integer>());
             request.setAttribute("classNumSet", new ArrayList<String>());
             request.setAttribute("subjects", new ArrayList<Subject>());
             return;
         }


        if (studentDao == null || classNumDao == null || subjectDao == null) {
             request.setAttribute("entYearSet", new ArrayList<Integer>());
             request.setAttribute("classNumSet", new ArrayList<String>());
             request.setAttribute("subjects", new ArrayList<Subject>());
            System.err.println("prepareDropdownData: One or more DAOs are null. studentDao=" + studentDao +
                               ", classNumDao=" + classNumDao + ", subjectDao=" + subjectDao);
            return;
        }

        // StudentDao.getAllEntYears(schoolCd) を使用
        List<Integer> entYearSet = studentDao.getAllEntYears(schoolCdForFilter);
        request.setAttribute("entYearSet", entYearSet);

        // ClassNumDao.filter(schoolCd) を使用
        List<String> classNumSet = classNumDao.filter(schoolCdForFilter);
        request.setAttribute("classNumSet", classNumSet);

        // SubjectDao.filter(school) は School オブジェクトを引数に取るままなので、loginSchool を渡す
        // もし SubjectDao.filter も schoolCd を取るように変更されたなら、そちらに合わせる
        List<Subject> subjects = subjectDao.filter(loginSchool); // ★元のSubjectDaoはSchool型引数だった。新しいSubjectDaoを確認
        request.setAttribute("subjects", subjects);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String infoMessage = null;

        SchoolDao schoolDao = null;
        StudentDao studentDao = null;
        ClassNumDao classNumDao = null;
        SubjectDao subjectDao = null;

        try {
            schoolDao = new SchoolDao();
            studentDao = new StudentDao();
            classNumDao = new ClassNumDao();
            subjectDao = new SubjectDao();
        } catch (RuntimeException re) {
            re.printStackTrace();
            errorMessage = "システム設定エラー: " + re.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "システム初期化中に予期せぬエラーが発生しました。";
        }

        if (loginSchool == null && schoolDao != null) { // schoolDaoが初期化成功している場合のみデフォルト校取得を試みる
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null && errorMessage == null) { // エラーがまだなければ設定
                    errorMessage = "デフォルトの学校情報（コード: " + DEFAULT_SCHOOL_CD + "）がシステムに設定されていません。";
                }
            } catch (Exception e) { // SchoolDao.get() はRuntimeExceptionをスロー
                e.printStackTrace();
                if (errorMessage == null) errorMessage = "学校情報の取得中にエラーが発生しました: " + e.getMessage();
            }
        } else if (schoolDao == null && errorMessage == null) {
             errorMessage = "学校情報サービスを利用できません。";
        }


        try {
            // loginSchoolがnullの場合でも、prepareDropdownDataはそれを考慮する
            prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
        } catch (Exception e) {
            e.printStackTrace();
            if (errorMessage == null) errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        if (errorMessage == null && loginSchool != null) {
            infoMessage = "検索条件を選択または入力して検索ボタンをクリックしてください。";
        } else if (loginSchool == null && errorMessage == null) {
            errorMessage = "利用可能な学校情報がありません。システム管理者に連絡してください。";
        }

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage);
        request.setAttribute("searchMode", null);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/test_list.jsp");
        dispatcher.forward(request, response);
    }
}