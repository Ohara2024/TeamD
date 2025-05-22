package scoremanager.main;

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School; // Schoolクラスをインポート
import bean.Subject;
import bean.Teacher;
import dao.SubjectDao;

@WebServlet(urlPatterns={"/SubjectDeleteAction"})
public class SubjectDeleteAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        Teacher teacher = null;
        School school = null; // Schoolオブジェクトを追加
        if (session != null) {
            teacher = (Teacher) session.getAttribute("teacher");
            // ★重要: LoginServletで "loginSchool" というキーでSchoolオブジェクトがセッションに保存されている必要があります。
            school = (School) session.getAttribute("loginSchool");
        }

        // ログイン状態と学校コードの確認
        if (teacher == null || school == null || school.getCd() == null || school.getCd().isEmpty()) {
            request.setAttribute("errorMessage", "学校情報が取得できませんでした。ログインし直してください。");
            request.getRequestDispatcher("/scoremanager/main/login.jsp").forward(request, response);
            return;
        }

        String schoolCd = school.getCd(); // 学校コードを取得

        // パラメータから削除対象の科目コードを取得
        String cd = request.getParameter("cd");

        Subject subject = null;
        try {
            SubjectDao subjectDao = new SubjectDao();
            // 科目コードと学校コードで科目情報を取得
            subject = subjectDao.get(cd, schoolCd); // SubjectDao.get(String, String) を呼び出す

            if (subject == null) {
                // 指定された科目コードの科目が見つからない場合
                request.setAttribute("errorMessage", "指定された科目が見つかりませんでした。");
                request.getRequestDispatcher("/SubjectListAction").forward(request, response); // 科目一覧に戻す
                return;
            }

            // 取得した科目オブジェクトをリクエストスコープに保存
            request.setAttribute("subject", subject);

            // 削除確認JSPへフォワード
            request.getRequestDispatcher("/scoremanager/main/subject_delete.jsp").forward(request, response);

        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベース接続設定が見つかりません。システム管理者に連絡してください。");
            request.getRequestDispatcher("/scoremanager/main/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "科目情報の取得に失敗しました。システム管理者に連絡してください。");
            request.getRequestDispatcher("/scoremanager/main/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 通常、確認画面への遷移はGETで行われるため、POSTでここに来た場合はGETと同じ処理にフォワード
        doGet(request, response);
    }
}