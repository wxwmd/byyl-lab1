package lexical;

import java.util.HashMap;
import java.util.Map;

public class Util {

    //关键字
	public static String keywords[] = { "auto", "double", "int", "struct",  
        "break", "else", "long", "switch", "case", "enum", "register",  
        "typedef", "char", "extern", "return", "union", "const", "float",  
        "short", "unsigned", "continue", "for", "signed", "void",  
        "default", "goto", "sizeof", "volatile", "do", "if", "while",  
        "static", "String"};

	public static Map<String, Integer> keywords_code = new HashMap<String, Integer>() {
		private static final long serialVersionUID=1L;
		{
			for (int i = 0; i < keywords.length; i++)
			{
				put(keywords[i], i + 101);
			}
		}
	};

	public static boolean isKeyword(String s) {
        return keywords_code.containsKey(s);  
    }
	
	
    public static String operator[] = { "+", "-", "*", "/", "%", "++", "--",
    		"<", "<=", ">", ">=", "==", "!=","=",
    		"&&", "||", "!","~", "&", "|", "^", ">>", "<<", 
    		"+=", "-=", "*=", "/=", "%=", "&=", "^=", "|=", "<<=", ">>="};

	public static Map<String, Integer> operator_code = new HashMap<String, Integer>() {
		private static final long serialVersionUID=1L;
		{
			for (int i = 0; i < operator.length; i++)
			{
				put(operator[i], i + 201);
			}
		}	
	};

	public static boolean isOperator(String s) {
		return operator_code.containsKey(s);
    }
	 
	
	public static String delimiter[] = { ",", ";", "[", "]", "{", "}", "(", ")"};
	public static Map<String, Integer> delimiter_code = new HashMap<String, Integer>() {
		private static final long serialVersionUID=1L;
		{
			for (int i = 0; i < delimiter.length; i++)
			{
				put(delimiter[i], i + 301);
			}
		}	
	};
	public static boolean isDelimiter(String s) {
		return delimiter_code.containsKey(s);
    }
	
	
	public String toUpper(String s)
	{
		return s.toUpperCase();
	}

	// 这些符号后面可跟运算符"="
	public static boolean isPlusEqu(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>' 
        		|| ch == '<' || ch == '&' || ch == '|'  || ch == '^' || ch == '%' || ch == '!';
    }

	// 这些符号后面可再跟相同运算符
	public static boolean isPlusSame(char ch) {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '>' || ch == '<';  
    }
	
	public static boolean isAlpha(char ch) {
	    return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
	}

	public static boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');  
    }

	public static boolean isEsSt(char ch) {
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'  
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';  
    }


}
