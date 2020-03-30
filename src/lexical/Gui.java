package lexical;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Gui extends JFrame {
	private static final long serialVersionUID=1L;
	
	public Gui() {
		getContentPane().setForeground(Color.WHITE);
		getContentPane().setFont(new Font("意大利斜体", Font.ITALIC, 25));
		
		setTitle("词法分析GUI");    //设置显示窗口标题

		setSize(1200,900);    //设置窗口显示尺寸
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
		getContentPane().setLayout(null);

		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setBounds(25, 25, 430, 405);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane1);
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Times New Roman", Font.ITALIC, 17));
		scrollPane1.setViewportView(textArea);
		scrollPane1.setRowHeaderView(new ShowStyle());
			
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setToolTipText("");
		scrollPane2.setBackground(SystemColor.menu);
		scrollPane2.setBounds(500, 25, 628, 466);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane2);
		
		String[] name1 = new String[] {"行号","Token", "类别", "种别码"};
        JTable table1 = new JTable(new DefaultTableModel(new Object[][] {}, name1));
        table1.setForeground(Color.BLACK);
        table1.setFillsViewportHeight(true);
        table1.setFont(new Font("意大利斜体", Font.ITALIC, 18));
		table1.setBackground(new Color(255, 255, 255));
		scrollPane2.setViewportView(table1);
		
		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane3.setBackground(SystemColor.menu);
		scrollPane3.setBounds(500, 513, 628, 288);
		getContentPane().add(scrollPane3);
		
		String[] name2 = new String[] {"错误行号", "Token", "详细说明"};
		JTable table2 = new JTable(new DefaultTableModel(new Object[][] {}, name2));
		table2.setFont(new Font("意大利斜体", Font.ITALIC, 18));
		table2.setFillsViewportHeight(true);
		table2.setBackground(Color.WHITE);
		//table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane3.setViewportView(table2);
	
		
		JScrollPane scrollPane4 = new JScrollPane();
		scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane4.setBackground(SystemColor.menu);
		scrollPane4.setBounds(25, 512, 209, 289);
		getContentPane().add(scrollPane4);
		
		String[] name3 = new String[] {"标识符", "行号"};
		JTable table3 = new JTable(new DefaultTableModel(new Object[][] {}, name3));
		table3.setForeground(Color.BLACK);
		table3.setFont(new Font("意大利斜体", Font.ITALIC, 18));
		table3.setFillsViewportHeight(true);
		table3.setBackground(Color.WHITE);
		scrollPane4.setViewportView(table3);
		
		JScrollPane scrollPane5 = new JScrollPane();
		scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane5.setBackground(SystemColor.menu);
		scrollPane5.setBounds(239, 512, 209, 289);
		getContentPane().add(scrollPane5);
		
		String[] name4 = new String[] {"常量", "行号"};
		JTable table4 = new JTable(new DefaultTableModel(new Object[][] {}, name4));
		table4.setForeground(Color.BLACK);
		table4.setFont(new Font("意大利斜体", Font.ITALIC, 18));
		table4.setFillsViewportHeight(true);
		table4.setBackground(Color.WHITE);
		scrollPane5.setViewportView(table4);

		
		JButton button3 = new JButton("词法分析");
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model1 = new DefaultTableModel(new Object[][]{},name1);
				table1.setModel(model1);
				
				DefaultTableModel model2 = new DefaultTableModel(new Object[][]{},name2);
				table2.setModel(model2);
			
				DefaultTableModel model3 = new DefaultTableModel(new Object[][]{},name3);
				table3.setModel(model3);

				DefaultTableModel model4 = new DefaultTableModel(new Object[][]{},name4);
				table4.setModel(model4);

				Lexer text_lex = new Lexer(textArea.getText(), table1, table2, table3, table4);
				text_lex.lex();
				
				if (table1.getRowCount() == 0 && table2.getRowCount() == 0 
						&& table3.getRowCount() == 0 && table4.getRowCount() == 0) {
					JOptionPane.showMessageDialog(null, "没有可分析的程序", "Warning", JOptionPane.DEFAULT_OPTION);
				}
			
			}
		});
		button3.setFont(new Font("意大利斜体", Font.ITALIC, 23));
		button3.setBounds(25, 445, 200, 46);
		getContentPane().add(button3);
		setVisible(true);    //设置窗口是否可见


        JButton button1 = new JButton("导入文件");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String file_name;
                JFileChooser file_open_filechooser = new JFileChooser();
                file_open_filechooser.setCurrentDirectory(new File("."));
                file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = file_open_filechooser.showOpenDialog(scrollPane1);

                if (result == JFileChooser.APPROVE_OPTION) {
                    file_name = file_open_filechooser.getSelectedFile().getPath();
                    // 读取文件，写到JTextArea里面
                    File file = new File(file_name);
                    try {
                        textArea.setText("");
                        InputStream in = new FileInputStream(file);
                        int tempbyte;
                        while ((tempbyte=in.read()) != -1)
                        {
                            textArea.append(""+(char)tempbyte);
                        }
                        in.close();
                    }
                    catch(Exception event) {
                        event.printStackTrace();
                    }
                }
            }
        });
        button1.setFont(new Font("意大利斜体", Font.ITALIC, 23));
        button1.setBounds(240, 445, 200, 46);
        getContentPane().add(button1);
	}
	
	
    public static void main(String[] agrs) {
        new Gui();
    }
}
