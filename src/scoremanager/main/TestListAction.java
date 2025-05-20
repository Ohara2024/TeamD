package scoremanager.main;

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String searchMode = null;

        SchoolDao schoolDao = new SchoolDao();
        StudentDao studentDao = new StudentDao();
        ClassNumDao classNumDao = new ClassNumDao();
        SubjectDao subjectDao = new SubjectDao();
        TestListSubjectDao testListSubjectDao = new TestListSubjectDao();
        TestListStudentDao testListStudentDao = new TestListStudentDao();

        if (loginSchool == null) {
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報がシステムに設定されていません。";
                    request.setAttribute("errorMessage", errorMessage);
                    // prepareDropdownData を try-catch で囲む
                    try {
                        prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
                    } catch (Exception innerEx) {
                        innerEx.printStackTrace();
                        if (errorMessage == null) errorMessage = "プルダウンデータの準備中にエラーが発生しました。";
                        request.setAttribute("errorMessage", errorMessage); // エラーメッセージを更新または設定
                    }
                    request.getRequestDispatcher("/seiseki/test_list.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました。";
                request.setAttribute("errorMessage", errorMessage);
                // prepareDropdownData を try-catch で囲む
                try {
                    // loginSchool がこの時点で null の可能性があるため、prepareDropdownData がそれを処理できるか確認
                    prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
                } catch (Exception innerEx) {
                    innerEx.printStackTrace();
                    // エラーメッセージは既に設定されているか、ここで上書き
                    errorMessage = "学校情報取得エラー後、プルダウンデータの準備中にもエラーが発生しました。";
                    request.setAttribute("errorMessage", errorMessage);
                }
                request.getRequestDispatcher("/seiseki/test_list.jsp").forward(request, response);
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
            // このエラーが致命的なら、ここでフォワードしてreturnも検討
        }

        request.setAttribute("fEntYear", entYearStr);
        request.setAttribute("fClassNum", classNum);
        request.setAttribute("fSubjectCd", subjectCd);
        request.setAttribute("fStudentNo", studentNo);

        if (studentNo != null && !studentNo.isEmpty()) {
            searchMode = "student";
            try {
                Student student = studentDao.get(studentNo);
                if (student != null && student.getSchool().getCd().equals(loginSchool.getCd())) {
                    List<TestListStudent> studentScores = testListStudentDao.filter(student);
                    request.setAttribute("student", student);
                    request.setAttribute("studentScores", studentScores);
                } else {
                    if (errorMessage == null) errorMessage = "指定された学生番号の学生が見つからないか、学校が異なります。";
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
                Subject subject = subjectDao.get(subjectCd, loginSchool);
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
        request.getRequestDispatcher("/seiseki/test_list_student.jsp").forward(request, response);
    }

    private void prepareDropdownData(HttpServletRequest request, School loginSchool,
                                     StudentDao studentDao, ClassNumDao classNumDao, SubjectDao subjectDao) throws Exception { // このメソッドは Exception をスローする
        if (loginSchool == null || studentDao == null || classNumDao == null || subjectDao == null) {
             request.setAttribute("entYearSet", new ArrayList<Integer>());
             request.setAttribute("classNumSet", new ArrayList<String>());
             request.setAttribute("subjects", new ArrayList<Subject>());
            System.err.println("prepareDropdownData: loginSchool or one of the DAOs is null. loginSchool=" + loginSchool);
            // loginSchool が null の場合に Exception をスローして、呼び出し元でエラー処理させることもできる
            // throw new ServletException("prepareDropdownData: Required objects are null.");
            return;
        }
        List<Student> studentListForYears = studentDao.filter(loginSchool, false);
        Set<Integer> distinctYears = new HashSet<>();
        if (studentListForYears != null) {
            for (Student s : studentListForYears) {
                distinctYears.add(s.getEntYear());
            }
        }
        List<Integer> entYearSet = new ArrayList<>(distinctYears);
        Collections.sort(entYearSet, Collections.reverseOrder());
        request.setAttribute("entYearSet", entYearSet);

        List<String> classNumSet = classNumDao.filter(loginSchool);
        request.setAttribute("classNumSet", classNumSet);

        List<Subject> subjects = subjectDao.filter(loginSchool);
        request.setAttribute("subjects", subjects);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String infoMessage = null;

        SchoolDao schoolDao = new SchoolDao();
        StudentDao studentDao = new StudentDao();
        ClassNumDao classNumDao = new ClassNumDao();
        SubjectDao subjectDao = new SubjectDao();

        if (loginSchool == null) {
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報（コード: " + DEFAULT_SCHOOL_CD + "）がシステムに設定されていません。";
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました。";
            }
        }

        try {
            prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
        } catch (Exception e) {
            e.printStackTrace();
            if (errorMessage == null) errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        if (errorMessage == null) {
            infoMessage = "検索条件を選択または入力して検索ボタンをクリックしてください。";
        }
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage);
        request.setAttribute("searchMode", null);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/seiseki/test_list.jsp");
        dispatcher.forward(request, response);
    }
}