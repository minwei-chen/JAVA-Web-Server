

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.*;

public class Surface implements ActionListener{
	
	JFrame jf = new JFrame("Web Server");
	JLabel iP = new JLabel("IP: ");
	JLabel port = new JLabel("Port: ");
	JTextField ipAdd = new JTextField(15);
	JTextField portNum = new JTextField(4);
	JButton start = new JButton("Start");
	JButton over = new JButton("Stop");
	JLabel direct = new JLabel("Main Directory: ");
	JTextField directory = new JTextField(24);
	JButton br = new JButton("...");
	//JLabel diary = new JLabel("Diary:");
	JTextArea ta = new JTextArea(10,40);
	//JTextField outDirct = new JTextField(15);
	//JTextField outIp = new JTextField(8);
	JTextField outStatus = new JTextField(40);
	//JLabel status = new JLabel("Status:");
	ServerThread listener = null;
	boolean hasStarted = false;
	String ipp;
	
	public void init()
	{
		JPanel jp1 = new JPanel();
		jp1.add(iP);
		jp1.add(ipAdd);
		jp1.add(port);
		jp1.add(portNum);
		jp1.add(start);
		jp1.add(over);
		JPanel jp2 = new JPanel();
		jp2.add(direct);
		jp2.add(directory);
		jp2.add(br);
		//JPanel jp3 = new JPanel();
		//jp3.add(diary);
		//JScrollPane jsp = new JScrollPane();
		//jsp.add(ta);
		Border bb = BorderFactory.createEtchedBorder();
		Border tb1 = BorderFactory.createTitledBorder(bb,"Console") ;
		Border tb2 = BorderFactory.createTitledBorder(bb,"Diary") ;
		JScrollPane jsp = new JScrollPane(ta);
		JPanel jp4 = new JPanel();
		jp4.add(jsp);
		jp4.setBorder(tb2);
		ta.setLineWrap(true);
		ta.setEditable(false);
		start.addActionListener(this);
		over.addActionListener(this);
		br.addActionListener(this);
		Box jb1 = Box.createVerticalBox();
		jb1.add(jp1);
		jb1.add(jp2);
		jb1.setBorder(tb1);
		directory.setText("C:/Users/Allen/Desktop/MyWeb");
		//Box jb2 = Box.createVerticalBox();
		//jb2.add(jp3);
		//jb2.add(jp4);
		Box jb = Box.createVerticalBox();
		//jb.setBorder(bb);
		outStatus.setBackground(jf.getBackground());
		outStatus.setBorder(null);
		outStatus.setEditable(false);
		JPanel jp5 = new JPanel();
		jp5.setBorder(bb);
		//jp5.add(status);
		jp5.add(outStatus);
		outStatus.setText("服务器已停止。");
		try{
			ipAdd.setText(InetAddress.getLocalHost().getHostAddress());
		}
		catch(Exception e){
			ipAdd.setText("0.0.0.0");
		}
		jb.add(jb1);
		jb.add(jp4);
		jb.add(jp5);
		//jb.add(jp3);
		//jb.add(jp4);
		jf.add(jb);
		//jf.add(jp1);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
		jf.pack();
		jf.setVisible(true);
		
	}
	
	public void startServer()
	{
		start.setEnabled(false);
		over.setEnabled(true);
		int port = 8080;
		try{
			port = Integer.parseInt(portNum.getText());
		}
		catch(Exception e)
		{
			ta.append("端口号异常，已使用8080号端口。\n");
			portNum.setText("8080");
		}
		if(hasStarted&&port==listener.port){
			listener.resume();
			ta.append("服务器已开启，端口："+listener.port+"\n");
			outStatus.setText("服务器已开启。 "+ipp+":"+port+"  "+directory.getText());
			//ipAdd.setText(InetAddress.getLocalHost().getHostAddress());
			return;
		}
		
		try{
			listener = new ServerThread(port,ta,outStatus,directory,ipAdd);
			ipp = ipAdd.getText();
		}
		catch(Exception e){
			ta.append("端口已被其他程序占用，请重试。\n");
			listener = null;
			hasStarted = false;
		}
		if(listener == null){
			start.setEnabled(true);
		}
		else{
			hasStarted = true;
			listener.start();
		}
	}
		
	
	public void exitServer()
	{
		ta.append("服务器关闭。\n");
		if(listener!=null){
			listener.stop();
		}
		System.exit(0);
	}
	
	public void stopServer()
	{
		start.setEnabled(true);
		over.setEnabled(false);
		listener.suspend();
		ta.append("服务器已停止。\n");
		outStatus.setText("服务器已停止。");
	}
	
	public void selectPath()
	{
		String str = "";
		String loc = "";
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("主目录");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) { 
	        //str += chooser.getCurrentDirectory();
	        str += chooser.getSelectedFile();
	        loc = str.replaceAll("\\\\", "/");	//Windows路径到JAVA路径的转换
	    }
	    directory.setText(loc);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if(command.equals("Start"))
		{
			startServer();
		}
		if(command.equals("Stop"))
		{
			stopServer();
		}
		if(command.equals("Exit"))
		{
			exitServer();
		}
		if(command.equals("..."))
		{
			selectPath();
		}
	}
	
	public static void main(String[] args)
	{
		Surface sf = new Surface();
		sf.init();
		//sf.outStatus.setText("服务器已开启：220.111.332.444:8000 sdafsagassasa");
	}

}
