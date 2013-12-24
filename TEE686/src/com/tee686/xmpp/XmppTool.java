package com.tee686.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;

public class XmppTool {

	private static XMPPConnection con = null;
    private static int PORT = 5222;  
    private static String SERVER_NAME = "192.168.1.101";  
	
    //打开连接
	private static void openConnection() {
		try {
			//设置服务器url和端口
			ConnectionConfiguration connConfig = new ConnectionConfiguration(SERVER_NAME, PORT);
			connConfig.setSASLAuthenticationEnabled(false);
			con = new XMPPConnection(connConfig);
			con.connect();
			/*
			con.addConnectionListener(new ConnectionListener() {
				
	            @Override
				public void connectionClosed() {
	                
	            	System.out.println("关闭连接");
	            }
	            @Override
				public void connectionClosedOnError(Exception e) {
	            	System.out.println("关闭连接异常");
	            }
	            @Override
				public void reconnectingIn(int seconds) {
	            	System.out.println("重新连接"+seconds);
	            }
	            @Override
				public void reconnectionFailed(Exception e) {
	            	System.out.println("重新连接失败");
	            }
	            @Override
				public void reconnectionSuccessful() {
	            	System.out.println("重新连接成功");
	            }
	        });		
			*/
		}
		catch (XMPPException xe) 
		{
			System.out.println(xe.getXMPPError().toString());
			xe.printStackTrace();
		}
	}

	//获得连接对象
	public static XMPPConnection getConnection() {
		if (con == null) {
			openConnection();
		}
		else if(con != null&&!con.isConnected())
		{
			openConnection();
		}
		return con;
	}

	//关闭连接
	public static void closeConnection() {
		con.disconnect();
		con = null;
	}

    //用户登录  
    public static boolean login(String account, String passwd)  
    {  
        ConnectionConfiguration config = new ConnectionConfiguration(SERVER_NAME, PORT);  
        //config.setSASLAuthenticationEnabled(false);  
        con = new XMPPConnection(config);  
        try  
        {  
            con.connect();  
            con.login(account, passwd);  
            return true;  
  
        }  
        catch (XMPPException e)  
        {  
            e.printStackTrace();  
            return false;  
        }  
    }  
    
	public static void addPacketListener(PacketListener myListener,
			OrFilter allPF) {
		con.addPacketListener(myListener, allPF);
	}
}
