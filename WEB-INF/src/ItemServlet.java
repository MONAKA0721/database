import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ItemServlet extends HttpServlet {

	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public void init() throws ServletException {
		// iniファイルから自分のデータベース情報を読み込む
		String iniFilePath = getServletConfig().getServletContext()
				.getRealPath("WEB-INF/le4db.ini");
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			Properties prop = new Properties();
			prop.load(fis);
			_hostname = prop.getProperty("hostname");
			_dbname = prop.getProperty("dbname");
			_username = prop.getProperty("username");
			_password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String actorname = request.getParameter("actorname");

		out.println("<html>");
		out.println("<head>");
		out.println("<link rel=\"stylesheet\" href=\"/uikit.min.css\">");
		out.println("</head>");
		out.println("<body class=\"uk-background-muted uk-padding\">");

		out.println("<h3>更新</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<form action=\"update\" method=\"GET\">");
			out.println("俳優名： "+ actorname);
			out.println("<input type=\"hidden\" name=\"update_actorname\" value=\"" + actorname + "\"/>");
			out.println("<br/>");
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM actor WHERE actorname = '" + actorname + "'");
			while (rs.next()) {
				int reputation = rs.getInt("reputation");
				String prefecture = rs.getString("prefecture");
				int height = rs.getInt("height");
				java.sql.Date birthday = rs.getDate("birthday");
				
			
				out.println("評価： ");
				out.println("<input type=\"text\" name=\"update_reputation\" value=\"" + reputation + "\"/>");
				out.println("<br/>");
				out.println("出身都道府県： ");
				out.println("<input type=\"text\" name=\"update_prefecture\" value=\"" + prefecture + "\"/>");
				out.println("<br/>");
				out.println("身長： ");
				out.println("<input type=\"text\" name=\"update_height\" value=\"" + height + "\"/>");
				out.println("<br/>");
				out.println("誕生日： ");
				out.println("<input type=\"text\" name=\"update_birthday\" value=\"" + birthday + "\"/>");
				out.println("<br/>");
				
			}
			rs.close();
			
			out.println("<input type=\"submit\" value=\"更新\"/>");
			out.println("</form>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.println("<h3>削除</h3>");
		out.println("<form action=\"delete\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"delete_actorname\" value=\"" + actorname + "\">");
		out.println("<input type=\"submit\" value=\"削除\"/>");
		out.println("</form>");

//		out.println("<table class='uk-table' border=\"1\">");
//		ResultSet rs_works = stmt.executeQuery("SELECT * FROM works WHERE actorname=" + actorname );
		
		out.println("<br/>");
		out.println("<a href=\"list?about=actor\">トップページに戻る</a>");

		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
