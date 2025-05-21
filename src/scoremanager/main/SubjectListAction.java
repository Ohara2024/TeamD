package scoremanager.main;

import java.io.IOException;
// import java.io.PrintWriter; // JSPにフォワードするため不要になります
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
import dao.SubjectDao;

@WebServlet(urlPatterns={"/SubjectListAction"})
public class SubjectListAction extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // PrintWriter out = response.getWriter(); // JSPにフォワードするため不要になります
        HttpSession session = request.getSession(false);

        String schoolCd = null;
        if (session != null && session.getAttribute("schoolCd") != null) {
            // ★ セッションからログインしている教師の学校コードを取得 ★
            schoolCd = (String) session.getAttribute("schoolCd");
        }

        if (schoolCd == null || schoolCd.isEmpty()) {
            // 学校情報が取得できない場合はエラーページまたはログインページへリダイレクト
            // ここでは簡易的にエラーメッセージを設定し、エラーJSPにフォワードする例
            request.setAttribute("errorMessage", "学校情報が取得できませんでした。ログインし直してください。");
            request.getRequestDispatcher("error.jsp").forward(request, response); // error.jsp がある場合
            // もしくは response.sendRedirect("login.jsp"); など
            return;
        }

        School school = new School();
        school.setCd(schoolCd);

        try {
            SubjectDao subjectDao = new SubjectDao();

            List<Subject> subjectList = subjectDao.filter(school);

            // 取得した科目リストをリクエストスコープに保存
            request.setAttribute("subjectList", subjectList);

            // 科目一覧JSP（subject_list.jsp）へフォワード
            // JSPが webcontent/scoremanager/main/ にあることを前提としています
            request.getRequestDispatcher("/scoremanager/main/subject_list.jsp").forward(request, response);

        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "初期化エラーが発生しました。");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "データの取得に失敗しました。");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}