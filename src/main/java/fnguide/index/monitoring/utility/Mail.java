package fnguide.index.monitoring.utility;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
	public static String InputStream2String(InputStream is, String encoding){
		
		String result = null;
		
		if(encoding.compareTo("UTF-8") == 0){
			InputStreamReader isr;
			try {
				isr = new InputStreamReader(is, "UTF-8");
				StringBuffer sb = new StringBuffer();
			     char[] b = new char[4096];
			     for (int n; (n = isr.read(b)) != -1;) {
			         sb.append(new String(b, 0, n));
			     }
			     result = sb.toString();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			InputStreamReader isr;
			try {
				isr = new InputStreamReader(is);
				StringBuffer sb = new StringBuffer();
			     char[] b = new char[4096];
			     for (int n; (n = isr.read(b)) != -1;) {
			         sb.append(new String(b, 0, n));
			     }
			     result = sb.toString();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
		public static class ReportManager{
				
				BufferedWriter out = null;
				
				public void ReportManger(){
					
				}
				
				public void Open(String path){
					try {
						
						out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));				
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				public void Close(){
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				public void Write(String msg){
					try {
						out.write(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				public void Tab(){
					try {
						out.write("\t");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};

		public static void SendReportMail(String subject, String cotents, String from, String to)
		{	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
	    	String host = "mw-002.cafe24.com";              //smtp mail server      
	    	//String from = "Finance News Scrap<renacer@renacer.cafe24.com>";     //sender email address
	    	//String to = "reinitiate@gmail.com";
	    	Properties props = new Properties();
	    	props.put("mail.smtp.host", host);
	    	props.put("mail.smtp.auth","true");
	    	Authenticator auth = new MyAuthentication();
	    	Session sess = Session.getInstance(props, auth);
	    		    	
			try {			
		        MimeMessage msg = new MimeMessage(sess);
		        msg.setFrom(new InternetAddress(from));		        
		        InternetAddress[] address = {new InternetAddress(to)};
		        msg.setRecipients(Message.RecipientType.TO, address);		        
		        msg.setSubject(sdf.format((new Date())) + " " + subject);
		        msg.setSentDate(new Date());
		        msg.setText(cotents);
		        Transport.send(msg);
		        
			} catch (MessagingException mex) {
				
			}
		}
		
		public static void SendReportMail(String subject, String cotents, String from, String[] to)
		{	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
	    	String host = "mw-002.cafe24.com";              //smtp mail server      
	    	//String from = "Finance News Scrap<renacer@renacer.cafe24.com>";     //sender email address
	    	//String to = "reinitiate@gmail.com";
	    	Properties props = new Properties();
	    	props.put("mail.smtp.host", host);
	    	props.put("mail.smtp.auth","true");
	    	Authenticator auth = new MyAuthentication();
	    	Session sess = Session.getInstance(props, auth);
	    	
			try {			
		        MimeMessage msg = new MimeMessage(sess);
		        msg.setFrom(new InternetAddress(from));
		        
		        InternetAddress[] address = new InternetAddress[to.length]; 
		        
		        for(int i=0; i<to.length; i++)
		        {
		        	address[i] = new InternetAddress(to[i]);
		        }
		        	        
		        msg.setRecipients(Message.RecipientType.TO, address);
		        msg.setSubject(sdf.format((new Date())) + " " + subject);
		        msg.setSentDate(new Date());
		        msg.setText(cotents);
		        Transport.send(msg);
		        
			} catch (MessagingException mex) {
				
			}
		}
		
		public static void SendReportMail_Html(String subject, String cotents, String from, String[] to)
		{	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
	    	String host = "mw-002.cafe24.com";              //smtp mail server      
	    	//String from = "Finance News Scrap<renacer@renacer.cafe24.com>";     //sender email address
	    	//String to = "reinitiate@gmail.com";
	    	Properties props = new Properties();
	    	props.put("mail.smtp.host", host);
	    	props.put("mail.smtp.auth","true");
	    	Authenticator auth = new MyAuthentication();
	    	Session sess = Session.getInstance(props, auth);
	    	
			try {			
		        MimeMessage msg = new MimeMessage(sess);
		        msg.setFrom(new InternetAddress(from));
		        
		        InternetAddress[] address = new InternetAddress[to.length]; 
		        
		        for(int i=0; i<to.length; i++)
		        {
		        	address[i] = new InternetAddress(to[i]);
		        }
		        		        
		        msg.setRecipients(Message.RecipientType.TO, address);
		        msg.setSubject(sdf.format((new Date())) + " " + subject, "utf-8");
		        
		        msg.setSentDate(new Date());
		        //msg.setText(cotents);
		        msg.setContent(cotents, "text/html; charset=UTF-8");		        
		        Transport.send(msg);		        
		        
			} catch (MessagingException mex) {
				
			}
		}
		
		public static class MyAuthentication extends Authenticator {
	        PasswordAuthentication pa;
	        public MyAuthentication(){
	            pa = new PasswordAuthentication("renacer1@renacer.cafe24.com", "shdutk1008");  //ex) ID:cafe24@cafe24.com PASSWD:1234
	        }
	        public PasswordAuthentication getPasswordAuthentication() {
	            return pa;
	        }
	    };	

}