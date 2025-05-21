package scoremanager.main;

import java.io.IOException;

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

@WebServlet(urlPatterns={"/SubjectDeleteExecuteAction"})
public class SubjectDeleteExecuteAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        Teacher teacher = null;
        School school = null;
        if (session != null) {
            teacher = (Teacher) session.getAttribute("teacher");
            school = (School) session.getAttribute("loginSchool");
        }

        if (teacher == null || school == null || school.getCd() == null || school.getCd().isEmpty()) {
            request.setAttribute("errorMessage", "学校情報が取得できませんでした。ログインし直してください。");
            request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
            return;
        }

        String cd = request.getParameter("cd");

        Subject subjectToDelete = new Subject();
        subjectToDelete.setCd(cd);
        subjectToDelete.setSchool(school); // Schoolオブジェクトをセット

        try {
            SubjectDao subjectDao = new SubjectDao();

            // 削除前に、削除する科目情報を取得して、完了画面で表示できるようにリクエストスコープにセットしておく
            // ※削除に失敗した場合でも、どの科目を削除しようとしたか表示できるようにするため、先に取得
            Subject deletedSubjectInfo = subjectDao.get(cd, school.getCd());


            // データベースから科目を削除
            boolean isDeleted = subjectDao.delete(subjectToDelete);

            if (isDeleted) {
                // 削除成功時: 完了画面へフォワード
                // 削除された科目情報を完了画面に渡す
                request.setAttribute("deletedSubject", deletedSubjectInfo);
                request.getRequestDispatcher("/scoremanager/main/subject_delete_done.jsp").forward(request, response);
            } else {
                // 削除失敗時: エラーメッセージをセットして科目一覧へ戻る
                request.setAttribute("message", "科目の削除に失敗しました。指定された科目が見つからないか、他の要因で削除できませんでした。");
                request.setAttribute("isSuccess", false);
                request.getRequestDispatcher("/SubjectListAction").forward(request, response);
            }

        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データベース接続設定が見つかりません。システム管理者に連絡してください。");
            request.getRequestDispatcher("/scoremanager/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "科目の削除処理中にエラーが発生しました。システム管理者に連絡してください。");
            request.getRequestDispatcher("/scoremanager/error.jsp").forward(request, response);
        }
    }
}