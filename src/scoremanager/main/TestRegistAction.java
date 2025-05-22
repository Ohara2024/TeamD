package scoremanager.main;

import java.io.IOException;
// LocalDateは直接使用しなくなったため、もし他の箇所でも使っていなければ削除可能
// import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Student;
import bean.Subject;
import bean.Test;
import dao.ClassNumDao;
import dao.SchoolDao;
import dao.StudentDao;
import dao.SubjectDao;
import dao.TestDao;

@WebServlet("/main/TestRegist.action")
public class TestRegistAction extends HttpServlet {

    private static final String DEFAULT_SCHOOL_CD = "oom"; // ご自身の有効な学校コードに置き換えてください

    /**
     * GETリクエストを処理します（初期表示）。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String infoMessage = null;

        SchoolDao schoolDao = null;
        StudentDao studentDao = null;
        ClassNumDao classNumDao = null;
        SubjectDao subjectDao = null;

        try {
            schoolDao = new SchoolDao(); // コンストラクタで RuntimeException がスローされる可能性
            studentDao = new StudentDao(); // 同上
            classNumDao = new ClassNumDao(); // 同上
            subjectDao = new SubjectDao();   // 同上
        } catch (RuntimeException re) { // DAO初期化時のRuntimeExceptionをキャッチ
            re.printStackTrace();
            errorMessage = "データベース接続設定エラー: " + re.getMessage();
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/scoremanager/main/test_regist.jsp").forward(request, response);
            return;
        } catch (Exception e) { // その他の予期せぬ初期化エラー
            e.printStackTrace();
            errorMessage = "システム初期化中に予期せぬエラーが発生しました。";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/scoremanager/main/test_regist.jsp").forward(request, response);
            return;
        }


        if (loginSchool == null) {
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報（コード: " + DEFAULT_SCHOOL_CD + "）がシステムに設定されていません。";
                }
            } catch (Exception e) { // SchoolDao.get() は RuntimeException をスローする可能性あり
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました: " + e.getMessage();
            }
        }

        try {
            prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
        } catch (Exception e) {
            e.printStackTrace();
            if (errorMessage == null) errorMessage = "初期データの取得中にエラーが発生しました：" + e.getMessage();
        }

        if (errorMessage == null && loginSchool != null) { // loginSchoolが取得できた場合のみinfoMessageを設定
            infoMessage = "検索条件を指定して検索ボタンを押してください。";
        } else if (loginSchool == null && errorMessage == null) { // デフォルト校も取得できなかった場合
             errorMessage = "利用可能な学校情報がありません。システム管理者に連絡してください。";
        }

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage);

        request.getRequestDispatcher("/scoremanager/main/test_regist.jsp").forward(request, response);
    }

    /**
     * POSTリクエストを処理します（検索処理、または成績登録/更新処理）。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        School loginSchool = (School) session.getAttribute("loginSchool");
        String errorMessage = null;
        String infoMessage = null;
        String forwardPath = "/scoremanager/main/test_regist.jsp";

        SchoolDao schoolDao = null;
        StudentDao studentDao = null;
        ClassNumDao classNumDao = null;
        SubjectDao subjectDao = null;
        TestDao testDao = null; // TestDaoもここで初期化

        try {
            schoolDao = new SchoolDao();
            studentDao = new StudentDao();
            classNumDao = new ClassNumDao();
            subjectDao = new SubjectDao();
            testDao = new TestDao(); // TestDaoも初期化
        } catch (RuntimeException re) {
            re.printStackTrace();
            errorMessage = "データベース接続設定エラー: " + re.getMessage();
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher(forwardPath).forward(request, response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "システム初期化中に予期せぬエラーが発生しました。";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher(forwardPath).forward(request, response);
            return;
        }

        if (loginSchool == null) {
            try {
                loginSchool = schoolDao.get(DEFAULT_SCHOOL_CD);
                if (loginSchool == null) {
                    errorMessage = "デフォルトの学校情報がシステムに設定されていません。";
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "学校情報の取得中にエラーが発生しました: " + e.getMessage();
            }
            // loginSchool が null の場合でも、エラーメッセージを設定した上でプルダウン準備を試みる
            try {
                prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
            } catch (Exception prepEx) {
                prepEx.printStackTrace();
                if (errorMessage == null) errorMessage = "プルダウンデータの準備中にエラーが発生しました。";
                else errorMessage += " また、プルダウンデータの準備中にもエラーが発生しました。";
            }
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher(forwardPath).forward(request, response); // forwardPathを使用
            return;
        }

        try {
            prepareDropdownData(request, loginSchool, studentDao, classNumDao, subjectDao);
        } catch (Exception e) {
            e.printStackTrace();
            if (errorMessage == null) errorMessage = "プルダウンデータの取得中にエラーが発生しました。";
        }

        String fEntYearStr = request.getParameter("fEntYear");
        String fClassNum = request.getParameter("fClassNum");
        String fSubjectCd = request.getParameter("fSubjectCd");
        String fTestNoStr = request.getParameter("fTestNo");
        String action = request.getParameter("action");

        request.setAttribute("fEntYear", fEntYearStr);
        request.setAttribute("fClassNum", fClassNum);
        request.setAttribute("fSubjectCd", fSubjectCd);
        request.setAttribute("fTestNo", fTestNoStr);

        if ("search_students_for_score".equals(action)) {
            if (fEntYearStr != null && !fEntYearStr.isEmpty() &&
                fClassNum != null && !fClassNum.isEmpty() &&
                fSubjectCd != null && !fSubjectCd.isEmpty() &&
                fTestNoStr != null && !fTestNoStr.isEmpty()) {
                try {
                    int entYear = Integer.parseInt(fEntYearStr);
                    int testNo = Integer.parseInt(fTestNoStr);
                    Subject subject = subjectDao.get(fSubjectCd, loginSchool.getCd());

                    if (subject != null) {
                        // StudentDao.filterの呼び出し方を新しいシグネチャに合わせる
                        // (Integer entYear, String classNum, Boolean isAttend, String schoolCd)
                        // 在学中(isAttend=true)の学生を対象とする
                        List<Student> students = studentDao.filter(entYear, fClassNum, true, loginSchool.getCd());
                        Map<String, Integer> pointsMap = new HashMap<>();
                        if (students != null && !students.isEmpty()) {
                            for (Student student : students) {
                                Test test = testDao.get(student, subject, loginSchool, testNo);
                                if (test != null) {
                                    pointsMap.put(student.getNo(), test.getPoint());
                                } else {
                                    pointsMap.put(student.getNo(), null); // JSP側でvalueがnullの場合の処理が必要な場合がある
                                }
                            }
                        }
                        request.setAttribute("students", students);
                        request.setAttribute("pointsMap", pointsMap);
                        request.setAttribute("searchedSubject", subject);
                        request.setAttribute("searchedTestNo", testNo);
                        if (students == null || students.isEmpty()) {
                           if (infoMessage == null) infoMessage = "指定された条件に合致する学生情報は見つかりませんでした。";
                        }
                    } else {
                        if (errorMessage == null) errorMessage = "指定された科目が存在しません。";
                    }
                } catch (NumberFormatException e) {
                    if (errorMessage == null) errorMessage = "入学年度または回数が不正な形式です。";
                } catch (Exception e) {
                    e.printStackTrace();
                    if (errorMessage == null) errorMessage = "学生または成績情報の検索中にエラーが発生しました: " + e.getMessage();
                }
            } else {
                if (errorMessage == null) errorMessage = "検索条件（入学年度、クラス、科目、回数）をすべて選択してください。";
            }
        } else if ("register_scores".equals(action)) {
            String hiddenSubjectCd = request.getParameter("hidden_fSubjectCd");
            String hiddenTestNoStr = request.getParameter("hidden_fTestNo");
            String[] studentNos = request.getParameterValues("studentNos");

            if (hiddenSubjectCd != null && hiddenTestNoStr != null && studentNos != null && loginSchool != null) {
                try {
                    Subject subject = subjectDao.get(hiddenSubjectCd, loginSchool.getCd());
                    int testNo = Integer.parseInt(hiddenTestNoStr);
                    List<Test> testsToSave = new ArrayList<>();
                    boolean allPointsValid = true;

                    for (String studentNoValue : studentNos) { // 変数名を studentNo から studentNoValue に変更
                        String pointStr = request.getParameter("point_" + studentNoValue);
                        if (pointStr == null || pointStr.trim().isEmpty()) {
                            continue;
                        }
                        
                        int point;
                        try {
                            point = Integer.parseInt(pointStr);
                            if (point < 0 || point > 100) {
                                errorMessage = "学生番号 " + studentNoValue + " の点数は0点から100点の範囲で入力してください。";
                                allPointsValid = false;
                                break;
                            }
                        } catch (NumberFormatException e) {
                            errorMessage = "学生番号 " + studentNoValue + " の点数が数値ではありません。";
                            allPointsValid = false;
                            break;
                        }

                        Student student = studentDao.get(studentNoValue, loginSchool.getCd()); // 学校コードも渡す
                        if (student == null || subject == null) {
                            errorMessage = "学生または科目の情報取得に失敗しました（登録処理中）。";
                            allPointsValid = false;
                            break;
                        }

                        Test test = new Test();
                        test.setStudent(student);
                        test.setSubject(subject);
                        test.setSchool(loginSchool);
                        test.setNo(testNo);
                        test.setPoint(point);
                        test.setClassNum(student.getClassNum());
                        testsToSave.add(test);
                    }

                    if (allPointsValid && !testsToSave.isEmpty()) {
                        boolean result = testDao.save(testsToSave);
                        if (result) {
                            request.setAttribute("successMessage", "成績を登録/更新しました。");
                            forwardPath = "/scoremanager/main/test_regist_done.jsp";
                        } else {
                            if (errorMessage == null) errorMessage = "成績の登録/更新に失敗しました。";
                        }
                    } else if (allPointsValid && testsToSave.isEmpty() && studentNos.length > 0) {
                        if (infoMessage == null) infoMessage = "登録対象の点数が入力されていませんでした。";
                    }

                } catch (NumberFormatException e) {
                    if (errorMessage == null) errorMessage = "回数または点数が不正な形式です（登録処理中）。";
                } catch (Exception e) {
                    e.printStackTrace();
                    if (errorMessage == null) errorMessage = "成績の登録/更新中にエラーが発生しました: " + e.getMessage();
                }
            } else {
                if (errorMessage == null) errorMessage = "登録に必要な情報（科目、回数、学生）が不足しています。";
            }
        }

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("infoMessage", infoMessage);
        request.getRequestDispatcher(forwardPath).forward(request, response);
    }

    private void prepareDropdownData(HttpServletRequest request, School loginSchool,
                                     StudentDao studentDao, ClassNumDao classNumDao, SubjectDao subjectDao) throws Exception {
        String schoolCdForFilter = null;
        if (loginSchool != null && loginSchool.getCd() != null) {
            schoolCdForFilter = loginSchool.getCd();
        }

        if (studentDao == null || classNumDao == null || subjectDao == null) {
             request.setAttribute("entYearSet", new ArrayList<Integer>());
             request.setAttribute("classNumSet", new ArrayList<String>());
             request.setAttribute("subjectList", new ArrayList<Subject>());
            System.err.println("prepareDropdownData: One or more DAOs are null. studentDao=" + studentDao +
                               ", classNumDao=" + classNumDao + ", subjectDao=" + subjectDao);
            return;
        }
        
        if (schoolCdForFilter == null) {
            System.err.println("prepareDropdownData: schoolCdForFilter is null. Cannot fetch school-specific dropdown data.");
            request.setAttribute("entYearSet", new ArrayList<Integer>());
            request.setAttribute("classNumSet", new ArrayList<String>());
            request.setAttribute("subjectList", new ArrayList<Subject>());
            return;
        }

        // StudentDao.getAllEntYears(schoolCd) を使用
        List<Integer> entYearSet = studentDao.getAllEntYears(schoolCdForFilter);
        request.setAttribute("entYearSet", entYearSet);

        // ClassNumDao.filter(schoolCd) を使用 (studentテーブルから取得するロジックに変更されたことを反映)
        List<String> classNumSet = classNumDao.filter(schoolCdForFilter);
        request.setAttribute("classNumSet", classNumSet);

        // SubjectDao.filter(school) は School オブジェクトを引数に取る (提供された新しいSubjectDaoでは変更なし)
        List<Subject> subjectList = subjectDao.filter(loginSchool);
        request.setAttribute("subjectList", subjectList);
    }
}