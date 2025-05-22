package login; // パッケージ名はあなたのプロジェクトに合わせてください (例: scoremanager.main または login)

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

// bean.School と bean.Teacher をインポート (認証成功時にセッションに格納するため)
import bean.School;
import bean.Teacher;
import dao.SchoolDao;


@WebServlet("/login") // ★サーブレットのマッピング名を確認
public class LoginServlet extends HttpServlet {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 3 * 60 * 1000; // 3 minutes

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // リクエストの文字コード設定

        String teacherId = request.getParameter("teacherId"); // JSPのname属性に合わせる (例: "teacherId")
        String password = request.getParameter("password");
        HttpSession session = request.getSession();

        Long lockTime = (Long) session.getAttribute("lockTime");
        Boolean loginLocked = (Boolean) session.getAttribute("loginLocked");

        if (loginLocked != null && loginLocked) {
            if (lockTime != null && (System.currentTimeMillis() - lockTime > LOCKOUT_DURATION)) {
                session.removeAttribute("loginLocked");
                session.removeAttribute("lockTime");
                session.removeAttribute("remainingAttempts"); // ロック解除時に試行回数もリセット
                request.setAttribute("infoMessage", "アカウントのロックが解除されました。"); // エラーではなく情報メッセージ
                System.out.println("DEBUG: Account unlocked for ID: " + teacherId); // デバッグログ
            } else {
                request.setAttribute("errorMessage", "アカウントがロックされています。しばらく経ってから再度お試しください。");
                request.setAttribute("loginLocked", true); // JSP側で入力フィールドを無効化するために再度セット
                System.out.println("DEBUG: Login attempt for locked account ID: " + teacherId); // デバッグログ
                request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
                return;
            }
        }

        Integer remainingAttempts = (Integer) session.getAttribute("remainingAttempts");
        if (remainingAttempts == null) {
            remainingAttempts = MAX_ATTEMPTS; // 初回は最大回数
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Teacher teacher = null; // 認証成功したTeacherオブジェクトを格納

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/yajima"); // ★JNDI名を "jdbc/yajima" に変更
            conn = ds.getConnection();

            String sql = "SELECT * FROM TEACHER WHERE ID = ? AND PASSWORD = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacherId);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 認証成功
                teacher = new Teacher();
                teacher.setId(rs.getString("ID"));
                teacher.setName(rs.getString("NAME"));
                teacher.setPassword(rs.getString("PASSWORD")); // パスワードをセッションに保存するのはセキュリティ上非推奨

                String schoolCd = rs.getString("SCHOOL_CD");
                if (schoolCd != null) {
                    SchoolDao schoolDao = new SchoolDao(); // SchoolDaoのインスタンス化
                    School school = schoolDao.get(schoolCd); // SchoolDaoを使ってSchoolオブジェクトを取得
                    teacher.setSchool(school); // TeacherオブジェクトにSchoolオブジェクトをセット
                }

                // ★ここを修正: キー名を "loginTeacher" に変更しました★
                session.setAttribute("loginTeacher", teacher); // Teacherオブジェクトをセッションに
                System.out.println("DEBUG: Login successful for ID: " + teacherId + ", Name: " + teacher.getName()); // デバッグログ

                if (teacher.getSchool() != null) {
                    session.setAttribute("loginSchool", teacher.getSchool()); // Schoolオブジェクトをセッションに
                    System.out.println("DEBUG: School associated: " + teacher.getSchool().getName()); // デバッグログ
                } else {
                    // 学校情報がない場合の処理 (エラーにするか、特定の動作をするか)
                    request.setAttribute("errorMessage", "教員に学校情報が紐付いていません。");
                    System.err.println("ERROR: Teacher with ID " + teacherId + " has no associated school."); // エラーログ
                    request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
                    return;
                }

                session.removeAttribute("remainingAttempts");
                session.removeAttribute("loginLocked");
                session.removeAttribute("lockTime");

                // ログイン後のリダイレクト先
                response.sendRedirect(request.getContextPath() + "/scoremanager/main/menu.jsp"); // または StudentListActionなど
            } else {
                // 認証失敗
                remainingAttempts--;
                session.setAttribute("remainingAttempts", remainingAttempts);
                request.setAttribute("remainingAttempts", remainingAttempts); // JSP表示用
                request.setAttribute("errorMessage", "ユーザー名またはパスワードが間違っています。");
                System.out.println("DEBUG: Login failed for ID: " + teacherId + ". Remaining attempts: " + remainingAttempts); // デバッグログ

                if (remainingAttempts <= 0) {
                    session.setAttribute("loginLocked", true);
                    session.setAttribute("lockTime", System.currentTimeMillis());
                    request.setAttribute("loginLocked", true); // JSP表示用
                    request.setAttribute("errorMessage", "入力可能回数が上限に達しました。アカウントをロックしました。");
                    System.out.println("DEBUG: Account locked for ID: " + teacherId); // デバッグログ
                }
                request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
            }

        } catch (NamingException e) {
            e.printStackTrace();
            System.err.println("ERROR: NamingException occurred: " + e.getMessage()); // エラーログ
            request.setAttribute("errorMessage", "データベース接続設定が見つかりません (JNDI名: jdbc/yajima)。");
            request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: SQLException occurred: " + e.getMessage()); // エラーログ
            request.setAttribute("errorMessage", "データベースエラーが発生しました。");
            request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
        } catch (Exception e) { // SchoolDao.get() などで発生しうる一般的な例外もキャッチ
            e.printStackTrace();
            System.err.println("ERROR: Unexpected Exception occurred: " + e.getMessage()); // エラーログ
            request.setAttribute("errorMessage", "処理中に予期せぬエラーが発生しました。");
            request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
        }
        finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GETリクエストで /login にアクセスされた場合は、ログインページを表示
        request.getRequestDispatcher("/scoremanager/login.jsp").forward(request, response);
    }
}