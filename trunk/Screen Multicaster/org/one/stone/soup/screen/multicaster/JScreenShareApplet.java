package org.one.stone.soup.screen.multicaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.one.stone.soup.screen.recorder.DesktopScreenRecorder;
import org.one.stone.soup.screen.recorder.ScreenRecorder;
import org.one.stone.soup.screen.recorder.ScreenRecorderListener;
import org.one.stone.soup.xml.XmlElement;

public class JScreenShareApplet extends JApplet implements ScreenRecorderListener{
	
		private ScreenRecorder recorder;

		private JLabel text;
		private JLabel frameLabel;
		private int frameCount;
		
		private Socket socket; 
		
		public JScreenShareApplet()
		{
			System.out.println("Build 1.02");

			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1,2));
			panel.setBackground(Color.black);
			
			frameLabel = new JLabel("Frame: 0");
			frameLabel.setBackground(Color.black);
			frameLabel.setForeground(Color.red);
			text=new JLabel("Not Connected");
			text.setBackground(Color.black);
			text.setForeground(Color.red);
			
			panel.add(text);
			panel.add(frameLabel);
			
			this.getContentPane().add( panel,BorderLayout.SOUTH );
			
			this.setVisible(true);
		}

		public void init()
		{
			String address = getParameter("address");
			String port = getParameter("port");
			String page = getParameter("page");
			
			try{
				XmlElement header = new XmlElement("Recorder");
				Rectangle screen = new Rectangle( Toolkit.getDefaultToolkit ().getScreenSize() );
				
				socket = new Socket(address,Integer.parseInt(port));
				socket.getOutputStream().write( ("GET "+page+" HTTP/1.1\r\n").getBytes() );
				socket.getOutputStream().write( ("\r\n\r\n").getBytes() );
				
				socket.getOutputStream().write(header.toXml().getBytes());
				socket.getOutputStream().flush();
				
				recorder = new DesktopScreenRecorder(socket.getOutputStream(),this);
				recorder.startRecording();
				
				text.setText("Connected to "+address+":"+port);
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}

		}

		public void frameRecorded(boolean fullFrame)
		{
			frameCount++;
			frameLabel.setText("Frame: "+frameCount);		
			
			try{
				socket.getInputStream().read();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				recorder.stopRecording();
			}
		}

		public void recordingStopped()
		{
			recorder = null;
			
			text.setText("Not Connected");
		}	
}
