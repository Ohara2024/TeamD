package tool;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 文字エンコーディング設定
        request.setCharacterEncoding("UTF-8");

        // リクエストパスを取得
        String path = request.getServletPath();  // 例：/StudentList.action

        // アクション名に変換
        String actionClassName = convertPathToActionClass(path); // 例：scoremanager.action.StudentListAction

        try {
            // アクションクラスを動的に読み込み
            Class<?> actionClass = Class.forName(actionClassName);
            Action action = (Action) actionClass.getDeclaredConstructor().newInstance();

            // execute() 実行
            String result = action.execute(request, response);

            // 戻り値により遷移先決定（フォワード or リダイレクト）
            String view = "/WEB-INF/jsp/" + result + ".jsp";
            RequestDispatcher rd = request.getRequestDispatcher(view);
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 拡張：/StudentList.action → scoremanager.action.StudentListAction に変換
    private String convertPathToActionClass(String path) {
        String actionName = path.substring(1, path.indexOf(".action")); // 例：StudentList
        return "scoremanager.action." + actionName + "Action";         // 完全修飾クラス名
    }
}
