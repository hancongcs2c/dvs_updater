package cn.com.cs2c.model.dvs.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton updateBtn = new JButton("Update");
	private JButton chooseDirBtn = new JButton("Data Location");
	private JButton exitBtn = new JButton("Exit");
	private JLabel label_driver = new JLabel("Driver :", SwingConstants.RIGHT);
	private JLabel label_db = new JLabel("DB Url :", SwingConstants.RIGHT);
	private JLabel label_name = new JLabel("User Name:", SwingConstants.RIGHT);
	private JLabel label_passwd = new JLabel("Password:", SwingConstants.RIGHT);
	private JLabel label_dir = new JLabel("Data Directory:",
			SwingConstants.RIGHT);
	private JLabel title = new JLabel("DVS Data-Updater");

	private static JTextField jtf_dirver = new JTextField(80);
	private static JTextField jtf_db = new JTextField(80);
	private static JTextField jtf_name = new JTextField(80);
	private static JTextField jtf_passwd = new JTextField(80);
	private static JTextField jtf_dir = new JTextField(80);

	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel panel3 = new JPanel();
	private JPanel panel4 = new JPanel();
	private JPanel panel5 = new JPanel();

	//构造方法中创建框架窗口
	public Gui() {
		super("Update the DVS data");
		//组件之间的水平间隔和垂直间隔距离，单位像素
		this.setLayout(new BorderLayout(10, 10));
		panel1.setLayout(new GridLayout(6, 3, 10, 35));
		panel2.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel1.add(label_driver);
		panel1.add(jtf_dirver);
		panel1.add(label_db);
		panel1.add(jtf_db);
		panel1.add(label_name);
		panel1.add(jtf_name);
		panel1.add(label_passwd);
		panel1.add(jtf_passwd);
		panel1.add(label_dir);
		panel1.add(jtf_dir);
		panel2.add(chooseDirBtn);
		panel2.add(updateBtn);
		panel2.add(exitBtn);
		panel3.add(title);
		this.add(panel1, BorderLayout.CENTER);
		this.add(panel2, BorderLayout.SOUTH);
		this.add(panel3, BorderLayout.NORTH);
		this.add(panel4, BorderLayout.EAST);
		this.add(panel5, BorderLayout.WEST);
		this.init_jtf();
		AL_Button button_listener = new AL_Button();
		this.updateBtn.addActionListener(button_listener);
		this.chooseDirBtn.addActionListener(button_listener);
		this.exitBtn.addActionListener(button_listener);

		this.setSize(750, 450);
		this.setLocation(250, 150);
		this.setResizable(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void init_jtf() {
		String dburl;
		String username;
		String password;
		String driver;
		String dir;
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("jdbc.properties"));
			driver = props.getProperty("driver");
			dburl = props.getProperty("dburl");
			username = props.getProperty("username");
			password = props.getProperty("password");
			dir = props.getProperty("data_dir");

			Gui.jtf_dirver.setText(driver);
			Gui.jtf_dir.setText(dir);
			Gui.jtf_name.setText(username);
			Gui.jtf_passwd.setText(password);
			Gui.jtf_db.setText(dburl);

		} catch (Exception e) {
			//打印信息
			System.out.print(e.toString());
		}

	}

	public static void setProperties() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("jdbc.properties"));
			props.setProperty("driver", Gui.jtf_dirver.getText());
			props.setProperty("dburl", Gui.jtf_db.getText());
			props.setProperty("username", Gui.jtf_name.getText());
			props.setProperty("password", Gui.jtf_passwd.getText());
			props.setProperty("data_dir", Gui.jtf_dir.getText());
			props.store(new FileOutputStream("jdbc.properties"),
					"new properties");

			DataDAO.dir = Gui.jtf_dir.getText();
			DataDAO.dburl = Gui.jtf_db.getText();
			DataDAO.driver = Gui.jtf_dirver.getText();
			DataDAO.password = Gui.jtf_passwd.getText();
			DataDAO.username = Gui.jtf_name.getText();

		} catch (Exception e) {
			//打印信息
			System.out.print(e.toString());
		}

	}

	class AL_Button implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ((JButton) e.getSource() == updateBtn) {

				int have_file = 0;

				Gui.setProperties();

				File[] files = DataDAO.getFiles();
				for (int i = 0; i < files.length; i++) {
					if (!files[i].isDirectory()
							&& files[i].getName().endsWith("csv")) {
						try {
							have_file = 1;
							if (-1 == Insert_to_DB.insert_to_mysqldb(files[i])) {
								have_file = 2;
							}
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
				}
				if (have_file == 1)
					JOptionPane.showMessageDialog(Gui.this, "数据更新完成", "更新",
							JOptionPane.INFORMATION_MESSAGE);
				else if (have_file == 0)
					JOptionPane.showMessageDialog(Gui.this, "指定目录中没有数据文件",
							"错误", JOptionPane.WARNING_MESSAGE);
				else if (have_file == 2)
					JOptionPane.showMessageDialog(Gui.this, "数据库连接错误", "错误",
							JOptionPane.WARNING_MESSAGE);

			} else if ((JButton) e.getSource() == chooseDirBtn) {
				JFileChooser fc = new JFileChooser("./");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int intRetVal = fc.showOpenDialog(jtf_dir);
				if (intRetVal == JFileChooser.APPROVE_OPTION) {
					Gui.jtf_dir.setText(fc.getSelectedFile().getPath());
				}

			} else if ((JButton) e.getSource() == exitBtn) {
				System.exit(0);
			}

		}

	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Gui frame = new Gui();
	}

}
