import java.io.*;
import java.net.*;

import javax.swing.*;

public final class ServerThread extends Thread {
	int port;
	JTextArea display;
	JTextField status;
	JTextField direc;
	//JTextField ipAdd;
	ServerSocket listener = null;
	ServerThread (int port, JTextArea display, JTextField status,JTextField direc,JTextField ipAdd) throws Exception
	{
		this.port = port;
		this.display = display;
		this.status = status;
		this.direc = direc;
		byte[] ip = new byte[4];
		String ipp = ipAdd.getText();
		//System.out.println(ipp);
		String[] ip1 = ipp.split("\\.");
		for(int i = 0;i<ip1.length;i++){
			try{
				ip[i] = (byte) Integer.parseInt(ip1[i]);
			}
			catch(Exception e){
				ip[i] = -1;
			}
			//System.out.println(ip1[i]);
		}
		//System.out.println(ip[0]+" "+ip[1]+" "+ip[2]+" "+ip[3]);
		//this.ipAdd = ipAdd;
		try{
			listener = new ServerSocket(port,20, InetAddress.getByAddress(ip));
		}
		catch(Exception e){
			
			listener = new ServerSocket(port,20);
			display.append("��Ч��IP��ַ����ʹ��Ĭ�ϵ�ַ��"+InetAddress.getLocalHost().getHostAddress()+"\n");
			ipAdd.setText(InetAddress.getLocalHost().getHostAddress());
		}
		display.append("�������ѿ������˿ڣ�"+port+"\n");
		status.setText("�������ѿ����� "+ipAdd.getText()+":"+port+"  "+direc.getText());
	}
	
	public void run()
	{
		
		
		while(true)
		{
			try{
				Socket socket = listener.accept();
				/*for(String s = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();s!="\n";
						s = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine()){
					display.append(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
				}
				*/
				/*BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				for(String s = br.readLine();s!=null;s = br.readLine()){
					display.append(s+"\n");
				}*/
				new HttpResponse(socket, display, direc).start();
			}
			catch(Exception e){
				display.append(e+"\n");
			}
		}
	}
	
}
