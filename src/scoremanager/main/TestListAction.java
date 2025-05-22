package scoremanager.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException; // NamingExceptionのインポートを追加
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

        SchoolDao schoolDao = null;
        StudentDao studentDao = null;
        ClassNumDao classNumDao = null;
        SubjectDao subjectDao = null;
        TestListSubjectDao testListSubjectDao = null;
        TestListStudentDao testListStudentDao = null;

        try {
            schoolDao = new SchoolDao();
            studentDao = new StudentDao();
            classNumDao = new ClassNumDao();
            subjectDao = new SubjectDao(); // NamingExceptionをスローする可能性あり
            testListSubjectDao = new TestListSubjectDao();
            testListStudentDao = new TestListStudentDao();
        } catch (NamingException ne) {
            ne.printStackTrace();
            errorMessage = "システム設定エラー: データベース接続情報の取得に失敗しました。(NE)";
            // prepareDropdownData が null を扱えるように、エラー時は DAO を null にしておく
            schoolDao = null; studentDao = null; classNumDao = null; subjectDao = null;
            testListStudentDao = null; testListStudentDao = null;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "システム初期化中に予期せぬエラーが発生しました。";
            schoolDao = null; studentDao = null; classNumDao = null; subjectDao = null;
            testListStudentDao = null; testListStudentDao = null;
        }


        if (loginSchool == null) {
            try {
                if (schoolDao != null) { // schoolDaoが初期化されているか確認
                    loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                } else if (errorMessage == null) { // schoolDaoがnullで、まだ主要なエラーメッセージがない場合
                    errorMessage = "学校情報サービスを利用できません。";
                }

                if (loginSchool == null && errorMessage == null) { // デフォルト学校情報も取得できなかった場合
                    errorMessage = "デフォルトの学校情報がシステムに設定されていません。";
                }

                if (errorMessage != null && loginSchool == null) { // エラーがあり、loginSchoolが解決できなかった場合
                    request.setAttribute("errorMessage", errorMessage);
                    try {
                        // loginSchool が null の場合でも、他のDAOが初期化されていればドロップダウンを試みる
                        prepareDropdownData(request, null, studentDao, classNumDao, subjectDao);
                    } catch (Exception innerEx) {
                        innerEx.printStackTrace();
                        if (errorMessage == null) errorMessage = "プルダウンデータの準備中にエラーが発生しました。";
                        else errorMessage += " また、プルダウンデータの準備中にもエラーが発生しました。";
                        request.setAttribute("errorMessage", errorMessage);
                    }
                    request.getRequestDispatcher("/scoremanager/main/test_list.jsp").forward(request, response); // ★パス変更
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (errorMessage == null) errorMessage = "学校情報の取得中にエラーが発生しました。";
                request.setAttribute("errorMessage", errorMessage);
                try {
                    prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
                } catch (Exception innerEx) {
                    innerEx.printStackTrace();
                    errorMessage = (errorMessage == null ? "" : errorMessage + " ");
                    errorMessage += "学校情報取得エラー後、プルダウンデータの準備中にもエラーが発生しました。";
                    request.setAttribute("errorMessage", errorMessage);
                }
                request.getRequestDispatcher("/scoremanager/main/test_list.jsp").forward(request, response); // ★パス変更
                return;
            }
        }

        String entYearStr = request.getParameter("entYear");
        String classNum = request.getParameter("classNum");
        String subjectCd = request.getParameter("subjectCd");
        String studentNo = request.getParameter("studentNo");

        try {
            // loginSchoolがこの時点で確定している想定でprepareDropdownDataを呼び出す
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
                if (studentDao != null && loginSchool != null) {
                    Student student = studentDao.get(studentNo);
                    if (student != null && student.getSchool().getCd().equals(loginSchool.getCd())) {
                        if (testListStudentDao != null) {
                            List<TestListStudent> studentScores = testListStudentDao.filter(student);
                            request.setAttribute("student", student);
                            request.setAttribute("studentScores", studentScores);
                        } else if (errorMessage == null) {
                            errorMessage = "成績情報サービスが利用できません。(student)";
                        }
                    } else {
                        if (errorMessage == null) errorMessage = "指定された学生番号の学生が見つからないか、学校が異なります。";
                    }
                } else if (errorMessage == null) {
                     errorMessage = "学生情報または学校情報サービスが利用できません。";
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
                Subject subject = null;
                if (subjectDao != null && loginSchool != null) { // subjectDao と loginSchool の null チェック
                    subject = subjectDao.get(subjectCd, loginSchool.getCd()); // ★修正: loginSchool.getCd() を使用
                } else if (errorMessage == null) {
                    errorMessage = "科目情報または学校情報サービスが利用できません。";
                }

                if (subject != null) {
                    if (testListSubjectDao != null) {
                        List<TestListSubject> scoreList = testListSubjectDao.filter(entYear, classNum, subject, loginSchool);
                        request.setAttribute("scoreList", scoreList);
                        request.setAttribute("searchSubjectName", subject.getName());
                        if (scoreList == null || scoreList.isEmpty()) {
                            if (errorMessage == null) errorMessage = "指定された条件に合致する成績情報は見つかりませんでした。";
                        }
                    } else if (errorMessage == null) {
                        errorMessage = "成績情報サービスが利用できません。(subject)";
                    }
                } else {
                    if (errorMessage == null && subjectDao != null) errorMessage = "指定された科目が見つかりません。";
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
        request.getRequestDispatcher("/scoremanager/main/test_list_student.jsp").forward(request, response); // ★パス変更
    }

    private void prepareDropdownData(HttpServletRequest request, School loginSchool,
                                     StudentDao studentDao, ClassNumDao classNumDao, SubjectDao subjectDao) throws Exception {
        if (loginSchool == null || studentDao == null || classNumDao == null || subjectDao == null) {
             request.setAttribute("entYearSet", new ArrayList<Integer>());
             request.setAttribute("classNumSet", new ArrayList<String>());
             request.setAttribute("subjects", new ArrayList<Subject>());
            // エラーメッセージは呼び出し元で設定するため、ここではログ出力に留める
            System.err.println("prepareDropdownData: loginSchool or one of the DAOs is null. loginSchool=" + loginSchool +
                               ", studentDao=" + studentDao + ", classNumDao=" + classNumDao + ", subjectDao=" + subjectDao);
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

        SchoolDao schoolDao = null;
        StudentDao studentDao = null;
        ClassNumDao classNumDao = null;
        SubjectDao subjectDao = null;

        try {
            schoolDao = new SchoolDao();
            studentDao = new StudentDao();
            classNumDao = new ClassNumDao();
            subjectDao = new SubjectDao(); // NamingExceptionをスローする可能性あり
        } catch (NamingException ne) {
            ne.printStackTrace();
            errorMessage = "システム設定エラー: データベース接続情報の取得に失敗しました。(NE)";
            schoolDao = null; studentDao = null; classNumDao = null; subjectDao = null;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "システム初期化中に予期せぬエラーが発生しました。";
            schoolDao = null; studentDao = null; classNumDao = null; subjectDao = null;
        }

        if (loginSchool == null) {
            try {
                if (schoolDao != null) { // schoolDaoが初期化されているか確認
                    loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                } else if (errorMessage == null) { // schoolDaoがnullで、まだ主要なエラーメッセージがない場合
                     errorMessage = "学校情報サービスを利用できません。";
                }
                if (loginSchool == null && errorMessage == null) {
                    errorMessage = "デフォルトの学校情報（コード: " + DEFAULT_SCHOOL_CD + "）がシステムに設定されていません。";
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (errorMessage == null) errorMessage = "学校情報の取得中にエラーが発生しました。";
            }
        }

        try {
            prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
        } catch (Exception e) {
            e.printStackTrace();
            if (errorMessage == null) errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        if (errorMessage == null && loginSchool != null) { // loginSchool が null の場合は infoMessage を出さない方が自然か検討
            infoMessage = "検索条件を選択または入力して検索ボタンをクリックしてください。";
        } else if (loginSchool == null && errorMessage == null) { // loginSchoolも取れず、特にエラーもない場合（デフォルトCDもないなど）
            errorMessage = "利用可能な学校情報がありません。システム管理者に連絡してください。";
        }


        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage);
        request.setAttribute("searchMode", null);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/test_list.jsp"); // ★パス変更
        dispatcher.forward(request, response);
    }
}