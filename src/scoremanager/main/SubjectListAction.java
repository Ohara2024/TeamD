package scoremanager.main;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Subject;
import bean.Teacher;
import dao.SubjectDao;

@WebServlet(urlPatterns={"/main/SubjectListAction"})
public class SubjectListAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false); // セッションが存在しない場合は新しく作成しない

        // セッションからログイン中の教師と学校情報を取得
        Teacher teacher = null;
        School school = null;
        String schoolCd = null;

        if (session != null) {
            teacher = (Teacher) session.getAttribute("teacher"); // LoginServletで設定するキー名
            school = (School) session.getAttribute("loginSchool"); // LoginServletで設定するキー名
        }

        // ログイン状態と学校情報の確認
        if (teacher == null || school == null || school.getCd() == null || school.getCd().isEmpty()) {
            // ログインしていない、または学校情報が取得できない場合はエラーメッセージを設定し、ログインページへリダイレクト
            request.setAttribute("errorMessage", "学校情報が取得できませんでした。ログインし直してください。");
            request.getRequestDispatcher("/scoremanager/main/login.jsp").forward(request, response);
            return;
        }

        schoolCd = school.getCd();

        try {
            SubjectDao subjectDao = new SubjectDao();

            // ログインしている教師の学校コードに紐づく科目リストを取得
            // DAOのfilterメソッドはSchoolオブジェクトを引数に取ることを想定
            School filterSchool = new School();
            filterSchool.setCd(schoolCd);
            List<Subject> subjectList = subjectDao.filter(filterSchool);

            // 取得した科目リストをリクエストスコープに保存
            request.setAttribute("subjectList", subjectList);

            // 科目一覧JSPへフォワード
            request.getRequestDispatcher("/scoremanager/main/subject_list.jsp").forward(request, response);

        } catch (NamingException e) {
            // JNDIリソースのルックアップ失敗などの初期化エラー
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベース接続設定が見つかりません。システム管理者に連絡してください。");
            request.getRequestDispatcher("/error.jsp").forward(request, response); // エラーページへのフォワード
        } catch (Exception e) {
            // その他の例外（SQLExceptionなど）
            e.printStackTrace();
            request.setAttribute("errorMessage", "データの取得に失敗しました。システム管理者に連絡してください。");
            request.getRequestDispatcher("/error.jsp").forward(request, response); // エラーページへのフォワード
        }
    }
}