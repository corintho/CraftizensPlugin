import java.io.*;
import java.sql.*;
import java.util.Map;
import java.util.logging.Logger;

public final class iData implements Serializable {
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static PropertiesFile accounts;
	private static int startingBalance;

	// Serial
	private static final long serialVersionUID = -5796481236376288855L;

	// Database
	static boolean mysql = false;
	static String driver = "com.mysql.jdbc.Driver";
	static String user = "root";
	static String pass = "root";
	static String db = "jdbc:mysql://localhost:3306/minecraft";

	// Directories
	static String mainDir = "iConomy/";
	static String logDir = "logs/";

	public static void setup(boolean mysql, int balance, String driver, String user, String pass, String db) {
		startingBalance = balance;

		// Database
		iData.driver = driver;
		iData.user = user;
		iData.pass = pass;
		iData.db = db;

		if (!mysql) {
			accounts = new PropertiesFile(mainDir + "balances.properties");
		} else {
			// MySQL
			iData.mysql = true;

			try {
				Class.forName(driver);
			} catch (ClassNotFoundException ex) {
				log.severe("[iConomy MySQL] Unable to find driver class " + driver);
			}
		}
	}

	public static Connection MySQL() {
		try {
			return DriverManager.getConnection(db,user,pass);
		} catch (SQLException ex) {
			log.severe("[iConomy MySQL] Unable to retreive MySQL connection");
		}

		return null;
	}

	public static int globalBalance() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int current = 0;

		if (mysql) {
			try {
				conn = MySQL();
				ps = conn.prepareStatement("SELECT balance FROM iBalances");
				rs = ps.executeQuery();

				while(rs.next()) {
					current += rs.getInt("balance");
				}

				return current;
			} catch (SQLException ex) {
				return 0;
			} finally {
				try {
					if (ps != null) { ps.close(); }
					if (rs != null) { rs.close(); }
					if (conn != null) { conn.close(); }
				} catch (SQLException ex) { }
			}
		} else {
			Map<String, String> balances;

			try {
				balances = accounts.returnMap();
			} catch (Exception ex) {
				log.info("[iConomy] Listing failed for accounts.");
				return 0;
			}

			for (Object key: balances.keySet()) {
				int balance = Integer.parseInt((String)balances.get(key));
				current += balance;
			}

			return current;
		}
	}

	public static boolean hasBalance(String playerName) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean has = false;

		if (mysql) {
			try {
				conn = MySQL();
				ps = conn.prepareStatement("SELECT balance FROM iBalances WHERE player = ? LIMIT 1");
				ps.setString(1, playerName);
				rs = ps.executeQuery();

				has = (rs.next()) ? true : false;
			} catch (SQLException ex) {
				log.severe("[iConomy] Unable to grab the balance for [" + playerName + "] from database!");
			} finally {
				try {
					if (ps != null) { ps.close(); }
					if (rs != null) { rs.close(); }
					if (conn != null) { conn.close(); }
				} catch (SQLException ex) { }
			}
		} else {
			return (accounts.getInt(playerName) != 0) ? true : false;
		}

		return has;
	}

	public static int getBalance(String playerName) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int balance = startingBalance;

		if (mysql) {
			try {
				conn = MySQL();
				ps = conn.prepareStatement("SELECT balance FROM iBalances WHERE player = ? LIMIT 1");
				ps.setString(1, playerName);
				rs = ps.executeQuery();

				if (rs.next()) {
					balance = rs.getInt("balance");
				} else {
					ps = conn.prepareStatement("INSERT INTO iBalances (player, balance) VALUES(?,?)");
					ps.setString(1, playerName);
					ps.setInt(2, balance);
					ps.executeUpdate();
				}
			} catch (SQLException ex) {
				log.severe("[iConomy] Unable to grab the balance for [" + playerName + "] from database!");
			} finally {
				try {
					if (ps != null) { ps.close(); }
					if (rs != null) { rs.close(); }
					if (conn != null) { conn.close(); }
				} catch (SQLException ex) { }
			}
		} else {
			// To work with plugins we must do this.
			try {
				accounts.load();
			} catch (IOException ex) {
				log.severe("[iConomy] Unable to reload balances.");
			}

			// Return the balance
			return (hasBalance(playerName)) ? accounts.getInt(playerName) : accounts.getInt(playerName, startingBalance);
		}

		return balance;
	}

	public static void setBalance(String playerName, int balance) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		if (mysql) {
			try {
				conn = MySQL();

				if (hasBalance(playerName)) {
					ps = conn.prepareStatement("UPDATE iBalances SET balance = ? WHERE player = ? LIMIT 1", Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, balance);
					ps.setString(2, playerName);
					ps.executeUpdate();
				} else {
					ps = conn.prepareStatement("INSERT INTO iBalances (player, balance) VALUES(?,?)");
					ps.setString(1, playerName);
					ps.setInt(2, balance);
					ps.executeUpdate();
				}
			} catch (SQLException ex) {
				log.severe("[iConomy] Unable to update or create the balance for [" + playerName + "] from database!");
			} finally {
				try {
					if (ps != null) { ps.close(); }
					if (rs != null) { rs.close(); }
					if (conn != null) { conn.close(); }
				} catch (SQLException ex) { }
			}
		} else {
			accounts.setInt(playerName, balance);
		}
	}
}
