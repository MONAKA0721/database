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
public class ListServlet extends HttpServlet {

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
		
		String about = request.getParameter("about");

		out.println("<html>");
		out.println("<head>");
		out.println("<link rel=\"stylesheet\" href=\"/uikit.min.css\">");
		out.println("<script src=\"uikit.min.js\" type=\"text/javascript\" charset=\"utf-8\" language=\"JavaScript\"></script>");
		out.println("<script src=\"method.js\" type=\"text/javascript\" charset=\"utf-8\" language=\"JavaScript\"></script>");
		out.println("<script src=\"uikit-icons.min.js\"></script>");
		out.println("</head>");
		out.println("<body class=\"uk-background-default uk-padding\">");
		out.println("<h1 class='uk-article-title uk-text-center'>俳優データベース</h1>");
		out.println("<nav class=\"uk-navbar-container\" uk-navbar uk-sticky>");
		out.println("<div class=\"uk-navbar-center\">");
		
		out.println("<ul class=\"uk-navbar-nav\">");
		out.println("<li class><a href=\"list?about=actor\">俳優</a></li>");
		out.println("<li><a href=\"list?about=offices\">事務所</a></li>");
		out.println("<li><a href=\"list?about=works\">作品(映画/ドラマ)</a></li>");
		out.println("<li><a href=\"list?about=production_source\">制作会社</a></li>");
		out.println("<li><a href=\"list?about=actor_office_information\">所属情報</a></li>");
		out.println("<li><a href=\"list?about=information\">出演情報</a></li>");
		out.println("<li><a href=\"list?about=music\">使用曲</a></li>");
		out.println("<li><a href=\"list?about=salemedia\">販売メディア</a></li>");
		out.println("<li><a href=\"list?about=distribution\">配給</a></li>");
		out.println("</ul>");
		out.println("<div class='uk-navbar-item'>");
		out.println("<form action=\"result\" method=\"GET\">");
		//out.println("俳優名or作品名： ");
		out.println("<input class='uk-input uk-form-width-small' type=\"text\" name=\"search_name\" placeholder='俳優名or作品名'/>");
		out.println("<input class='uk-button uk-button-primary uk-button-small' type=\"submit\" value=\"検索\"/>");
		out.println("</div>");
		out.println("</form>");
		out.println("</div>");
		
		out.println("</div>");
		out.println("</nav>");
		
		
		//ここまでヘッダー//////////////////////////////////////////////////////////////////
		out.println("<div class=\"uk-grid\">");
		out.println("<div class=\"uk-width-3-5\">");
		out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
		out.println("<h3 class='uk-text-lead' href=\"wlist\">一覧</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();
			
			out.println("<table class='uk-table' border=\"1\">");
			if(about.equals("actor")){
				
				String sortby = "";
				String orderBy[] = request.getParameterValues("orderBy");
				//out.println(orderBy[0]);
				if(orderBy!=null) {
					sortby += orderBy[0];}
				
				out.println("<tr><th>俳優名</th><th>評価(<a href='list?about=actor&orderBy=reputation_desc'>降順 </a><a href='list?about=actor&orderBy=reputation_asc'>昇順</a>)</th>"
						+ "<th>出身都道府県(<a href='list?about=actor&orderBy=prefecture'>ソート</a>)</th>"
						+ "<th>身長(<a href='list?about=actor&orderBy=height_desc'>降順 </a><a href='list?about=actor&orderBy=height_asc'>昇順</a>)</th>"
						+ "<th>誕生日(<a href='list?about=actor&orderBy=birthday_desc'>降順 </a><a href='list?about=actor&orderBy=birthday_asc'>昇順</a>)</th></tr>");
				ResultSet rs = null;
				if(sortby.isEmpty()){rs = stmt.executeQuery("SELECT * FROM actor");}
				else if(sortby.equals("reputation_asc")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY reputation");}
				else if(sortby.equals("reputation_desc")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY reputation DESC");}
				else if(sortby.equals("height_asc")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY height");}
				else if(sortby.equals("height_desc")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY height DESC");}
				else if(sortby.equals("birthday_asc")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY birthday");}
				else if(sortby.equals("birthday_desc")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY birthday DESC");}
				else if(sortby.equals("prefecture")) {rs = stmt.executeQuery("SELECT * FROM actor ORDER BY prefecture");}
				while (rs.next()) {
					String actorname = rs.getString("actorname");
					int reputation = rs.getInt("reputation");
					String prefecture = rs.getString("prefecture");
					int height = rs.getInt("height");
					java.sql.Date birthday = rs.getDate("birthday");
	
					out.println("<tr>");
					out.println("<td><a href=\"item?actorname=" + actorname + "\">" + actorname + "</a></td>");
					out.println("<td>" + reputation + "</td>");
					out.println("<td>" + prefecture + "</td>");
					out.println("<td>" + height + "</td>");
					out.println("<td>" + birthday + "</td>");
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("works")){
				out.println("<tr><th>作品名</th><th>放送年</th><th>放送されたメディア</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM works");
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
			}else if(about.equals("information")){
				out.println("<tr><th>俳優名</th><th>作品名</th><th>出演度</th></tr>");
				
				ResultSet rs = stmt.executeQuery("SELECT * FROM play_information");
				while (rs.next()) {
					String actorname = rs.getString("actorname");
					String workname = rs.getString("workname");
					int performance = rs.getInt("performance");
	
					out.println("<tr>");
					/*out.println("<td><a href=\"item?pid=" + pid + "\">" + pid
							+ "</a></td>");*/
					//out.println("<td><a href=\"item?actorname=" + actorname + "\">" + actorname + "</a></td>");
					out.println("<td>" + actorname + "</td>");
					out.println("<td>" + workname + "</td>");
					out.println("<td>" + performance + "</td>");
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("offices")) {
				out.println("<tr><th>事務所名</th><th>所在都道府県</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM offices");
				while (rs.next()) {
					String officename = rs.getString("officename");
					String location= rs.getString("location");

					out.println("<tr>");
					out.println("<td>" + officename + "</td>");
					out.println("<td>" + location + "</td>");
					
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("music")) {
				out.println("<tr><th>作品名</th><th>使用曲</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM music");
				while (rs.next()) {
					String workname = rs.getString("workname");
					String music= rs.getString("music");

					out.println("<tr>");
					out.println("<td>" + workname + "</td>");
					out.println("<td>" + music + "</td>");
					
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("actor_office_information")) {
				out.println("<tr><th>事務所名</th><th>俳優名</th><th>所属年</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM actor_office_information");
				while (rs.next()) {
					String officename = rs.getString("officename");
					String actorname= rs.getString("actorname");
					String year = rs.getString("year");

					out.println("<tr>");
					out.println("<td>" + officename + "</td>");
					out.println("<td>" + actorname + "</td>");
					out.println("<td>" + year + "</td>");
					
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("distribution")) {
				out.println("<tr><th>制作会社</th><th>作品名</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM distribution");
				while (rs.next()) {
					String company_name = rs.getString("company_name");
					String workname = rs.getString("workname");

					out.println("<tr>");
					out.println("<td>" + company_name + "</td>");
					out.println("<td>" + workname + "</td>");
					
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("production_source")) {
				out.println("<tr><th>社名</th><th>所在地</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM production_source");
				while (rs.next()) {
					String company_name = rs.getString("company_name");
					String location = rs.getString("location");

					out.println("<tr>");
					out.println("<td>" + company_name + "</td>");
					out.println("<td>" + location + "</td>");
					
					out.println("</tr>");
				}
				rs.close();
			}else if(about.equals("salemedia")) {
				out.println("<tr><th>作品名</th><th>販売メディア</th><th>販売年</th></tr>");

				ResultSet rs = stmt.executeQuery("SELECT * FROM salemedia");
				while (rs.next()) {
					String workname = rs.getString("workname");
					String media = rs.getString("media");
					String year = rs.getString("year");

					out.println("<tr>");
					out.println("<td>" + workname + "</td>");
					out.println("<td>" + media + "</td>");
					out.println("<td>" + year + "</td>");
					
					out.println("</tr>");
				}
				rs.close();
			}
			out.println("</table>");
			ResultSet rs_count = null;
			if(about.equals("actor")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM actor");
			}else if(about.equals("works")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM works");
			}else if(about.equals("information")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM play_information");
			}else if(about.equals("offices")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM offices");
			}else if(about.equals("salemedia")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM salemedia");
			}else if(about.equals("distribution")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM distribution");
			}else if(about.equals("actor_office_information")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM actor_office_information");
			}else if(about.equals("music")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM music");
			}else if(about.equals("production_source")){
				rs_count = stmt.executeQuery("SELECT COUNT(*) FROM production_source");
			}
			while(rs_count.next()){
				int countnumber = rs_count.getInt("count");
				out.println("<p>データ件数は <span class='uk-text-emphasis	'>" + countnumber + "</span> 件です</p>");
			}
			out.println("</div>");
			out.println("</div>");
			rs_count.close();
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
		
		//右側のカード群//////////////////////////////////////////////////////////////////////////
		//out.println("<div class=\"uk-grid\">");
		out.println("<div class=\"uk-width-2-5\">");
		
		
		if(about.equals("actor")){
			out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
			out.println("<h3 class='uk-text-lead'>詳細検索</h3>");
			out.println("<form name=\"DetailedSearch_form\" action=\"search\" method=\"GET\">");
			out.println("<div class=\"uk-margin uk-grid-small uk-child-width-auto uk-grid\">\r\n" + 
					"            <label><input class=\"uk-radio\" type=\"radio\" name=\"search_method\" value=\"AND\" checked> AND検索</label>\r\n" + 
					"            <label><input class=\"uk-radio\" type=\"radio\" name=\"search_method\" value=\"OR\"> OR検索</label>\r\n" + 
					"    </div>");
			out.println("<input class='uk-input uk-form-small uk-form-width-medium' type=\"text\" name=\"search_name\" placeholder=\"俳優名\"/>");
			out.println("<br/>");
			//out.println("<input type=\"button\" value=\"Sample1\" onclick=\"Sample1()\">");
			out.println("<br/>");
			out.println("評価:");
			out.println("<select class='uk-select uk-form-small uk-form-width-small' name=\"search_lowerReputation\">");
			out.println("<option value=\"\">下限なし</option>");
			out.println("<option value=\"0\">0</option>\r\n" + 
					"<option value=\"1\">1</option>\r\n" + 
					"<option value=\"2\">2</option>\r\n" + 
					"<option value=\"3\">3</option>\r\n" + 
					"<option value=\"4\">4</option>\r\n" + 
					"<option value=\"5\">5</option>\r\n" + 
					"<option value=\"6\">6</option>\r\n" + 
					"<option value=\"7\">7</option>\r\n" + 
					"<option value=\"8\">8</option>\r\n" + 
					"<option value=\"9\">9</option>\r\n" + 
					"<option value=\"10\">10</option>\r\n" + 
					"<option value=\"11\">11</option>\r\n" + 
					"<option value=\"12\">12</option>\r\n" + 
					"<option value=\"13\">13</option>\r\n" + 
					"<option value=\"14\">14</option>\r\n" + 
					"<option value=\"15\">15</option>\r\n" + 
					"<option value=\"16\">16</option>\r\n" + 
					"<option value=\"17\">17</option>\r\n" + 
					"<option value=\"18\">18</option>\r\n" + 
					"<option value=\"19\">19</option>\r\n" + 
					"<option value=\"20\">20</option>\r\n" + 
					"<option value=\"21\">21</option>\r\n" + 
					"<option value=\"22\">22</option>\r\n" + 
					"<option value=\"23\">23</option>\r\n" + 
					"<option value=\"24\">24</option>\r\n" + 
					"<option value=\"25\">25</option>\r\n" + 
					"<option value=\"26\">26</option>\r\n" + 
					"<option value=\"27\">27</option>\r\n" + 
					"<option value=\"28\">28</option>\r\n" + 
					"<option value=\"29\">29</option>\r\n" + 
					"<option value=\"30\">30</option>\r\n" + 
					"<option value=\"31\">31</option>\r\n" + 
					"<option value=\"32\">32</option>\r\n" + 
					"<option value=\"33\">33</option>\r\n" + 
					"<option value=\"34\">34</option>\r\n" + 
					"<option value=\"35\">35</option>\r\n" + 
					"<option value=\"36\">36</option>\r\n" + 
					"<option value=\"37\">37</option>\r\n" + 
					"<option value=\"38\">38</option>\r\n" + 
					"<option value=\"39\">39</option>\r\n" + 
					"<option value=\"40\">40</option>\r\n" + 
					"<option value=\"41\">41</option>\r\n" + 
					"<option value=\"42\">42</option>\r\n" + 
					"<option value=\"43\">43</option>\r\n" + 
					"<option value=\"44\">44</option>\r\n" + 
					"<option value=\"45\">45</option>\r\n" + 
					"<option value=\"46\">46</option>\r\n" + 
					"<option value=\"47\">47</option>\r\n" + 
					"<option value=\"48\">48</option>\r\n" + 
					"<option value=\"49\">49</option>\r\n" + 
					"<option value=\"50\">50</option>\r\n" + 
					"<option value=\"51\">51</option>\r\n" + 
					"<option value=\"52\">52</option>\r\n" + 
					"<option value=\"53\">53</option>\r\n" + 
					"<option value=\"54\">54</option>\r\n" + 
					"<option value=\"55\">55</option>\r\n" + 
					"<option value=\"56\">56</option>\r\n" + 
					"<option value=\"57\">57</option>\r\n" + 
					"<option value=\"58\">58</option>\r\n" + 
					"<option value=\"59\">59</option>\r\n" + 
					"<option value=\"60\">60</option>\r\n" + 
					"<option value=\"61\">61</option>\r\n" + 
					"<option value=\"62\">62</option>\r\n" + 
					"<option value=\"63\">63</option>\r\n" + 
					"<option value=\"64\">64</option>\r\n" + 
					"<option value=\"65\">65</option>\r\n" + 
					"<option value=\"66\">66</option>\r\n" + 
					"<option value=\"67\">67</option>\r\n" + 
					"<option value=\"68\">68</option>\r\n" + 
					"<option value=\"69\">69</option>\r\n" + 
					"<option value=\"70\">70</option>\r\n" + 
					"<option value=\"71\">71</option>\r\n" + 
					"<option value=\"72\">72</option>\r\n" + 
					"<option value=\"73\">73</option>\r\n" + 
					"<option value=\"74\">74</option>\r\n" + 
					"<option value=\"75\">75</option>\r\n" + 
					"<option value=\"76\">76</option>\r\n" + 
					"<option value=\"77\">77</option>\r\n" + 
					"<option value=\"78\">78</option>\r\n" + 
					"<option value=\"79\">79</option>\r\n" + 
					"<option value=\"80\">80</option>\r\n" + 
					"<option value=\"81\">81</option>\r\n" + 
					"<option value=\"82\">82</option>\r\n" + 
					"<option value=\"83\">83</option>\r\n" + 
					"<option value=\"84\">84</option>\r\n" + 
					"<option value=\"85\">85</option>\r\n" + 
					"<option value=\"86\">86</option>\r\n" + 
					"<option value=\"87\">87</option>\r\n" + 
					"<option value=\"88\">88</option>\r\n" + 
					"<option value=\"89\">89</option>\r\n" + 
					"<option value=\"90\">90</option>\r\n" + 
					"<option value=\"91\">91</option>\r\n" + 
					"<option value=\"92\">92</option>\r\n" + 
					"<option value=\"93\">93</option>\r\n" + 
					"<option value=\"94\">94</option>\r\n" + 
					"<option value=\"95\">95</option>\r\n" + 
					"<option value=\"96\">96</option>\r\n" + 
					"<option value=\"97\">97</option>\r\n" + 
					"<option value=\"98\">98</option>\r\n" + 
					"<option value=\"99\">99</option>\r\n" +
					"<option value=\"100\">100</option>");
			out.println("</select>");
			out.println(" 〜 ");
			out.println("<select class='uk-select uk-form-small uk-form-width-small' name=\"search_upperReputation\">");
			out.println("<option value=\"10000\">上限なし</option>");
			out.println("<option value=\"0\">0</option>\r\n" + 
					"<option value=\"1\">1</option>\r\n" + 
					"<option value=\"2\">2</option>\r\n" + 
					"<option value=\"3\">3</option>\r\n" + 
					"<option value=\"4\">4</option>\r\n" + 
					"<option value=\"5\">5</option>\r\n" + 
					"<option value=\"6\">6</option>\r\n" + 
					"<option value=\"7\">7</option>\r\n" + 
					"<option value=\"8\">8</option>\r\n" + 
					"<option value=\"9\">9</option>\r\n" + 
					"<option value=\"10\">10</option>\r\n" + 
					"<option value=\"11\">11</option>\r\n" + 
					"<option value=\"12\">12</option>\r\n" + 
					"<option value=\"13\">13</option>\r\n" + 
					"<option value=\"14\">14</option>\r\n" + 
					"<option value=\"15\">15</option>\r\n" + 
					"<option value=\"16\">16</option>\r\n" + 
					"<option value=\"17\">17</option>\r\n" + 
					"<option value=\"18\">18</option>\r\n" + 
					"<option value=\"19\">19</option>\r\n" + 
					"<option value=\"20\">20</option>\r\n" + 
					"<option value=\"21\">21</option>\r\n" + 
					"<option value=\"22\">22</option>\r\n" + 
					"<option value=\"23\">23</option>\r\n" + 
					"<option value=\"24\">24</option>\r\n" + 
					"<option value=\"25\">25</option>\r\n" + 
					"<option value=\"26\">26</option>\r\n" + 
					"<option value=\"27\">27</option>\r\n" + 
					"<option value=\"28\">28</option>\r\n" + 
					"<option value=\"29\">29</option>\r\n" + 
					"<option value=\"30\">30</option>\r\n" + 
					"<option value=\"31\">31</option>\r\n" + 
					"<option value=\"32\">32</option>\r\n" + 
					"<option value=\"33\">33</option>\r\n" + 
					"<option value=\"34\">34</option>\r\n" + 
					"<option value=\"35\">35</option>\r\n" + 
					"<option value=\"36\">36</option>\r\n" + 
					"<option value=\"37\">37</option>\r\n" + 
					"<option value=\"38\">38</option>\r\n" + 
					"<option value=\"39\">39</option>\r\n" + 
					"<option value=\"40\">40</option>\r\n" + 
					"<option value=\"41\">41</option>\r\n" + 
					"<option value=\"42\">42</option>\r\n" + 
					"<option value=\"43\">43</option>\r\n" + 
					"<option value=\"44\">44</option>\r\n" + 
					"<option value=\"45\">45</option>\r\n" + 
					"<option value=\"46\">46</option>\r\n" + 
					"<option value=\"47\">47</option>\r\n" + 
					"<option value=\"48\">48</option>\r\n" + 
					"<option value=\"49\">49</option>\r\n" + 
					"<option value=\"50\">50</option>\r\n" + 
					"<option value=\"51\">51</option>\r\n" + 
					"<option value=\"52\">52</option>\r\n" + 
					"<option value=\"53\">53</option>\r\n" + 
					"<option value=\"54\">54</option>\r\n" + 
					"<option value=\"55\">55</option>\r\n" + 
					"<option value=\"56\">56</option>\r\n" + 
					"<option value=\"57\">57</option>\r\n" + 
					"<option value=\"58\">58</option>\r\n" + 
					"<option value=\"59\">59</option>\r\n" + 
					"<option value=\"60\">60</option>\r\n" + 
					"<option value=\"61\">61</option>\r\n" + 
					"<option value=\"62\">62</option>\r\n" + 
					"<option value=\"63\">63</option>\r\n" + 
					"<option value=\"64\">64</option>\r\n" + 
					"<option value=\"65\">65</option>\r\n" + 
					"<option value=\"66\">66</option>\r\n" + 
					"<option value=\"67\">67</option>\r\n" + 
					"<option value=\"68\">68</option>\r\n" + 
					"<option value=\"69\">69</option>\r\n" + 
					"<option value=\"70\">70</option>\r\n" + 
					"<option value=\"71\">71</option>\r\n" + 
					"<option value=\"72\">72</option>\r\n" + 
					"<option value=\"73\">73</option>\r\n" + 
					"<option value=\"74\">74</option>\r\n" + 
					"<option value=\"75\">75</option>\r\n" + 
					"<option value=\"76\">76</option>\r\n" + 
					"<option value=\"77\">77</option>\r\n" + 
					"<option value=\"78\">78</option>\r\n" + 
					"<option value=\"79\">79</option>\r\n" + 
					"<option value=\"80\">80</option>\r\n" + 
					"<option value=\"81\">81</option>\r\n" + 
					"<option value=\"82\">82</option>\r\n" + 
					"<option value=\"83\">83</option>\r\n" + 
					"<option value=\"84\">84</option>\r\n" + 
					"<option value=\"85\">85</option>\r\n" + 
					"<option value=\"86\">86</option>\r\n" + 
					"<option value=\"87\">87</option>\r\n" + 
					"<option value=\"88\">88</option>\r\n" + 
					"<option value=\"89\">89</option>\r\n" + 
					"<option value=\"90\">90</option>\r\n" + 
					"<option value=\"91\">91</option>\r\n" + 
					"<option value=\"92\">92</option>\r\n" + 
					"<option value=\"93\">93</option>\r\n" + 
					"<option value=\"94\">94</option>\r\n" + 
					"<option value=\"95\">95</option>\r\n" + 
					"<option value=\"96\">96</option>\r\n" + 
					"<option value=\"97\">97</option>\r\n" + 
					"<option value=\"98\">98</option>\r\n" + 
					"<option value=\"99\">99</option>\r\n" +
					"<option value=\"100\">100</option>");
			out.println("</select>");
			
			out.println("<div class='uk-margin'>");
	        out.println("<select class='uk-select uk-form-small uk-form-width-medium' name=\"search_prefecture\">");
	        out.println("<option value=\"\">出身都道府県</option>");
	        out.println("<option value=\"北海道\">北海道</option>\r\n" + 
	        		"<option value=\"青森県\">青森県</option>\r\n" + 
	        		"<option value=\"岩手県\">岩手県</option>\r\n" + 
	        		"<option value=\"宮城県\">宮城県</option>\r\n" + 
	        		"<option value=\"秋田県\">秋田県</option>\r\n" + 
	        		"<option value=\"山形県\">山形県</option>\r\n" + 
	        		"<option value=\"福島県\">福島県</option>\r\n" + 
	        		"<option value=\"茨城県\">茨城県</option>\r\n" + 
	        		"<option value=\"栃木県\">栃木県</option>\r\n" + 
	        		"<option value=\"群馬県\">群馬県</option>\r\n" + 
	        		"<option value=\"埼玉県\">埼玉県</option>\r\n" + 
	        		"<option value=\"千葉県\">千葉県</option>\r\n" + 
	        		"<option value=\"東京都\">東京都</option>\r\n" + 
	        		"<option value=\"神奈川県\">神奈川県</option>\r\n" + 
	        		"<option value=\"新潟県\">新潟県</option>\r\n" + 
	        		"<option value=\"富山県\">富山県</option>\r\n" + 
	        		"<option value=\"石川県\">石川県</option>\r\n" + 
	        		"<option value=\"福井県\">福井県</option>\r\n" + 
	        		"<option value=\"山梨県\">山梨県</option>\r\n" + 
	        		"<option value=\"長野県\">長野県</option>\r\n" + 
	        		"<option value=\"岐阜県\">岐阜県</option>\r\n" + 
	        		"<option value=\"静岡県\">静岡県</option>\r\n" + 
	        		"<option value=\"愛知県\">愛知県</option>\r\n" + 
	        		"<option value=\"三重県\">三重県</option>\r\n" + 
	        		"<option value=\"滋賀県\">滋賀県</option>\r\n" + 
	        		"<option value=\"京都府\">京都府</option>\r\n" + 
	        		"<option value=\"大阪府\">大阪府</option>\r\n" + 
	        		"<option value=\"兵庫県\">兵庫県</option>\r\n" + 
	        		"<option value=\"奈良県\">奈良県</option>\r\n" + 
	        		"<option value=\"和歌山県\">和歌山県</option>\r\n" + 
	        		"<option value=\"鳥取県\">鳥取県</option>\r\n" + 
	        		"<option value=\"島根県\">島根県</option>\r\n" + 
	        		"<option value=\"岡山県\">岡山県</option>\r\n" + 
	        		"<option value=\"広島県\">広島県</option>\r\n" + 
	        		"<option value=\"山口県\">山口県</option>\r\n" + 
	        		"<option value=\"徳島県\">徳島県</option>\r\n" + 
	        		"<option value=\"香川県\">香川県</option>\r\n" + 
	        		"<option value=\"愛媛県\">愛媛県</option>\r\n" + 
	        		"<option value=\"高知県\">高知県</option>\r\n" + 
	        		"<option value=\"福岡県\">福岡県</option>\r\n" + 
	        		"<option value=\"佐賀県\">佐賀県</option>\r\n" + 
	        		"<option value=\"長崎県\">長崎県</option>\r\n" + 
	        		"<option value=\"熊本県\">熊本県</option>\r\n" + 
	        		"<option value=\"大分県\">大分県</option>\r\n" + 
	        		"<option value=\"宮崎県\">宮崎県</option>\r\n" + 
	        		"<option value=\"鹿児島県\">鹿児島県</option>\r\n" + 
	        		"<option value=\"沖縄県\">沖縄県</option>");
	        out.println("</select>");
	        out.println("</div>");
	        
	        out.println("<input class='uk-button uk-button-primary uk-button-small' type=\"submit\" onclick=\"return test()\" value=\"検索\"/>");
			out.println("</form>");
			out.println("</div>");
		}
		
		if(about.equals("actor")){
			out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
			out.println("<h3 class='uk-text-lead'>追加</h3>");
			out.println("<form action=\"add\" method=\"GET\">");
			out.println("<input class='uk-input uk-form-small uk-form-width-medium uk-margin' type=\"text\" name=\"add_actorname\" placeholder=\"俳優名\"/>");
			out.println("<br/>");
			out.println("<select class='uk-select uk-form-small uk-form-width-medium' name=\"add_reputation\">");
			out.println("<option value=\"\">評価</option>");
			out.println("<option value=\"0\">0</option>\r\n" + 
					"<option value=\"1\">1</option>\r\n" + 
					"<option value=\"2\">2</option>\r\n" + 
					"<option value=\"3\">3</option>\r\n" + 
					"<option value=\"4\">4</option>\r\n" + 
					"<option value=\"5\">5</option>\r\n" + 
					"<option value=\"6\">6</option>\r\n" + 
					"<option value=\"7\">7</option>\r\n" + 
					"<option value=\"8\">8</option>\r\n" + 
					"<option value=\"9\">9</option>\r\n" + 
					"<option value=\"10\">10</option>\r\n" + 
					"<option value=\"11\">11</option>\r\n" + 
					"<option value=\"12\">12</option>\r\n" + 
					"<option value=\"13\">13</option>\r\n" + 
					"<option value=\"14\">14</option>\r\n" + 
					"<option value=\"15\">15</option>\r\n" + 
					"<option value=\"16\">16</option>\r\n" + 
					"<option value=\"17\">17</option>\r\n" + 
					"<option value=\"18\">18</option>\r\n" + 
					"<option value=\"19\">19</option>\r\n" + 
					"<option value=\"20\">20</option>\r\n" + 
					"<option value=\"21\">21</option>\r\n" + 
					"<option value=\"22\">22</option>\r\n" + 
					"<option value=\"23\">23</option>\r\n" + 
					"<option value=\"24\">24</option>\r\n" + 
					"<option value=\"25\">25</option>\r\n" + 
					"<option value=\"26\">26</option>\r\n" + 
					"<option value=\"27\">27</option>\r\n" + 
					"<option value=\"28\">28</option>\r\n" + 
					"<option value=\"29\">29</option>\r\n" + 
					"<option value=\"30\">30</option>\r\n" + 
					"<option value=\"31\">31</option>\r\n" + 
					"<option value=\"32\">32</option>\r\n" + 
					"<option value=\"33\">33</option>\r\n" + 
					"<option value=\"34\">34</option>\r\n" + 
					"<option value=\"35\">35</option>\r\n" + 
					"<option value=\"36\">36</option>\r\n" + 
					"<option value=\"37\">37</option>\r\n" + 
					"<option value=\"38\">38</option>\r\n" + 
					"<option value=\"39\">39</option>\r\n" + 
					"<option value=\"40\">40</option>\r\n" + 
					"<option value=\"41\">41</option>\r\n" + 
					"<option value=\"42\">42</option>\r\n" + 
					"<option value=\"43\">43</option>\r\n" + 
					"<option value=\"44\">44</option>\r\n" + 
					"<option value=\"45\">45</option>\r\n" + 
					"<option value=\"46\">46</option>\r\n" + 
					"<option value=\"47\">47</option>\r\n" + 
					"<option value=\"48\">48</option>\r\n" + 
					"<option value=\"49\">49</option>\r\n" + 
					"<option value=\"50\">50</option>\r\n" + 
					"<option value=\"51\">51</option>\r\n" + 
					"<option value=\"52\">52</option>\r\n" + 
					"<option value=\"53\">53</option>\r\n" + 
					"<option value=\"54\">54</option>\r\n" + 
					"<option value=\"55\">55</option>\r\n" + 
					"<option value=\"56\">56</option>\r\n" + 
					"<option value=\"57\">57</option>\r\n" + 
					"<option value=\"58\">58</option>\r\n" + 
					"<option value=\"59\">59</option>\r\n" + 
					"<option value=\"60\">60</option>\r\n" + 
					"<option value=\"61\">61</option>\r\n" + 
					"<option value=\"62\">62</option>\r\n" + 
					"<option value=\"63\">63</option>\r\n" + 
					"<option value=\"64\">64</option>\r\n" + 
					"<option value=\"65\">65</option>\r\n" + 
					"<option value=\"66\">66</option>\r\n" + 
					"<option value=\"67\">67</option>\r\n" + 
					"<option value=\"68\">68</option>\r\n" + 
					"<option value=\"69\">69</option>\r\n" + 
					"<option value=\"70\">70</option>\r\n" + 
					"<option value=\"71\">71</option>\r\n" + 
					"<option value=\"72\">72</option>\r\n" + 
					"<option value=\"73\">73</option>\r\n" + 
					"<option value=\"74\">74</option>\r\n" + 
					"<option value=\"75\">75</option>\r\n" + 
					"<option value=\"76\">76</option>\r\n" + 
					"<option value=\"77\">77</option>\r\n" + 
					"<option value=\"78\">78</option>\r\n" + 
					"<option value=\"79\">79</option>\r\n" + 
					"<option value=\"80\">80</option>\r\n" + 
					"<option value=\"81\">81</option>\r\n" + 
					"<option value=\"82\">82</option>\r\n" + 
					"<option value=\"83\">83</option>\r\n" + 
					"<option value=\"84\">84</option>\r\n" + 
					"<option value=\"85\">85</option>\r\n" + 
					"<option value=\"86\">86</option>\r\n" + 
					"<option value=\"87\">87</option>\r\n" + 
					"<option value=\"88\">88</option>\r\n" + 
					"<option value=\"89\">89</option>\r\n" + 
					"<option value=\"90\">90</option>\r\n" + 
					"<option value=\"91\">91</option>\r\n" + 
					"<option value=\"92\">92</option>\r\n" + 
					"<option value=\"93\">93</option>\r\n" + 
					"<option value=\"94\">94</option>\r\n" + 
					"<option value=\"95\">95</option>\r\n" + 
					"<option value=\"96\">96</option>\r\n" + 
					"<option value=\"97\">97</option>\r\n" + 
					"<option value=\"98\">98</option>\r\n" + 
					"<option value=\"99\">99</option>\r\n" +
					"<option value=\"100\">100</option>");
			out.println("</select>");
			//out.println("出身都道府県： ");
			//out.println("<input type=\"text\" name=\"add_prefecture\"/>");
			out.println("<div class='uk-margin'>");
	        out.println("<select class='uk-select uk-form-small uk-form-width-medium' name=\"add_prefecture\">");
	        out.println("<option value=\"\">出身都道府県</option>");
	        out.println("<option value=\"北海道\">北海道</option>\r\n" + 
	        		"<option value=\"青森県\">青森県</option>\r\n" + 
	        		"<option value=\"岩手県\">岩手県</option>\r\n" + 
	        		"<option value=\"宮城県\">宮城県</option>\r\n" + 
	        		"<option value=\"秋田県\">秋田県</option>\r\n" + 
	        		"<option value=\"山形県\">山形県</option>\r\n" + 
	        		"<option value=\"福島県\">福島県</option>\r\n" + 
	        		"<option value=\"茨城県\">茨城県</option>\r\n" + 
	        		"<option value=\"栃木県\">栃木県</option>\r\n" + 
	        		"<option value=\"群馬県\">群馬県</option>\r\n" + 
	        		"<option value=\"埼玉県\">埼玉県</option>\r\n" + 
	        		"<option value=\"千葉県\">千葉県</option>\r\n" + 
	        		"<option value=\"東京都\">東京都</option>\r\n" + 
	        		"<option value=\"神奈川県\">神奈川県</option>\r\n" + 
	        		"<option value=\"新潟県\">新潟県</option>\r\n" + 
	        		"<option value=\"富山県\">富山県</option>\r\n" + 
	        		"<option value=\"石川県\">石川県</option>\r\n" + 
	        		"<option value=\"福井県\">福井県</option>\r\n" + 
	        		"<option value=\"山梨県\">山梨県</option>\r\n" + 
	        		"<option value=\"長野県\">長野県</option>\r\n" + 
	        		"<option value=\"岐阜県\">岐阜県</option>\r\n" + 
	        		"<option value=\"静岡県\">静岡県</option>\r\n" + 
	        		"<option value=\"愛知県\">愛知県</option>\r\n" + 
	        		"<option value=\"三重県\">三重県</option>\r\n" + 
	        		"<option value=\"滋賀県\">滋賀県</option>\r\n" + 
	        		"<option value=\"京都府\">京都府</option>\r\n" + 
	        		"<option value=\"大阪府\">大阪府</option>\r\n" + 
	        		"<option value=\"兵庫県\">兵庫県</option>\r\n" + 
	        		"<option value=\"奈良県\">奈良県</option>\r\n" + 
	        		"<option value=\"和歌山県\">和歌山県</option>\r\n" + 
	        		"<option value=\"鳥取県\">鳥取県</option>\r\n" + 
	        		"<option value=\"島根県\">島根県</option>\r\n" + 
	        		"<option value=\"岡山県\">岡山県</option>\r\n" + 
	        		"<option value=\"広島県\">広島県</option>\r\n" + 
	        		"<option value=\"山口県\">山口県</option>\r\n" + 
	        		"<option value=\"徳島県\">徳島県</option>\r\n" + 
	        		"<option value=\"香川県\">香川県</option>\r\n" + 
	        		"<option value=\"愛媛県\">愛媛県</option>\r\n" + 
	        		"<option value=\"高知県\">高知県</option>\r\n" + 
	        		"<option value=\"福岡県\">福岡県</option>\r\n" + 
	        		"<option value=\"佐賀県\">佐賀県</option>\r\n" + 
	        		"<option value=\"長崎県\">長崎県</option>\r\n" + 
	        		"<option value=\"熊本県\">熊本県</option>\r\n" + 
	        		"<option value=\"大分県\">大分県</option>\r\n" + 
	        		"<option value=\"宮崎県\">宮崎県</option>\r\n" + 
	        		"<option value=\"鹿児島県\">鹿児島県</option>\r\n" + 
	        		"<option value=\"沖縄県\">沖縄県</option>");
	        out.println("</select>");
	        //out.println("<button class=\"uk-button uk-button-default\" type=\"button\" tabindex=\"-1\">");
	        //out.println("<span></span>");
	        //out.println("<span uk-icon=\"icon: chevron-down\"></span>");
	        //out.println("</button>");
	        out.println("</div>");
	        //out.println("</div>");
			out.println("<input class='uk-input uk-form-small uk-form-width-small' type=\"text\" name=\"add_height\" placeholder=\"身長\"/> cm");	
			out.println("<br/>");
			out.println("<div class='uk-margin'>");
			out.println("生年月日： ");
			out.println("<select name=\"add_year\" class='uk-select uk-form-small uk-form-width-xsmall'>\r\n" + 
					"<option value=\"\"></option>\r\n" + 
					"<option value=\"1900\">1900</option>\r\n" + 
					"<option value=\"1901\">1901</option>\r\n" + 
					"<option value=\"1902\">1902</option>\r\n" + 
					"<option value=\"1903\">1903</option>\r\n" + 
					"<option value=\"1904\">1904</option>\r\n" + 
					"<option value=\"1905\">1905</option>\r\n" + 
					"<option value=\"1906\">1906</option>\r\n" + 
					"<option value=\"1907\">1907</option>\r\n" + 
					"<option value=\"1908\">1908</option>\r\n" + 
					"<option value=\"1909\">1909</option>\r\n" + 
					"<option value=\"1910\">1910</option>\r\n" + 
					"<option value=\"1911\">1911</option>\r\n" + 
					"<option value=\"1912\">1912</option>\r\n" + 
					"<option value=\"1913\">1913</option>\r\n" + 
					"<option value=\"1914\">1914</option>\r\n" + 
					"<option value=\"1915\">1915</option>\r\n" + 
					"<option value=\"1916\">1916</option>\r\n" + 
					"<option value=\"1917\">1917</option>\r\n" + 
					"<option value=\"1918\">1918</option>\r\n" + 
					"<option value=\"1919\">1919</option>\r\n" + 
					"<option value=\"1920\">1920</option>\r\n" + 
					"<option value=\"1921\">1921</option>\r\n" + 
					"<option value=\"1922\">1922</option>\r\n" + 
					"<option value=\"1923\">1923</option>\r\n" + 
					"<option value=\"1924\">1924</option>\r\n" + 
					"<option value=\"1925\">1925</option>\r\n" + 
					"<option value=\"1926\">1926</option>\r\n" + 
					"<option value=\"1927\">1927</option>\r\n" + 
					"<option value=\"1928\">1928</option>\r\n" + 
					"<option value=\"1929\">1929</option>\r\n" + 
					"<option value=\"1930\">1930</option>\r\n" + 
					"<option value=\"1931\">1931</option>\r\n" + 
					"<option value=\"1932\">1932</option>\r\n" + 
					"<option value=\"1933\">1933</option>\r\n" + 
					"<option value=\"1934\">1934</option>\r\n" + 
					"<option value=\"1935\">1935</option>\r\n" + 
					"<option value=\"1936\">1936</option>\r\n" + 
					"<option value=\"1937\">1937</option>\r\n" + 
					"<option value=\"1938\">1938</option>\r\n" + 
					"<option value=\"1939\">1939</option>\r\n" + 
					"<option value=\"1940\">1940</option>\r\n" + 
					"<option value=\"1941\">1941</option>\r\n" + 
					"<option value=\"1942\">1942</option>\r\n" + 
					"<option value=\"1943\">1943</option>\r\n" + 
					"<option value=\"1944\">1944</option>\r\n" + 
					"<option value=\"1945\">1945</option>\r\n" + 
					"<option value=\"1946\">1946</option>\r\n" + 
					"<option value=\"1947\">1947</option>\r\n" + 
					"<option value=\"1948\">1948</option>\r\n" + 
					"<option value=\"1949\">1949</option>\r\n" + 
					"<option value=\"1950\">1950</option>\r\n" + 
					"<option value=\"1951\">1951</option>\r\n" + 
					"<option value=\"1952\">1952</option>\r\n" + 
					"<option value=\"1953\">1953</option>\r\n" + 
					"<option value=\"1954\">1954</option>\r\n" + 
					"<option value=\"1955\">1955</option>\r\n" + 
					"<option value=\"1956\">1956</option>\r\n" + 
					"<option value=\"1957\">1957</option>\r\n" + 
					"<option value=\"1958\">1958</option>\r\n" + 
					"<option value=\"1959\">1959</option>\r\n" + 
					"<option value=\"1960\">1960</option>\r\n" + 
					"<option value=\"1961\">1961</option>\r\n" + 
					"<option value=\"1962\">1962</option>\r\n" + 
					"<option value=\"1963\">1963</option>\r\n" + 
					"<option value=\"1964\">1964</option>\r\n" + 
					"<option value=\"1965\">1965</option>\r\n" + 
					"<option value=\"1966\">1966</option>\r\n" + 
					"<option value=\"1967\">1967</option>\r\n" + 
					"<option value=\"1968\">1968</option>\r\n" + 
					"<option value=\"1969\">1969</option>\r\n" + 
					"<option value=\"1970\">1970</option>\r\n" + 
					"<option value=\"1971\">1971</option>\r\n" + 
					"<option value=\"1972\">1972</option>\r\n" + 
					"<option value=\"1973\">1973</option>\r\n" + 
					"<option value=\"1974\">1974</option>\r\n" + 
					"<option value=\"1975\">1975</option>\r\n" + 
					"<option value=\"1976\">1976</option>\r\n" + 
					"<option value=\"1977\">1977</option>\r\n" + 
					"<option value=\"1978\">1978</option>\r\n" + 
					"<option value=\"1979\">1979</option>\r\n" + 
					"<option value=\"1980\">1980</option>\r\n" + 
					"<option value=\"1981\">1981</option>\r\n" + 
					"<option value=\"1982\">1982</option>\r\n" + 
					"<option value=\"1983\">1983</option>\r\n" + 
					"<option value=\"1984\">1984</option>\r\n" + 
					"<option value=\"1985\">1985</option>\r\n" + 
					"<option value=\"1986\">1986</option>\r\n" + 
					"<option value=\"1987\">1987</option>\r\n" + 
					"<option value=\"1988\">1988</option>\r\n" + 
					"<option value=\"1989\">1989</option>\r\n" + 
					"<option value=\"1990\">1990</option>\r\n" + 
					"<option value=\"1991\">1991</option>\r\n" + 
					"<option value=\"1992\">1992</option>\r\n" + 
					"<option value=\"1993\">1993</option>\r\n" + 
					"<option value=\"1994\">1994</option>\r\n" + 
					"<option value=\"1995\">1995</option>\r\n" + 
					"<option value=\"1996\">1996</option>\r\n" + 
					"<option value=\"1997\">1997</option>\r\n" + 
					"<option value=\"1998\">1998</option>\r\n" + 
					"<option value=\"1999\">1999</option>\r\n" + 
					"<option value=\"2000\">2000</option>\r\n" + 
					"<option value=\"2001\">2001</option>\r\n" + 
					"<option value=\"2002\">2002</option>\r\n" + 
					"<option value=\"2003\">2003</option>\r\n" + 
					"<option value=\"2004\">2004</option>\r\n" + 
					"<option value=\"2005\">2005</option>\r\n" + 
					"<option value=\"2006\">2006</option>\r\n" + 
					"<option value=\"2007\">2007</option>\r\n" + 
					"<option value=\"2008\">2008</option>\r\n" + 
					"<option value=\"2009\">2009</option>\r\n" + 
					"<option value=\"2010\">2010</option>\r\n" + 
					"<option value=\"2011\">2011</option>\r\n" + 
					"<option value=\"2012\">2012</option>\r\n" + 
					"<option value=\"2013\">2013</option>\r\n" + 
					"<option value=\"2014\">2014</option>\r\n" + 
					"<option value=\"2015\">2015</option>\r\n" + 
					"<option value=\"2016\">2016</option>\r\n" + 
					"<option value=\"2017\">2017</option>\r\n" + 
					"<option value=\"2018\">2018</option>\r\n" + 
					"<option value=\"2019\">2019</option>\r\n" + 
					"<option value=\"2020\">2020</option>\r\n" + 
					"<option value=\"2021\">2021</option>\r\n" + 
					"<option value=\"2022\">2022</option>\r\n" + 
					"<option value=\"2023\">2023</option>\r\n" + 
					"<option value=\"2024\">2024</option>\r\n" + 
					"<option value=\"2025\">2025</option>\r\n" + 
					"<option value=\"2026\">2026</option>\r\n" + 
					"<option value=\"2027\">2027</option>\r\n" + 
					"<option value=\"2028\">2028</option>\r\n" + 
					"<option value=\"2029\">2029</option>\r\n" + 
					"<option value=\"2030\">2030</option>\r\n" + 
					"</select>年"
					+ "<select name=\"add_month\" class='uk-select uk-form-small uk-form-width-xsmall'>\r\n" + 
					"<option value=\"\"></option>\r\n" + 
					"<option value=\"01\">1</option>\r\n" + 
					"<option value=\"02\">2</option>\r\n" + 
					"<option value=\"03\">3</option>\r\n" + 
					"<option value=\"04\">4</option>\r\n" + 
					"<option value=\"05\">5</option>\r\n" + 
					"<option value=\"06\">6</option>\r\n" + 
					"<option value=\"07\">7</option>\r\n" + 
					"<option value=\"08\">8</option>\r\n" + 
					"<option value=\"09\">9</option>\r\n" + 
					"<option value=\"10\">10</option>\r\n" + 
					"<option value=\"11\">11</option>\r\n" + 
					"<option value=\"12\">12</option>\r\n" + 
					"</select>月" + "<select name=\"add_day\" class='uk-select uk-form-small uk-form-width-xsmall'>\r\n" + 
							"<option value=\"\"></option>\r\n" + 
							"<option value=\"01\">1</option>\r\n" + 
							"<option value=\"02\">2</option>\r\n" + 
							"<option value=\"03\">3</option>\r\n" + 
							"<option value=\"04\">4</option>\r\n" + 
							"<option value=\"05\">5</option>\r\n" + 
							"<option value=\"06\">6</option>\r\n" + 
							"<option value=\"07\">7</option>\r\n" + 
							"<option value=\"08\">8</option>\r\n" + 
							"<option value=\"09\">9</option>\r\n" + 
							"<option value=\"10\">10</option>\r\n" + 
							"<option value=\"11\">11</option>\r\n" + 
							"<option value=\"12\">12</option>\r\n" + 
							"<option value=\"13\">13</option>\r\n" + 
							"<option value=\"14\">14</option>\r\n" + 
							"<option value=\"15\">15</option>\r\n" + 
							"<option value=\"16\">16</option>\r\n" + 
							"<option value=\"17\">17</option>\r\n" + 
							"<option value=\"18\">18</option>\r\n" + 
							"<option value=\"19\">19</option>\r\n" + 
							"<option value=\"20\">20</option>\r\n" + 
							"<option value=\"21\">21</option>\r\n" + 
							"<option value=\"22\">22</option>\r\n" + 
							"<option value=\"23\">23</option>\r\n" + 
							"<option value=\"24\">24</option>\r\n" + 
							"<option value=\"25\">25</option>\r\n" + 
							"<option value=\"26\">26</option>\r\n" + 
							"<option value=\"27\">27</option>\r\n" + 
							"<option value=\"28\">28</option>\r\n" + 
							"<option value=\"29\">29</option>\r\n" + 
							"<option value=\"30\">30</option>\r\n" + 
							"<option value=\"31\">31</option>\r\n" + 
							"</select>日"
					);
			out.println("</div>");
			out.println("<input class='uk-button uk-button-primary uk-button-small' type=\"submit\" value=\"追加\"/>");
			out.println("</form>");
			out.println("</div>");
		}
		if(about.equals("actor")||about.equals("works")) {
			out.println("<div class=\"uk-card uk-card-default uk-card-body uk-card-hover uk-margin-small\">");
			out.println("<h3 class='uk-text-lead'>総合検索</h3>");
			out.println("<form action=\"result\" method=\"GET\">");
			out.println("俳優名or作品名： ");
			out.println("<input type=\"text\" name=\"search_name\"/>");
			out.println("<br/>");
			out.println("<input class='uk-button uk-button-primary uk-button-small uk-margin' type=\"submit\" value=\"検索\"/>");
			out.println("</form>");
			out.println("</div>");
		}
		
		out.println("</div>");
		out.println("</div>");
	
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
