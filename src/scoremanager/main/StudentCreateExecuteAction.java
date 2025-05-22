package scoremanager.main;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Student;
import dao.StudentDao;

@WebServlet(urlPatterns={"/StudentCreateExecuteAction"})
public class StudentCreateExecuteAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        School school = (School) session.getAttribute("loginSchool");

        // ログインチェック
        if (school == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // --- リクエストパラメータの取得とバリデーション ---
        String entYearStr = request.getParameter("entYear");
        String no = request.getParameter("no");
        String name = request.getParameter("name");
        String classNum = request.getParameter("classNum");
        boolean isAttend = request.getParameter("attend") != null; // チェックボックスがチェックされていれば true

        String error_message = null; // エラーメッセージ用

        // 入学年度の数値変換
        Integer entYear = null;
        try {
            if (entYearStr != null && !entYearStr.isEmpty()) {
                entYear = Integer.parseInt(entYearStr);
            } else {
                error_message = "入学年度が選択されていません。";
            }
        } catch (NumberFormatException e) {
            error_message = "入学年度の形式が不正です。";
            e.printStackTrace();
        }

        // バリデーションチェック (簡易的な例)
        if (no == null || no.isEmpty()) {
            error_message = "学生番号が入力されていません。";
        } else if (name == null || name.isEmpty()) {
            error_message = "氏名が入力されていません。";
        } else if (classNum == null || classNum.isEmpty()) {
            error_message = "クラスが選択されていません。";
        }

        // エラーがある場合は、元のフォームに戻る
        if (error_message != null) {
            request.setAttribute("error", error_message);
            // 入力値を保持してJSPに戻す
            request.setAttribute("entYear", entYearStr); // Stringのまま
            request.setAttribute("no", no);
            request.setAttribute("name", name);
            request.setAttribute("classNum", classNum);
            request.setAttribute("attend", isAttend);

            // ドロップダウンリストの再取得（StudentCreateActionでやっていたように）
            try {
                StudentDao daoForDropdown = new StudentDao();
                request.setAttribute("entYears", daoForDropdown.getAllEntYears(school.getCd()));
                request.setAttribute("classNums", daoForDropdown.getAllClassNums(school.getCd()));
            } catch (RuntimeException e) {
                e.printStackTrace();
                // ドロップダウン取得エラーもユーザーに伝える
                request.setAttribute("error", (request.getAttribute("error") != null ? request.getAttribute("error") + "<br>" : "") + "ドロップダウンリストの取得に失敗しました。");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", (request.getAttribute("error") != null ? request.getAttribute("error") + "<br>" : "") + "ドロップダウンリストの取得中に予期せぬエラーが発生しました。");
            }


            RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_create.jsp");
            dispatcher.forward(request, response);
            return; // 処理を終了
        }

        // --- 学生オブジェクトの作成とDAOでの保存 ---
        Student student = new Student();
        student.setEntYear(entYear);
        student.setNo(no);
        student.setName(name);
        student.setClassNum(classNum);
        student.setAttend(isAttend);
        student.setSchool(school); // ログイン中の学校情報をセット

        StudentDao studentDao = null;
        try {
            studentDao = new StudentDao();
            boolean success = studentDao.save(student); // StudentDaoのsaveメソッドを呼び出し

            if (success) {
                // 登録成功の場合、完了画面にリダイレクト (PRGパターン推奨)
                response.sendRedirect(request.getContextPath() + "/scoremanager/main/student_create_done.jsp");
            } else {
                // 登録失敗の場合、エラーメッセージをセットして元のフォームに戻る
                error_message = "学生情報の登録に失敗しました。学生番号が既に存在するか、データベースに問題があります。";
                request.setAttribute("error", error_message);

                // 入力値を保持してJSPに戻す
                request.setAttribute("entYear", entYearStr);
                request.setAttribute("no", no);
                request.setAttribute("name", name);
                request.setAttribute("classNum", classNum);
                request.setAttribute("attend", isAttend);

                // ドロップダウンリストの再取得
                try {
                    StudentDao daoForDropdown = new StudentDao();
                    request.setAttribute("entYears", daoForDropdown.getAllEntYears(school.getCd()));
                    request.setAttribute("classNums", daoForDropdown.getAllClassNums(school.getCd()));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    request.setAttribute("error", (request.getAttribute("error") != null ? request.getAttribute("error") + "<br>" : "") + "ドロップダウンリストの取得に失敗しました。");
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", (request.getAttribute("error") != null ? request.getAttribute("error") + "<br>" : "") + "ドロップダウンリストの取得中に予期せぬエラーが発生しました。");
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_create.jsp");
                dispatcher.forward(request, response);
            }
        } catch (RuntimeException e) {
            // DAOアクセス時のRuntimeException（DataSource初期化エラーなど）
            e.printStackTrace();
            if (e.getCause() instanceof javax.naming.NamingException) {
                error_message = "データソースの初期化に失敗しました。アプリケーション管理者に連絡してください。<br>原因: " + e.getCause().getMessage();
            } else {
                error_message = "学生登録中に予期せぬエラーが発生しました。<br>" + e.getMessage();
            }
            request.setAttribute("error", error_message);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // 致命的なエラーはエラーページへ
            dispatcher.forward(request, response);
        } catch (Exception e) {
            // その他の予期せぬ例外
            e.printStackTrace();
            error_message = "学生登録中に予期せぬエラーが発生しました。<br>" + e.getMessage();
            request.setAttribute("error", error_message);

            // エラー時は元のフォームに戻る
            request.setAttribute("entYear", entYearStr);
            request.setAttribute("no", no);
            request.setAttribute("name", name);
            request.setAttribute("classNum", classNum);
            request.setAttribute("attend", isAttend);

            try {
                StudentDao daoForDropdown = new StudentDao();
                request.setAttribute("entYears", daoForDropdown.getAllEntYears(school.getCd()));
                request.setAttribute("classNums", daoForDropdown.getAllClassNums(school.getCd()));
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                request.setAttribute("error", (request.getAttribute("error") != null ? request.getAttribute("error") + "<br>" : "") + "ドロップダウンリストの取得に失敗しました。");
            } catch (Exception ex) {
                ex.printStackTrace();
                request.setAttribute("error", (request.getAttribute("error") != null ? request.getAttribute("error") + "<br>" : "") + "ドロップダウンリストの取得中に予期せぬエラーが発生しました。");
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("/scoremanager/main/student_create.jsp");
            dispatcher.forward(request, response);
        }
    }
}