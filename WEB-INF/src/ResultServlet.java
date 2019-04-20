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
public class ResultServlet extends HttpServlet {

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
		
		String searchName = request.getParameter("search_name");

		out.println("<html>");
		out.println("<head>");
		out.println("<link rel=\"stylesheet\" href=\"/uikit.min.css\">");
		out.println("<script src=\"uikit.min.js\"></script>");
		out.println("</head>");
		
		
		out.println("<body class=\"uk-background-default uk-padding\">");
		out.println("<h1 class='uk-article-title uk-text-center'>検索結果</h1>");
		
		out.println("<nav class=\"uk-navbar-container\" uk-navbar uk-sticky>");
		out.println("<div class=\"uk-navbar-center\">");
		out.println("<ul class=\"uk-navbar-nav\">");
		out.println("<li class><a href=\"list?about=actor\">俳優</a></li>");
		out.println("<li><a href=\"list?about=works\">作品(映画/ドラマ)</a></li>");
		out.println("<li><a href=\"list?about=information\">出演情報</a></li>");
		out.println("<li><a href=\"list?about=offices\">事務所</a></li>");
		out.println("</ul>");
		out.println("</div>");
		out.println("</nav>");
		
		
		//ここまでヘッダー
		
		
		out.println("<div class=\"uk-grid\">");
		out.println("<div class=\"uk-width-1-2\">");
		out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
		out.println("<h3 class='uk-text-lead' href=\"wlist\">俳優</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table class='uk-table' border=\"1\">");
			out.println("<tr><th>俳優名</th><th>評価</th><th>出身都道府県</th><th>身長</th><th>誕生日</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT * FROM actor WHERE actorname LIKE '%" 
												+ searchName + "%'");
			while (rs.next()) {
				String actorname = rs.getString("actorname");
				int reputation = rs.getInt("reputation");
				String prefecture = rs.getString("prefecture");
				int height = rs.getInt("height");
				java.sql.Date birthday = rs.getDate("birthday");

				out.println("<tr>");
				/*out.println("<td><a href=\"item?pid=" + pid + "\">" + pid
						+ "</a></td>");*/
				out.println("<td><a href=\"item?actorname=" + actorname + "\">" + actorname + "</a></td>");
				out.println("<td>" + reputation + "</td>");
				out.println("<td>" + prefecture + "</td>");
				out.println("<td>" + height + "</td>");
				out.println("<td>" + birthday + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");
			out.println("</div>");
			out.println("</div>");
		}catch (Exception e) {
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
		
		out.println("<div class=\"uk-width-1-2\">");
		out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
		out.println("<h3 class='uk-text-lead' href=\"wlist\">作品</h3>");
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table class='uk-table' border=\"1\">");
			out.println("<tr><th>作品名</th><th>放送年</th><th>放送されたメディア</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT * FROM works WHERE workname LIKE '%" 
												+ searchName + "%'");
			while (rs.next()) {
				String workname = rs.getString("workname");
				int year = rs.getInt("year");
				String broadcastmedia = rs.getString("broadcastmedia");

				out.println("<tr>");
				out.println("<td>" + workname + "</td>");
				out.println("<td>" + year + "</td>");
				out.println("<td>" + broadcastmedia + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");
			out.println("</div>");
			out.println("</div>");
			out.println("</div>");
			}
		 catch (Exception e) {
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
		
		
		//out.println("<div class=\"uk-grid\">");
		//out.println("<div class=\"uk-width-1-1\">");
		out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
		out.println("<h3 class='uk-text-lead' href=\"wlist\">出演情報</h3>");
		
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table class='uk-table' border=\"1\">");
			out.println("<tr><th>俳優名</th><th>評価</th><th>出身都道府県</th><th>身長</th><th>誕生日</th><th>作品名</th><th>出演度</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT * FROM actor NATURAL LEFT JOIN play_information WHERE actorname LIKE '%" 
												+ searchName + "%' OR workname LIKE '%" + searchName + "%'");
			while (rs.next()) {
				String actorname = rs.getString("actorname");
				int reputation = rs.getInt("reputation");
				String prefecture = rs.getString("prefecture");
				int height = rs.getInt("height");
				java.sql.Date birthday = rs.getDate("birthday");
				String workname = rs.getString("workname");
				int performance = rs.getInt("performance");

				out.println("<tr>");
				/*out.println("<td><a href=\"item?pid=" + pid + "\">" + pid
						+ "</a></td>");*/
				out.println("<td><a href=\"item?actorname=" + actorname + "\">" + actorname + "</a></td>");
				out.println("<td>" + reputation + "</td>");
				out.println("<td>" + prefecture + "</td>");
				out.println("<td>" + height + "</td>");
				out.println("<td>" + birthday + "</td>");
				out.println("<td>" + workname + "</td>");
				out.println("<td>" + performance + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");
			out.println("</div>");
			//out.println("</div>");
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
