package FileManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import tw.cheyingwu.ckip.CKIP;
import tw.cheyingwu.ckip.Term;
import tw.cheyingwu.ckip.WordSegmentationService;
import weka.core.parser.java_cup.internal_error;

public class CutSentence {

	static String ip = "140.109.19.104";
	static String id = "60347030S";
	static String pw = "60347030S";
	static int port = 1501;
	
	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		List<String> filelist = readDirs(".\\Data\\20160928(Long)\\160928Typhoon(Long)");
		WordSegmentationService c; // 宣告一個class變數c
		
		HashMap<Integer, ArrayList<String>> postsens = new HashMap<>();
		//----中研院斷詞----//
		c = new CKIP(ip, port, id, pw); // 輸入申請的IP、port、帳號、密碼
		int posid = 0;
		for(String file : filelist){ //數PO文
	           String content = readFile(file);
	           
	           if(content!=null){
	        	   String[] sep = content.split(";");
	        	if(sep.length>1){
	        		String[] sens = sep[1].split(":");	
			           
			           ArrayList<String> TermSen = new ArrayList<String>(); // 存放斷詞結果
			           for(int i=0; i<sens.length;i++){ // 數句子
			        	   
			        	   FileWriter fr1 = new FileWriter("p"+(posid+1)+"sen"+(i+1)+".txt"); 
			        	   BufferedWriter bw = new BufferedWriter(fr1);
			        	   
			        	   String newcontent = removeHtmlTag(sens[i]);
				           String sanitizedString = newcontent.replaceAll("[+\\-!(){}-◢▆▅▄▃▃▄▅▆◣=◆※@%#\\[\\]^~:\\\\]|/\\*|\\*/|&&|\\|\\|", "");
			        	   
			        	   c.setRawText(sanitizedString);
			        	   c.send(); // 傳送至中研院斷詞系統服務使用	        	   
			        	   String terms = ""; // 存放斷詞結果
			        	   
			        	   for (Term t : c.getTerm()) 
							{
								terms = terms +t.getTerm()+ " ";
								
							}
			        	   
			        	   
			        	   
			        	   TermSen.add(terms+":"+sens[i]);
			        	   bw.write(terms+";"+sens[i]+";"+sep[0]);
			        	   
			        	   bw.flush();
			        	   bw.close();
			        	   
			           }
		           }
		           
		           
		    //postsens.put(posid, TermSen);    
			
			posid++;
	        	}
		           
	    
		}
		
		//System.out.println(postsens);
		
	}
		
	public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException
    {
        try
        {
            File file = new File(filepath);
            if(!file.isDirectory())
            {
                System.out.println("輸入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            }
            else
            {
                String[] flist = file.list();
                for(int i = 0; i < flist.length; i++)
                {
                    File newfile = new File(filepath + "\\" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
                    {
                        readDirs(filepath + "\\" + flist[i]);
                    }                    
                }
            }
        }catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        return FileList;
    }
	
	public static String readFile(String file) throws FileNotFoundException, IOException
    {
        StringBuffer strSb = new StringBuffer(); //String is constant， StringBuffer can be changed.
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file)); //byte streams to character streams
        BufferedReader br = new BufferedReader(inStrR); 
        String line = br.readLine();
        
        
        while(line != null){
        	
        	String[] linea = line.split(";");
        	if(linea.length !=0){
        		
        		if(linea.length<3){
        			System.out.println("Path:"+file);
        			System.out.println("Pass!");
        			break;
        		}
        	
        		strSb.append(linea[1]+";"+linea[2]);
	        	
        	}
        	
        	
            line = br.readLine();    
        }
        
        return strSb.toString();
    }
	
	public static String removeHtmlTag(String inputString) {
		
		if (inputString == null){		
			return null;
		}
		
		String htmlStr = inputString; // 含html标签的字符串		
		String textStr = "";		
		java.util.regex.Pattern p_script;		
		java.util.regex.Matcher m_script;		
		java.util.regex.Pattern p_style;		
		java.util.regex.Matcher m_style;	
		java.util.regex.Pattern p_html;		
		java.util.regex.Matcher m_html;
		java.util.regex.Pattern p_ptt;		
		java.util.regex.Matcher m_ptt;
		java.util.regex.Pattern p_comm;		
		java.util.regex.Matcher m_comm;
		java.util.regex.Pattern p_special;		
		java.util.regex.Matcher m_special;
		
		try {
		
			//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>		
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";		
			//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>	
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";		
			// 定义HTML标签的正则表达式		
			String regEx_html = "<[^>]+>";
			// 定义雙刮弧标签的正则表达式		
			String regEx_ptt = "《[^>]+》";
			// 定义刮弧标签的正则表达式		
			String regEx_comm = "/[^>]+/";
			// 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		
			String regEx_special = "\\&[a-zA-Z]{1,10};";
					
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);		
			m_script = p_script.matcher(htmlStr);		
			htmlStr = m_script.replaceAll(""); // 过滤script标签	
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);		
			m_style = p_style.matcher(htmlStr);		
			htmlStr = m_style.replaceAll(""); // 过滤style标签		
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);		
			m_html = p_html.matcher(htmlStr);		
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			p_ptt = Pattern.compile(regEx_ptt, Pattern.CASE_INSENSITIVE);		
			m_ptt = p_ptt.matcher(htmlStr);		
			htmlStr = m_ptt.replaceAll(""); // 过滤雙刮弧标签		
			p_comm = Pattern.compile(regEx_comm, Pattern.CASE_INSENSITIVE);		
			m_comm = p_comm.matcher(htmlStr);		
			htmlStr = m_comm.replaceAll(""); // 过滤刮弧标签		
			p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);		
			m_special = p_special.matcher(htmlStr);		
			htmlStr = m_special.replaceAll(""); // 过滤特殊标签		
			textStr = htmlStr;		
		
		} catch (Exception e) {
		
		e.printStackTrace();
		
		}
		
		return textStr;// 返回文本字符串
		
		}

}
