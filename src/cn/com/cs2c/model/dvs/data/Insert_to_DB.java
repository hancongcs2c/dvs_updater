package cn.com.cs2c.model.dvs.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class Insert_to_DB {

	public Connection conn = null;
	public Statement stmt = null;

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static int insert_to_mysqldb(File file) throws ParseException {
		String community;
		String project;
		Connection conn = null;
		Statement stmt = null;

		// 根据文件名称获取community和project
		String[] sArray1 = file.getName().split("\\.");
		String[] ssArray = sArray1[0].split("-");
		community = ssArray[0];
		project = ssArray[1];
		//打印
		System.out.println(community + "-" + project
				+ " START...");
		// 清空就的数据
		try {
			conn = new DataDAO().getConnection();
			stmt = conn.createStatement();

			String sql = "DELETE  FROM dvs_data WHERE"
					+ " dvs_data.community = '" + community
					+ "' AND dvs_data.project='" + project + "'";

			stmt.execute(sql);

			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(
					file.getAbsolutePath()));
			String line = reader.readLine();
			line = reader.readLine();
			//打印，这是最需要打印的信息
			
			while (line != null) {
				System.out.println(".");
				String[] sArray = line.split(",");

				String name = sArray[0].substring(1, sArray[0].length() - 1);
				String email = sArray[1].substring(1, sArray[1].length() - 1);
				String affliation = sArray[2].substring(1,sArray[2].length() - 1);
				if(email.contains("cs2c")){
					affliation="cs2c";
				}
				String date = sArray[3].substring(1, sArray[3].length() - 1);

				int added = Integer.parseInt(sArray[4]);
				int removed = Integer.parseInt(sArray[5]);
				int changeset = Integer.parseInt(sArray[6]);
				name=name.replaceAll("'", "");
				String sql1 = "insert into dvs_data(community, project,name,"
						+ "email,affliation,date,added,"
						+ "removed,changeset) values('" + community + "','"
						+ project + "','" + name + "','"
						+ email + "','" + affliation + "','" + date + "','"
						+ added + "','" + removed + "','" + changeset + "')";
				stmt.executeUpdate(sql1);

				line = reader.readLine();
			}

			//打印
			System.out.println("..." + community + "-" + project
					+ " END");
			System.out.println("\n\n");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

}
