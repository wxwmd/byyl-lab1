package lexical;

import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Lexer {
    // 读入的测试样例文本
	private String text;
    // 实为Object[][]数组，存储识别信息，即“行数-Token-种别码-单词类别”
	private JTable jtable1;
    // 实为Object[][]数组，存储错误报告，即“行数-错误内容-错误信息”
	private JTable jtable2;
    // Object[][]数组，存储的是标识符表，即“标识符-标识符位置”
	private JTable jtable3;
    // Object[][]数组，存储的是常量表，即“常量-常量位置”
	private JTable jtable4;
	

	Lexer(String text, JTable jtable1, JTable jtable2, JTable jtable3, JTable jtable4) {
		this.text = text;
		this.jtable1 = jtable1;
		this.jtable2 = jtable2;
		this.jtable3 = jtable3;
		this.jtable4 = jtable4;
	}

    // 标识符HashMap
    private Map<String, Integer> symbol = new HashMap<String, Integer>();

    // 常量表HashMap
    private Map<String, Integer> constant = new HashMap<String, Integer>();

    void lex() {
		String[] texts = text.split("\n");
		symbol.clear();
		constant.clear();
		for(int m = 0; m < texts.length; m++) {
			String str = texts[m];
			if (str.equals("")) {
            }
			else {
				char[] strline = str.toCharArray();
				for(int i = 0; i < strline.length; i++) {
					char ch = strline[i];
					if (ch == ' ')
						continue;	
					
					String token = "";
                    // 识别关键字和标识符
					if (Util.isAlpha(ch)) {
                        do {
                            token += ch;  
                            i++;  
                            if(i >= strline.length){
                                break;
                            }
                            ch = strline[i];  
                        } while (Util.isAlpha(ch) || Util.isDigit(ch));
                        i--;
                        // 识别关键字
                        if (Util.isKeyword(token)) {
                            DefaultTableModel tableModel = (DefaultTableModel)jtable1.getModel();
                            tableModel.addRow(new Object[] {m+1, token, "关键字", Util.keywords_code.get(token)});
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
					else if(Util.isDigit(ch)) {
						int state = 2;
						token += ch;
						boolean flag = true;

						//dfa状态转化
                        while (i < strline.length && flag){
                            i++;
                            char ch1 = strline[i];
                            boolean isDigit = Util.isDigit(ch1);
                            switch (state){
                                case 2:{
                                    if (isDigit){
                                        state = 2;
                                        token += ch1;
                                    } else if (ch1 == 'e'){
                                        state = 5;
                                        token += ch1;
                                    } else if (ch1 == '.'){
                                        state = 3;
                                        token += ch1;
                                    } else {
                                        flag = false;
                                    }
                                    break;
                                }
                                case 3:{
                                    if (isDigit){
                                        state = 4;
                                        token += ch1;
                                    } else {
                                        flag = false;
                                    }
                                    break;
                                }
                                case 4:{
                                    if (isDigit){
                                        state = 4;
                                        token += ch1;
                                    } else if (ch1 == 'e'){
                                        state = 5;
                                        token += ch1;
                                    } else {
                                        flag = false;
                                    }
                                    break;
                                }
                                case 5:{
                                    if(isDigit){
                                        state = 7;
                                        token += ch1;
                                    } else if (ch1 == '+' || ch1 == '-'){
                                        state = 6;
                                        token += ch1;
                                    } else {
                                        flag = false;
                                    }
                                    break;
                                }
                                case 6:{
                                    if(isDigit){
                                        state = 7;
                                        token += ch1;
                                    } else {
                                        flag = false;
                                    }
                                    break;
                                }
                                case 7:{
                                    if (isDigit){
                                        state = 7;
                                        token += ch1;
                                    } else {
                                        flag = false;
                                    }
                                    break;
                                }
                                default:{
                                    break;
                                }
                            }
                        }

                        if (i < strline.length){
                            i--;
                        }

                        //最终识别
                        if (state == 7) {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "科学计数法", 4});
                            jtable1.invalidate();
                        }
                        else if (state == 4) {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "浮点型常量", 3});
                            jtable1.invalidate();
                        }
                        else if (state == 2){
                            if (i >= strline.length || Util.isDelimiter(""+strline[i+1]) || Util.isOperator(""+strline[i+1])){
                                DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "整型常量", 2});
                                jtable1.invalidate();
                            } else {
                                DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                                tableModel2.addRow(new Object[] {m+1, token, "语法错误"});
                                jtable2.invalidate();
                            }

                        } else {
                            while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ') {
                                token += ch;
                                i++;
                                if (i >= strline.length)
                                    break;
                                ch = strline[i];
                            }
                            DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "语法错误"});
                            jtable2.invalidate();
                        }

                        if ((state == 2 || state == 4 || state == 7)&&(!constant.containsKey(token))){
                            constant.put(token, m + 1);
                            DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                            tableModel4.addRow(new Object[] {token, m + 1});
                            jtable4.invalidate();
                        }
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
					else if (Util.isOperator(String.valueOf(ch))) {
						token += ch;
                        // 后面可以用一个"="
                        if (Util.isPlusEqu(ch)) {
                            i++;
                            if (i>=strline.length) 
                            	break;  
                            ch = strline[i];  
                            if (ch == '=') {
                                token += ch;
                            } else {
                                i--;
                            }
                        } else if (Util.isPlusSame(ch)) {  // 后面可以用一个和自己一样的
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
                        tableModel1.addRow(new Object[] {m+1, token, "运算符", Util.operator_code.get(token)});
                        jtable1.invalidate();
                    }

                    //界符
                    else if (Util.isDelimiter(String.valueOf(ch))){
					    token += ch;
					    DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
					    tableModel1.addRow(new Object[] {m+1, token, "界符", Util.delimiter_code.get(token)});
					    jtable1.invalidate();
                    }

                    //不合法字符
					else {
                        if(ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "字符不合法"});
                            jtable2.invalidate();
                            System.out.println(ch);
                        }  
                    }				
				}
			} 
		}
    }
}
