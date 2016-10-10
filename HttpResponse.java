import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JTextField;


public class HttpResponse extends Thread {
	Socket socket;
	JTextArea ta;
	PrintStream pout;
	boolean isHttp1 = false;
	String path = null;
	HttpResponse(Socket socket, JTextArea ta,JTextField path)
	{
		this.socket = socket;
		this.ta = ta;
		this.path = path.getText();
	}
	
	public void run()
	{
		try {
		
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pout = new PrintStream(socket.getOutputStream());
			String requestLine = br.readLine();
			//String requestCmds = ""+requestLine;
			if(requestLine==null){
				error(400,"Empty Request");
			}
			else{
				ta.append("Http���� ����["+socket.getInetAddress()+":"+socket.getPort()+"]   "+requestLine+"\n");
			}
			if(requestLine.toLowerCase().indexOf("http/1.")!=-1){
				isHttp1 = true;
			}
			String[] request = requestLine.split(" ");
			if(request.length<2)
			{
				error(400,"Bad Request");
			}
			String str1 = request[0];
			if(str1.equals("GET")){
				serveFile(request[1]);
			}
			else{
				error(400, "Bad Request");
				ta.append("Bad Request");
			}
			
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return;
		}
		
	}
	
	
	private void error(int erorcd, String erortx) {
		erortx = "<html><h1>" + erortx + "</h1></html>";
		if (isHttp1) {
			pout.println("HTTP/1.0 " + erorcd + " " + erortx);
			pout.println("Content-type: text/html");
			pout.println("Content-length: " + erortx.length() + "\n");
		}
		pout.println(erortx);
	}
	
	private void serveFile(String requestPath)  {
		if (requestPath.equals("/")){
			/**
			 * ȡ��ҳ�ļ�����ҳ�ļ�����Ϊindex.html��index.htm
			 */
			requestPath = "/index.html";
			if(path==null){
				path=new File("").getAbsolutePath();
			}
			if(!new File(path+requestPath).exists()){
				requestPath="/index.htm";
			}			
		}				
		try {
			sendFileData(requestPath);
			ta.append("�ļ�����ɹ� ��       "+requestPath+"\n");
		} catch (Exception e) {
			error(404, "");
			ta.append("�����ļ�������\n");
		}		
	}
	
	private void sendFileData(String requestPath) throws IOException,FileNotFoundException {
		InputStream inputstream = new FileInputStream(path+requestPath);
		if (inputstream == null)
			throw new FileNotFoundException(requestPath);		
		if (isHttp1) {
			pout.println("HTTP/1.0 200 Document follows");
			pout.println("Content-length: " + inputstream.available());
			if (requestPath.endsWith(".gif"))
				pout.println("Content-type: image/gif");
			else if (requestPath.endsWith(".jpg"))
				pout.println("Content-type: image/jpeg");
			else if (requestPath.endsWith(".html") || requestPath.endsWith(".htm"))
				pout.println("Content-Type: text/html");
			else
				pout.println("Content-Type: application/octet-stream");
			pout.println();
		}
		/*��������Ϊ8K*/
		byte[] is = new byte[8*1024];
		int length=0;
		while((length=inputstream.read(is))!=-1){
			pout.write(is, 0, length);
		}
		inputstream.close();
		pout.flush();
	}

}
