package tool;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // web.xmlで指定していれば取得（省略可）
        String configEncoding = filterConfig.getInitParameter("encoding");
        if (configEncoding != null) {
            encoding = configEncoding;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // エンコーディングを設定
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);

        // 次のフィルターまたはサーブレットへ処理を渡す
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // フィルターの後処理（特に何もしない）
    }
}
