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
public class AddServlet extends HttpServlet {

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

		String addActorname = request.getParameter("add_actorname");
		String addReputation = request.getParameter("add_reputation");
		String addPrefecture = request.getParameter("add_prefecture");
		String addHeight = request.getParameter("add_height");
		String addyear = request.getParameter("add_year");
		String addmonth = request.getParameter("add_month");
		String addday = request.getParameter("add_day");
		String addBirthday = addyear + "-" + addmonth + "-" + addday;

		out.println("<html>");
		out.println("<head>");
		out.println("<link rel=\"stylesheet\" href=\"/uikit.min.css\">");
		out.println("</head>");
		out.println("<body class=\"uk-background-muted uk-padding\">");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();
			
			/*int max_pid = 0;
			ResultSet rs = stmt.executeQuery("SELECT MAX(pid) AS max_pid FROM products");
			while (rs.next()) {
				max_pid = rs.getInt("max_pid");
				
			}
			rs.close();
			
			int addPID = max_pid + 1;
			*/
			stmt.executeUpdate("INSERT INTO actor VALUES( '" + addActorname + "' , " + addReputation + " , '" + addPrefecture + "' , " + addHeight + ", '" + 
					addBirthday + "')");

			out.println("以下のデータを追加しました。<br/><br/>");
			out.println("俳優名: " + addActorname + "<br/>");
			out.println("評価: " + addReputation + "<br/>");
			out.println("出身都道府県: " + addPrefecture + "<br/>");
			out.println("身長: " + addHeight + "<br/>");
			out.println("誕生日: " + addBirthday + "<br/>");
			
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
