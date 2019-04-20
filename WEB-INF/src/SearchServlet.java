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
public class SearchServlet extends HttpServlet {

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
		String searchPrefecture = request.getParameter("search_prefecture");
		String searchMethod = request.getParameter("search_method");
		String searchLowerReputation = request.getParameter("search_lowerReputation");
		String searchUpperReputation = request.getParameter("search_upperReputation");
		
		String addQuery = "WHERE";
		String searchInformation = "";
		if(!searchName.isEmpty()){
			addQuery += " actorname LIKE '%" + searchName + "%'";
			searchInformation += "俳優名 : " + searchName + "<br/>";
		}
		if(!searchPrefecture.isEmpty()){
			if(searchName.isEmpty()){searchMethod = "";}
			addQuery += searchMethod + " prefecture='" + searchPrefecture + "'";
			searchMethod = request.getParameter("search_method");
			searchInformation += "出身都道府県 : " + searchPrefecture + "<br/>";
		}
		if(!searchLowerReputation.isEmpty() && !searchUpperReputation.equals("10000")){
			int intSearchLowerReputation = Integer.parseInt(searchLowerReputation);
			int intSearchUpperReputation = Integer.parseInt(searchUpperReputation);
			if(searchName.isEmpty() && searchPrefecture.isEmpty()){searchMethod = "";}
			addQuery += searchMethod + " reputation BETWEEN " + intSearchLowerReputation + " AND " + intSearchUpperReputation;
			searchInformation += "評価 : " + searchLowerReputation + " 〜 " + searchUpperReputation + "<br/>";
			searchMethod = request.getParameter("search_method");
		}else if(!searchLowerReputation.isEmpty()) {
			int intSearchLowerReputation = Integer.parseInt(searchLowerReputation);
			if(searchName.isEmpty() && searchPrefecture.isEmpty()){searchMethod = "";}
			addQuery += searchMethod + " reputation >= " + intSearchLowerReputation ;
			searchInformation += "評価 : " + searchLowerReputation + "以上" + "<br/>";
		}else if(!searchUpperReputation.equals("10000") ) {
			int intSearchUpperReputation = Integer.parseInt(searchUpperReputation);
			if(searchName.isEmpty() && searchPrefecture.isEmpty() ){searchMethod = "";}
			addQuery += searchMethod + " reputation <= " + intSearchUpperReputation ;
			searchInformation += "評価 : " + searchUpperReputation + "以下" + "<br/>";
		}
		if(searchName.isEmpty() && searchPrefecture.isEmpty() && searchLowerReputation.isEmpty() && searchUpperReputation.equals("10000") ) {
			addQuery = "";
		}

		out.println("<html>");
		out.println("<head>");
		out.println("<link rel=\"stylesheet\" href=\"/uikit.min.css\">");
		out.println("</head>");
		out.println("<body class=\"uk-padding uk-background-muted\">");

		
		out.println("<h3>検索結果</h3>");
		out.println("検索に用いた条件<br/><br/>" + searchInformation);
		
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table class='uk-table' border=\"1\">");
			out.println("<tr><th>俳優名</th><th>評価</th><th>出身都道府県</th><th>身長</th><th>誕生日</th></tr>");

			ResultSet rs = stmt
					.executeQuery("SELECT * FROM actor " + addQuery);
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
