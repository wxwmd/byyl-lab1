package lexical;

import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Lexical {
	private String text;  // 读入的测试样例文本
	private JTable jtable1;  // 实为Object[][]数组，存储识别信息，即“行数-Token-种别码-单词类别”
	private JTable jtable2;  // 实为Object[][]数组，存储错误报告，即“行数-错误内容-错误信息”
	private JTable jtable3;  // Object[][]数组，存储的是标识符表，即“标识符-标识符位置”
	private JTable jtable4;  // Object[][]数组，存储的是常量表，即“常量-常量位置”
	
	/**
	 * 构造函数
	 * @param text String型，读入的测试样例文本
	 * @param jtable1 实为Object[][]数组，存储识别信息，即“行数-Token-单词类别-种别码”
	 * @param jtable2 实为Object[][]数组，存储错误报告，即“行数-错误内容-错误信息”
	 * @param jtable3 实为Object[][]数组，存储的是标识符表，即“标识符-标识符位置”
	 * @param jtable4 实为Object[][]数组，存储的是常量表，即“常量-常量位置”
	 */
	public Lexical(String text, JTable jtable1, JTable jtable2, JTable jtable3, JTable jtable4) {
		this.text = text;
		this.jtable1 = jtable1;
		this.jtable2 = jtable2;
		this.jtable3 = jtable3;
		this.jtable4 = jtable4;
	}


	public static Map<String, Integer> symbol = new HashMap<String, Integer>();  // 符号表HashMap
	

	public static Map<String, Integer> constant = new HashMap<String, Integer>();  // 常量表HashMap
	
	/**
	 * 核心函数
	 * 根据已经构成的DFA状态转换表
	 * 按行分析数据，识别相应信息
	 */
	public void lex() {
		String[] texts = text.split("\n");
		symbol.clear();
		constant.clear();
		for(int m = 0; m < texts.length; m++) {
			String str = texts[m];
			if (str.equals(""))
				continue;
			else {
				char[] strline = str.toCharArray();
				for(int i = 0; i < strline.length; i++) {
					char ch = strline[i];
					if (ch == ' ')
						continue;	
					
					String token = "";
                    // 识别关键字和标识符
					if (util.isAlpha(ch)) {
                        do {
                            token += ch;  
                            i++;  
                            if(i >= strline.length) 
                            	break;  
                            ch = strline[i];  
                        } while (ch != '\0' && (util.isAlpha(ch) || util.isDigit(ch)));  
                        i--;
                        // 识别关键字
                        if (util.isKeyword(token)) {
                            DefaultTableModel tableModel = (DefaultTableModel)jtable1.getModel();
                            tableModel.addRow(new Object[] {m+1, token, "关键字", util.keywords_code.get(token)});
                            jtable1.invalidate();
                        }
                        // 识别标识符
                        else {
                        	if (!symbol.containsKey(token)) {
                                symbol.put(token, m+1);
                                DefaultTableModel tableModel3 = (DefaultTableModel) jtable3.getModel();
                                tableModel3.addRow(new Object[] {token, m+1});
                                jtable3.invalidate();
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "标识符", 1});
                            jtable1.invalidate();
                        }
                    }

                    // 识别无符号数
					else if(util.isDigit(ch)) {
						int state = 1;
						int k;
                        Boolean isfloat = false;  
                        Boolean isSci_not = false;  
                        while ( (ch != '\0') && (util.isDigit(ch) || ch == '.' || ch == 'e' 
                        		|| ch == '-' || ch == 'E' || ch == '+'))
                        {
                        	if (ch == '.') 
                        		isfloat = true;
                        	if (ch == 'e' || ch == 'E')  
                        	{
                        		isfloat = false;
                        		isSci_not = true;
                        	}
                        	
                            for (k = 0; k <= 6; k++) 
                            {                             	
                                char tmpstr[] = util.digitDFA[state].toCharArray();  
                                if (ch != '#' && util.is_digit_state(ch, tmpstr[k]) == 1) 
                                {  
                                    token += ch;  
                                    state = k;  
                                    break;  
                                }  
                            }
                            if (k > 6) 
                            	break;
                            i++;
                            if (i >= strline.length) 
                            	break;  
                            ch = strline[i];
                        }
                        
                        Boolean haveMistake = false;  
                        
                        if (state == 2 || state == 4 || state == 5)  // 非终态
                        {  
                            haveMistake = true;  
                        }                     
                        else  // 无符号数后面紧跟的符号错误
                        {  
                            if ((ch == '.') || (!util.isOperator(String.valueOf(ch)) 
                            		&& !util.isDigit(ch) && !util.isDelimiter(String.valueOf(ch))
                            		&& ch != ' ')) 
                                haveMistake = true;  
                        }  
                                            
                        if (haveMistake)   // 错误处理 
                        {  
                        	while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ')
                            {  
                                token += ch;  
                                i++;
                                if (i >= strline.length) 
                                	break;  
                                ch = strline[i];  
                            }  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "无符号数不合规范"});
                            jtable2.invalidate();
                        }
                        else 
                        {  
                        	if (constant.isEmpty() || (!constant.isEmpty() && !constant.containsKey(token))) 
                        	{  
                        		constant.put(token, m + 1);
                                DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                                tableModel4.addRow(new Object[] {token, m + 1});
                                jtable4.invalidate();
                            }
                        	if (isSci_not)
                        	{  
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "科学计数法", 4});
                                jtable1.invalidate();    
                            } 
                        	else if (isfloat) 
                            {  
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "浮点型常量", 3});
                                jtable1.invalidate();    
                            } 
                            else
                            {   
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "整型常量", 2});
                                jtable1.invalidate();   
                            }  
                        }
                        i--;
                        token = "";
                    }

                    // 识别字符常量
					else if(ch == '\'') {
						int state = 1;
                        token += ch;                    
                        i++;
                        if (i < strline.length){
                            char ch1 = strline[i];
                            if (ch1 != '\\'){
                                token += ch1;
                                state = 2;
                                i++;
                                if (i < strline.length){
                                    char ch2 = strline[i];
                                    if (ch2 == '\''){
                                        token += ch2;
                                        state = 3;
                                    }
                                }
                            } else { //检测\n,\',\t等字符
                                token += ch1;
                                state = 4;
                                i++;
                                if (i < strline.length){
                                    char ch2 = strline[i];
                                    if (ch2 == '\'' || ch2 == 'n' || ch2 == 't' || ch2 == '\\'){
                                        token += ch2;
                                        state = 5;
                                        i++;
                                        if (i < strline.length){
                                            char ch3 = strline[i];
                                            if (ch3 == '\''){
                                                token += ch3;
                                                state = 3;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (state != 3) {
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "字符常量出错"});
                            jtable2.invalidate();
                            i--;
                        }
                        else {
                        	if (!constant.containsKey(token)) {
                        		constant.put(token, m + 1);
                                DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                                tableModel4.addRow(new Object[] {token, m + 1});
                                jtable4.invalidate();
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "字符常量", 5});
                            jtable1.invalidate(); 
                        }
                        token = "";
					}

                    // 识别字符串常量
					else if (ch == '"') {
						Boolean haveMistake = false;
						token += ch;

                        int state = 0;
                        while (state != 2){
                            i++;
                            if (i >= strline.length){
                                haveMistake = true;
                                break;
                            }
                            char ch1 = strline[i];
                            token += ch1;
                            if (ch1 == '"'){
                                state = 2;
                                break;
                            } else {
                                state = 1;
                            }
                        }

                        if (haveMistake) {
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "字符串常量出错"});
                            jtable2.invalidate();  
                            i--;  
                        } 
                        else {
                        	if (!constant.containsKey(token)) {
                        		constant.put(token, m + 1);
                                DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                                tableModel4.addRow(new Object[] {token, m + 1});
                                jtable4.invalidate();
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "字符串常量", 6});
                            jtable1.invalidate();  
                        }
                    }

                    // 识别/**/型注释
					else if (ch == '/') {
					    int state = 2;
						token += ch;  
                        i++;
                        if (i>=strline.length) 
                        	break;  
                        ch = strline[i];
                        boolean haveMistake = false;
                        if (ch == '*') {
                            token += ch;
                            state = 3;

                            //获取下一个字符
                            while (state != 5) {
                                if (i == strline.length-1) {
                                    token += "\n ";
                                    m++;
                                    if (m >= texts.length) {
                                        haveMistake = true;
                                        break;
                                    }
                                    str = texts[m];
                                    if (str.equals("")){
                                        continue;
                                    }
                                    else {
                                        strline = str.toCharArray();
                                        i=0;
                                        ch = strline[i];
                                    }
                                }
                                else {
                                    i++;
                                    ch = strline[i];
                                }

                                token += ch;

                                if (ch == '*'){
                                    state = 4;
                                } else if(state == 4 && ch == '/'){
                                    state = 5;
                                } else if (state == 4 && ch != '/'){
                                    state = 3;
                                } else {
                                    state = 3;
                                }
                            }
                        }
                        if(haveMistake) {
                            DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "注释未封闭"});
                            jtable2.invalidate();
                            --i;
                        }
                        else {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "注释", 7});
                            jtable1.invalidate();
                        }
                    }


                    // 运算符
					else if (util.isOperator(String.valueOf(ch))) {
						token += ch;
                        // 后面可以用一个"="
                        if (util.isPlusEqu(ch)) {
                            i++;
                            if (i>=strline.length) 
                            	break;  
                            ch = strline[i];  
                            if (ch == '=') {
                                token += ch;
                            } else {
                                i--;
                            }
                        } else if (util.isPlusSame(ch)) {  // 后面可以用一个和自己一样的
                            i++;
                            if (i < strline.length){
                                char ch1 = strline[i];
                                if (ch1 == ch){
                                    token += ch1;
                                } else{
                                    i--;
                                }
                            }
                        }

                        DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                        tableModel1.addRow(new Object[] {m+1, token, "运算符", util.operator_code.get(token)});
                        jtable1.invalidate();
                    }

                    //界符
                    else if (util.isDelimiter(String.valueOf(ch))){
					    token += ch;
					    DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
					    tableModel1.addRow(new Object[] {m+1, token, "界符", util.delimiter_code.get(token)});
					    jtable1.invalidate();
                    }

                    //不合法字符
					else {
                        if(ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "存在不合法字符"});
                            jtable2.invalidate();
                            System.out.println(ch);
                        }  
                    }				
				}
			} 
		}
    }
}
