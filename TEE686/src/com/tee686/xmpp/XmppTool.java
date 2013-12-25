package com.tee686.xmpp;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.util.Log;

public class XmppTool {

	private static XMPPConnection con = null;
    private static int PORT = 5222;  
    private static String SERVER_NAME = "192.168.2.6";  
	
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
    
	/**
	 * 更改用户状态
	 */
	public void setPresence(int code) {
		if (con == null)
			return;
		Presence presence;
		switch (code) {
			case 0:
				presence = new Presence(Presence.Type.available);
				con.sendPacket(presence);
				Log.v("state", "设置在线");
				break;
			case 1:
				presence = new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.chat);
				con.sendPacket(presence);
				Log.v("state", "设置Q我吧");
				System.out.println(presence.toXML());
				break;
			case 2:
				presence = new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.dnd);
				con.sendPacket(presence);
				Log.v("state", "设置忙碌");
				System.out.println(presence.toXML());
				break;
			case 3:
				presence = new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.away);
				con.sendPacket(presence);
				Log.v("state", "设置离开");
				System.out.println(presence.toXML());
				break;
			case 4:
				Roster roster = con.getRoster();
				Collection<RosterEntry> entries = roster.getEntries();
				for (RosterEntry entry : entries) {
					presence = new Presence(Presence.Type.unavailable);
					presence.setPacketID(Packet.ID_NOT_AVAILABLE);
					presence.setFrom(con.getUser());
					presence.setTo(entry.getUser());
					con.sendPacket(presence);
					System.out.println(presence.toXML());
				}
				// 向同一用户的其他客户端发送隐身状态
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(con.getUser());
				presence.setTo(StringUtils.parseBareAddress(con.getUser()));
				con.sendPacket(presence);
				Log.v("state", "设置隐身");
				break;
			case 5:
				presence = new Presence(Presence.Type.unavailable);
				con.sendPacket(presence);
				Log.v("state", "设置离线");
				break;
			default:
				break;
		}
	}
	
    /** 
     * 删除当前用户 
     * @param connection 
     * @return 
     */  
    public static boolean deleteAccount(XMPPConnection connection)  
    {  
        try {  
            connection.getAccountManager().deleteAccount();  
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    }  
    
    /** 
     * 修改密码 
     * @param connection 
     * @return 
     */  
    public static boolean changePassword(XMPPConnection connection,String pwd)  
    {  
        try {  
            connection.getAccountManager().changePassword(pwd);  
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    } 
	
	public static void addPacketListener(PacketListener myListener,
			OrFilter allPF) {
		con.addPacketListener(myListener, allPF);
	}
}
