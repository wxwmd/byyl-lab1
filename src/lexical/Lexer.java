package lexical;

import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Lexer {
    // ����Ĳ��������ı�
	private String text;
    // ʵΪObject[][]���飬�洢ʶ����Ϣ����������-Token-�ֱ���-�������
	private JTable jtable1;
    // ʵΪObject[][]���飬�洢���󱨸棬��������-��������-������Ϣ��
	private JTable jtable2;
    // Object[][]���飬�洢���Ǳ�ʶ������������ʶ��-��ʶ��λ�á�
	private JTable jtable3;
    // Object[][]���飬�洢���ǳ���������������-����λ�á�
	private JTable jtable4;
	

	Lexer(String text, JTable jtable1, JTable jtable2, JTable jtable3, JTable jtable4) {
		this.text = text;
		this.jtable1 = jtable1;
		this.jtable2 = jtable2;
		this.jtable3 = jtable3;
		this.jtable4 = jtable4;
	}

    // ��ʶ��HashMap
    private Map<String, Integer> symbol = new HashMap<String, Integer>();

    // ������HashMap
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
                    // ʶ��ؼ��ֺͱ�ʶ��
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
                        // ʶ��ؼ���
                        if (Util.isKeyword(token)) {
                            DefaultTableModel tableModel = (DefaultTableModel)jtable1.getModel();
                            tableModel.addRow(new Object[] {m+1, token, "�ؼ���", Util.keywords_code.get(token)});
                            jtable1.invalidate();
                        }
                        // ʶ���ʶ��
                        else {
                        	if (!symbol.containsKey(token)) {
                                symbol.put(token, m+1);
                                DefaultTableModel tableModel3 = (DefaultTableModel) jtable3.getModel();
                                tableModel3.addRow(new Object[] {token, m+1});
                                jtable3.invalidate();
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "��ʶ��", 1});
                            jtable1.invalidate();
                        }
                    }


                    // ʶ���޷�����
					else if(Util.isDigit(ch)) {
						int state = 2;
						token += ch;
						boolean flag = true;

						//dfa״̬ת��
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

                        //����ʶ��
                        if (state == 7) {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "��ѧ������", 4});
                            jtable1.invalidate();
                        }
                        else if (state == 4) {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "�����ͳ���", 3});
                            jtable1.invalidate();
                        }
                        else if (state == 2){
                            if (i >= strline.length || Util.isDelimiter(""+strline[i+1]) || Util.isOperator(""+strline[i+1])){
                                DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "���ͳ���", 2});
                                jtable1.invalidate();
                            } else {
                                DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                                tableModel2.addRow(new Object[] {m+1, token, "�﷨����"});
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
                            tableModel2.addRow(new Object[] {m+1, token, "�﷨����"});
                            jtable2.invalidate();
                        }

                        if ((state == 2 || state == 4 || state == 7)&&(!constant.containsKey(token))){
                            constant.put(token, m + 1);
                            DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                            tableModel4.addRow(new Object[] {token, m + 1});
                            jtable4.invalidate();
                        }
                    }

                    // ʶ���ַ�����
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
                            } else { //���\n,\',\t���ַ�
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
                            tableModel2.addRow(new Object[] {m+1, token, "�ַ���������"});
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
                            tableModel1.addRow(new Object[] {m+1, token, "�ַ�����", 5});
                            jtable1.invalidate(); 
                        }
                        token = "";
					}

                    // ʶ���ַ�������
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
                            tableModel2.addRow(new Object[] {m+1, token, "�ַ�����������"});
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
                            tableModel1.addRow(new Object[] {m+1, token, "�ַ�������", 6});
                            jtable1.invalidate();  
                        }
                    }

                    // ʶ��/**/��ע��
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

                            //��ȡ��һ���ַ�
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
                            tableModel2.addRow(new Object[] {m+1, token, "ע��δ���"});
                            jtable2.invalidate();
                            --i;
                        }
                        else {
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "ע��", 7});
                            jtable1.invalidate();
                        }
                    }


                    // �����
					else if (Util.isOperator(String.valueOf(ch))) {
						token += ch;
                        // ���������һ��"="
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
                        } else if (Util.isPlusSame(ch)) {  // ���������һ�����Լ�һ����
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
                        tableModel1.addRow(new Object[] {m+1, token, "�����", Util.operator_code.get(token)});
                        jtable1.invalidate();
                    }

                    //���
                    else if (Util.isDelimiter(String.valueOf(ch))){
					    token += ch;
					    DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
					    tableModel1.addRow(new Object[] {m+1, token, "���", Util.delimiter_code.get(token)});
					    jtable1.invalidate();
                    }

                    //���Ϸ��ַ�
					else {
                        if(ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "�ַ����Ϸ�"});
                            jtable2.invalidate();
                            System.out.println(ch);
                        }  
                    }				
				}
			} 
		}
    }
}