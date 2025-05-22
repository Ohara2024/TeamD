package scoremanager.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.School;
import bean.Subject;
import dao.SubjectDao;

@WebServlet("/main/SubjectCreate.action")
public class SubjectCreateAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String cd = request.getParameter("cd");
        String name = request.getParameter("name");

        School school = new School();
        school.setCd("oom"); // 必要に応じてセッションなどから取得

        Subject subject = new Subject();
        subject.setCd(cd);
        subject.setName(name);
        subject.setSchool(school);

        try {
            SubjectDao dao = new SubjectDao();
            boolean result = dao.save(subject);

            if (result) {
                request.setAttribute("message", "科目の登録または更新に成功しました。");
            } else {
                request.setAttribute("message", "科目の登録または更新に失敗しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "データベースエラーが発生しました: " + e.getMessage());
        }

        request.getRequestDispatcher("/scoremanager/main/subject_create_done.jsp").forward(request, response);
    }
}