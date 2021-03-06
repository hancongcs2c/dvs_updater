package cn.com.cs2c.model.dvs.data;

import java.sql.*;
import java.util.Properties;
import java.io.*;

public class DataDAO {

	public static String dburl;
	public static String username;
	public static String password;
	private Connection conn = null;
	public static String dir = null;
	public static String driver;

	static {

		try {
			Properties props = new Properties();
			props.load(new FileInputStream("jdbc.properties"));
			driver = props.getProperty("driver");
			dburl = props.getProperty("dburl");
			username = props.getProperty("username");
			password = props.getProperty("password");
			dir = props.getProperty("data_dir");
			Class.forName(driver);
		} catch (Exception e) {
			//打印信息
			System.out.print(e.toString());
		}
	}

	public DataDAO() {

	}

	public Connection getConnection() throws SQLException {

		if (conn == null)
			conn = DriverManager.getConnection(dburl, username, password);
		return conn;
	}

	public static File[] getFiles() {
		File file = new File(dir);
		File[] filenames = file.listFiles();

		return filenames;
	}

}
