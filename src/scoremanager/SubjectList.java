package scoremanager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.School;
import dao.SubjectDao;

@WebServlet("/SubjectList")
public class SubjectList extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ここは適切な学校コードに置き換えてください
            School school = new School();
            school.setCd("SCHOOL001");

            List<Map<String, String>> subjectList = SubjectDao.findAll(school);
            request.setAttribute("subjectList", subjectList);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "科目一覧の取得に失敗しました: " + e.getMessage());
        }

        request.getRequestDispatcher("/scoremanager/main/subject_list.jsp").forward(request, response);
    }
}
