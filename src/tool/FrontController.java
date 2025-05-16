package tool;

import java.io.IOException;
import java.lang.reflect.Constructor; // ★ import を追加 ★
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"*.action"})
public class FrontController extends HttpServlet {

    private static final String ACTION_PACKAGE = "scoremanager."; // Action クラスが存在するパッケージ

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String name = path.substring(1, path.lastIndexOf("."));

        try {
            @SuppressWarnings("rawtypes")
            Class actionClass = Class.forName(ACTION_PACKAGE + name.substring(0, 1).toUpperCase() + name.substring(1) + "Action");
            // ★ 修正箇所 ★
            Constructor<?> constructor = actionClass.getDeclaredConstructor();
            Action action = (Action) constructor.newInstance();
            String view = action.execute(request, response);
            if (view != null) {
                request.getRequestDispatcher(view).forward(request, response);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action クラスが見つかりません: " + name + "Action");
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Action クラスのインスタンス化に失敗しました: " + name + "Action");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Action の実行中にエラーが発生しました: " + name + "Action");
        }
    }
}