package scoremanager.main; // この行を修正

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Subject;
import dao.SubjectDao;

@WebServlet(urlPatterns = { "/SubjectDeleteAction" })
public class SubjectDeleteAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String cd = request.getParameter("cd");
        HttpSession session = request.getSession(false);
        String schoolCd = null;

        if (session != null && session.getAttribute("schoolCd") != null) {
            schoolCd = (String) session.getAttribute("schoolCd");
        }

        if (cd == null || cd.isEmpty()) {
            System.out.println("SubjectDeleteAction: 科目CDが提供されていません。"); // ★ ログ出力 ★
            response.sendRedirect("SubjectListAction");
            return;
        }

        if (schoolCd == null || schoolCd.isEmpty()) {
            System.out.println("SubjectDeleteAction: 学校情報が取得できませんでした。"); // ★ ログ出力 ★
            response.sendRedirect("SubjectListAction?error=noschool"); // エラーパラメータを追加
            return;
        }

        try {
            SubjectDao subjectDao = new SubjectDao();
            Subject subject = subjectDao.findByCd(cd);

            System.out.println("SubjectDeleteAction: 取得した Subject オブジェクト: " + subject); // ★ ログ出力 ★

            if (subject != null && subject.getSchool().getCd().equals(schoolCd)) {
                System.out.println("SubjectDeleteAction: 科目CD: " + subject.getCd() + ", 科目名: " + subject.getName() + ", 学校コード: " + subject.getSchool().getCd()); // ★ ログ出力 ★
                request.setAttribute("cd", subject.getCd());
                request.setAttribute("name", subject.getName());
                request.getRequestDispatcher("subjectDeleteConfirm.jsp").forward(request, response);
            } else if (subject == null) {
                System.out.println("SubjectDeleteAction: 科目が見つかりませんでした - CD: " + cd); // ★ ログ出力 ★
                response.sendRedirect("SubjectListAction?error=notfound");
            } else {
                System.out.println("SubjectDeleteAction: 削除しようとした科目は異なる学校に属しています - CD: " + cd + ", 学校コード: " + subject.getSchool().getCd() + ", ログイン教師の学校コード: " + schoolCd); // ★ ログ出力 ★
                response.sendRedirect("SubjectListAction?error=permission"); // エラーパラメータを追加
            }

        } catch (NamingException e) {
            e.printStackTrace();
            System.err.println("SubjectDeleteAction: 初期化エラーが発生しました: " + e.getMessage()); // ★ エラーログ出力 ★
            request.setAttribute("errorMessage", "初期化エラーが発生しました。");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SubjectDeleteAction: 科目の検索に失敗しました: " + e.getMessage()); // ★ エラーログ出力 ★
            request.setAttribute("errorMessage", "科目の検索に失敗しました。");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}