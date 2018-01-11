package Client;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainActivityGUI implements ActionListener, ListSelectionListener {

	private JFrame frame;
	private JList<ChatGUI> privateChatList, groupChatList;
	private JList<String> onlineList;
	private DefaultListModel<ChatGUI> privateChatDLM, groupChatDLM;
	private DefaultListModel<String> onlineUsersListDLM;
	private DefaultListModel<String> offlineUsersListDLM;
	private JButton Change, newPrivateChatBtn, newGroupChatBtn, deleteChatBtn, cancelDeleteBtn;
	private ArrayList<String> onlineUsers,offlineUsers;
	private String screenName;
	private JPanel panel, deleteChatPanel;
	private JMenuItem mnLogout, mnChangeDisplay,mntmChangePassword;
	private String userName,salt;
	private JLabel lblUsername;
	private ArrayList<ChatGUI>allChats= new ArrayList<>();
	private Communication c;

	public MainActivityGUI(ArrayList<String> onlineUsers, ArrayList<String>offlineUsers,String screenName,String userName,String salt,Communication c) {
		this.onlineUsers=onlineUsers;
		this.offlineUsers=offlineUsers;
		this.screenName=screenName;
		this.userName=userName;
		this.salt=salt;
		this.c=c;
		initialize();
		frame.setVisible(true);
	}

	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 975, 650);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		frame.setJMenuBar(menuBar);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		menuBar.add(mnHelp);
		JPanel about = new JPanel();
		about.setLayout(new BorderLayout());
		JLabel aboutTitle = new JLabel("About");
		JTextArea aboutText = new JTextArea("A Chat messenger that allows you to connect with others. \n\rCreate Group Chats with many users,Private Chats and send files to users easily.");
		about.add(aboutTitle,BorderLayout.NORTH);
		about.add(aboutText,BorderLayout.CENTER);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);
		JMenuItem mntmNewMenuItem = new JMenuItem("About");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, about, "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		mnHelp.add(mntmNewMenuItem);

		JMenu mnAccount = new JMenu("Account");
		mnAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		menuBar.add(mnAccount);

		mnChangeDisplay = new JMenuItem("Change Display Name");
		mnChangeDisplay.addActionListener(this);
		mnAccount.add(mnChangeDisplay);

		mntmChangePassword = new JMenuItem("Change Password");
		mntmChangePassword.addActionListener(this);
		mnAccount.add(mntmChangePassword);

		mnLogout = new JMenuItem("Logout");
		mnAccount.add(mnLogout);
		mnLogout.addActionListener(this);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setEnabled(false);
		splitPane_1.setResizeWeight(0.5);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setEnabled(false);
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setLeftComponent(splitPane_2);

		JSplitPane splitPane_4 = new JSplitPane();
		splitPane_4.setEnabled(false);
		splitPane_4.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_2.setRightComponent(splitPane_4);

		JLabel label = new JLabel("< Online Users >");
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		splitPane_4.setLeftComponent(label);

		onlineUsersListDLM= new DefaultListModel<String>();
		onlineList = new JList<String>(onlineUsersListDLM);
		JScrollPane scrollPane_1 = new JScrollPane(onlineList);
		onlineList.addListSelectionListener(this);
		onlineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


		offlineUsersListDLM= new DefaultListModel<String>();
		JList<String> offlineList = new JList<String>(offlineUsersListDLM);	
		JScrollPane scrollPane_2 = new JScrollPane(offlineList);

		fillUserLists(onlineUsers,offlineUsers);
		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(10, 30));
		splitPane_2.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		lblUsername = new JLabel("Screen name: "+screenName);
		lblUsername.setIconTextGap(9);
		lblUsername.setIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/user.png")));
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel_3.add(lblUsername, BorderLayout.CENTER);

		Change = new JButton("Change");
		Change.setMargin(new Insets(2, 10, 2, 10));
		panel_3.add(Change, BorderLayout.EAST);
		Change.addActionListener(this);

		JSplitPane splitPane_5 = new JSplitPane();
		splitPane_5.setEnabled(false);
		splitPane_5.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setRightComponent(splitPane_5);

		splitPane_5.setRightComponent(scrollPane_2);
		splitPane_4.setRightComponent(scrollPane_1);
		JLabel label_1 = new JLabel("<Offline Users >");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		splitPane_5.setLeftComponent(label_1);

		splitPane_1.setDividerLocation(320);

		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_3.setEnabled(false);
		splitPane.setLeftComponent(splitPane_3);

		panel = new JPanel();
		splitPane_3.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		privateChatDLM = new DefaultListModel<ChatGUI>();

		JPanel panel_5 = new JPanel();
		panel_5.setBackground(new Color(211, 211, 211));
		splitPane_3.setLeftComponent(panel_5);

		newPrivateChatBtn = new JButton("");
		newPrivateChatBtn.setContentAreaFilled(false);
		newPrivateChatBtn.setRolloverIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/privateChatIconRollOver.png")));
		newPrivateChatBtn.setBorderPainted(false);
		newPrivateChatBtn.setIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/privateChatIcon.png")));
		newPrivateChatBtn.setBorder(null);
		newPrivateChatBtn.addActionListener(this);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));
		panel_5.add(newPrivateChatBtn);
		newGroupChatBtn = new JButton("");
		newGroupChatBtn.setIconTextGap(2);
		newGroupChatBtn.setContentAreaFilled(false);
		newGroupChatBtn.setRolloverIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/groupChatRollOver.png")));
		newGroupChatBtn.setBorderPainted(false);
		newGroupChatBtn.setIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/groupChatIcon.png")));
		newGroupChatBtn.setBorder(new EmptyBorder(0, 0, 0, 0));
		panel_5.add(newGroupChatBtn);

		deleteChatBtn = new JButton("");
		deleteChatBtn.setContentAreaFilled(false);
		deleteChatBtn.setRolloverIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/deletechatRollOver.png")));
		deleteChatBtn.setBorderPainted(false);
		deleteChatBtn.setBorder(new EmptyBorder(0, 0, 0, 0));
		deleteChatBtn.setIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/deletechatIcon.png")));
		panel_5.add(deleteChatBtn);
		deleteChatBtn.addActionListener(this);
		newGroupChatBtn.addActionListener(this);
		splitPane.setDividerLocation(550);
		deleteChatPanel =new JPanel();
		deleteChatPanel.setLayout(new BorderLayout());
		cancelDeleteBtn = new JButton("Cancel");
		cancelDeleteBtn.addActionListener(this);
		JLabel label_2 = new JLabel("   Please select chat to delete:");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		deleteChatPanel.add(label_2,BorderLayout.CENTER);
		deleteChatPanel.add(cancelDeleteBtn,BorderLayout.EAST);
		panel.add(deleteChatPanel,BorderLayout.NORTH);
		deleteChatPanel.setVisible(false);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel.add(tabbedPane, BorderLayout.CENTER);
		privateChatList = new JList<ChatGUI>(privateChatDLM);
		privateChatList.setBorder(null);
		tabbedPane.addTab("  Private Chats  ", null, privateChatList, null);
		privateChatList.setFixedCellHeight(30);
		privateChatList.addListSelectionListener(this);
		privateChatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupChatDLM = new DefaultListModel<ChatGUI>();
		groupChatList = new JList<ChatGUI>(groupChatDLM);
		tabbedPane.addTab("  Group Chats  ", null, groupChatList, null);
		groupChatList.setFixedCellHeight(30);
		groupChatList.addListSelectionListener(this);
		groupChatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		frame.setLocationRelativeTo(null);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==newGroupChatBtn) {
			NewChatGUI newChat = new NewChatGUI("group",this,c,onlineUsers);
		}
		if(e.getSource()==newPrivateChatBtn) {
			NewChatGUI newChat = new NewChatGUI("private",this,c,onlineUsers);
		}
		if(e.getSource()==deleteChatBtn) {
			deleteChatPanel.setVisible(true);
		}
		if(e.getSource()==cancelDeleteBtn) {
			deleteChatPanel.setVisible(false);
		}
		if(e.getSource()==mnLogout) {
			int res=JOptionPane.showConfirmDialog(frame, "Are You Sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(res==JOptionPane.YES_OPTION) {
				c.send(new Message("LOGOUT",userName));
				frame.dispose();
				c.loginGUI = new LoginGUI(c);
			}
		}
		if(e.getSource()==mnChangeDisplay||e.getSource()==Change) {
			String newScreenName=JOptionPane.showInputDialog(frame,"Enter New ScreenName:");
			if(newScreenName!=null) {
				while(newScreenName!=null&&newScreenName.isEmpty()) {
					newScreenName=JOptionPane.showInputDialog(frame,"Error! Incomplete Fields\nPlease enter new Screen Name:");
				}		
				if(onlineUsers.contains(newScreenName.trim())||offlineUsers.contains(newScreenName.trim())) {
					JOptionPane.showMessageDialog(frame,"Screen Name already in use!","Error",JOptionPane.ERROR_MESSAGE);
				}if(newScreenName.trim().length()>15) {
					JOptionPane.showMessageDialog(frame,"Screen Name length too large!","Error",JOptionPane.ERROR_MESSAGE);
				}
				else {
					c.send(new Message("SCREENNAME",screenName,newScreenName,true));
					setScreenName(newScreenName);
					JOptionPane.showMessageDialog(frame,"Screen Name changed!", "Screen Name",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		if(e.getSource()==mntmChangePassword) {
			String newPassword=(JOptionPane.showInputDialog(frame,"Enter New Password:"));
			if(newPassword!=null) {
				while(newPassword!=null&&newPassword.isEmpty()) {
					newPassword=JOptionPane.showInputDialog(frame,"Error! Incomplete Fields\nPlease enter new Password:");
				}
				String confirmPassword=(JOptionPane.showInputDialog(frame,"Confirm New Password:"));
				while(confirmPassword!=null&&confirmPassword.isEmpty()) {
					confirmPassword=JOptionPane.showInputDialog(frame,"Error! Incomplete Fields\nPlease confirm Password:");
				}
				if(newPassword.equals(confirmPassword)) {
					c.send(new Message("PASSWORD",userName,BCrypt.hashpw(confirmPassword, salt)));
					JOptionPane.showMessageDialog(frame, "Password","Password changed!",JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(frame, "Password","Passwords do not match! Please try again.",JOptionPane.OK_OPTION);
				}
			}
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if(onlineList.getSelectedIndex()!=-1) {
			boolean exists=false;
			for(ChatGUI chat :allChats) {
				if(chat.getChatName().equals(onlineList.getSelectedValue())){
					chat.resetMessageCount();
					chat.setVisible(true);
					exists=true;
				}
			}
			if(!exists) {
				ChatGUI chat =new ChatGUI(NewChatGUI.createChatRoomName(onlineList.getSelectedValue()),this,c);
				addchat(chat);
			}
			onlineList.clearSelection();
		}
		if(privateChatList.getSelectedIndex()!=-1) {
			if(deleteChatPanel.isVisible()) {
				int res = JOptionPane.showConfirmDialog(null, "Are You Sure you want to delete "+privateChatList.getSelectedValue()+" ?", "Error", JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION) {
					removechat(privateChatDLM.get(privateChatList.getSelectedIndex()));
					privateChatDLM.remove(privateChatList.getSelectedIndex());
					JOptionPane.showMessageDialog(null, "Chat deleted!", "", JOptionPane.ERROR_MESSAGE);
				}
				deleteChatPanel.setVisible(false);
			}
			else {
				ChatGUI chat=getChat(privateChatList.getSelectedValue().getFullChatName());
				chat.resetMessageCount();
				chat.setVisible(true);
			}
			privateChatList.clearSelection();
		}

		if(groupChatList.getSelectedIndex()!=-1) {
			if(deleteChatPanel.isVisible()) {
				int res = JOptionPane.showConfirmDialog(null, "Are You Sure you want to delete "+groupChatList.getSelectedValue()+" ?", "Error", JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION) {
					removechat(groupChatDLM.get(groupChatList.getSelectedIndex()));
					groupChatDLM.remove(groupChatList.getSelectedIndex());
					JOptionPane.showMessageDialog(null, "Chat deleted!", "", JOptionPane.ERROR_MESSAGE);
				}
				deleteChatPanel.setVisible(false);
			}
			else {
				ChatGUI chat=getChat(groupChatList.getSelectedValue().getFullChatName());
				chat.resetMessageCount();
				chat.setVisible(true);
			}
			groupChatList.clearSelection();
		}
	}


	public void fillUserLists(ArrayList<String>onlineUsers,ArrayList<String>offlineUsers){
		onlineUsersListDLM.clear();
		offlineUsersListDLM.clear();
		this.offlineUsers=offlineUsers; 
		this.onlineUsers=onlineUsers; 
		for(String q:onlineUsers) {
			if(!q.equals(screenName)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						onlineUsersListDLM.addElement(q);
					}
				});
			}
		}

		for(String q:offlineUsers) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					offlineUsersListDLM.addElement(q);
				}
			}); 
		}
	}
	public void addToPrivateChatList(ChatGUI c) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				privateChatDLM.addElement(c);
			}
		});
	}

	public void removeFromPrivateChatList(ChatGUI c) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				privateChatDLM.removeElement(c);
			}
		});
	}

	public void addToGroupChatList(ChatGUI c) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				groupChatDLM.addElement(c);
			}
		});
	}

	public void addToOnlineList(String c) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				onlineUsersListDLM.addElement(c);
			}
		});
	}

	public void removeFromOnlineList(String c) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				onlineUsersListDLM.removeElement(c);
			}
		});
	}

	public void setScreenName(String screenName) {
		this.screenName=screenName;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				lblUsername.setText("Screen name: "+screenName);
			}
		});
	}

	public synchronized void addchat(ChatGUI c) {
		allChats.add(c);
		if(c.getChatType().equals("private"))
			addToPrivateChatList(c);
		else
			addToGroupChatList(c);
	}

	public synchronized void removechat(ChatGUI chatName) {
		allChats.remove(chatName);
	}

	public synchronized void updatechat(String chatName, String srcUser, String message) {
		ChatGUI chat = getChat(chatName);
		chat.setMessages("[ "+srcUser+ " ] : " + message);
	}

	public  boolean chatExists(String chatName) {
		for(ChatGUI chat:allChats) {
			if(chat.getFullChatName().equals(chatName)) {
				return true;
			}
		}
		return false;
	}

	public ChatGUI getChat(String chatName) {
		for(ChatGUI chat:allChats) {
			if(chat.getFullChatName().equals(chatName)) {
				return chat;
			}
		}
		return null;
	}

	public ArrayList<String> getOnlineUsers() {
		return onlineUsers;
	}


	public ArrayList<String> getOfflineUsers() {
		return offlineUsers;
	}

	public ArrayList<ChatGUI> getAllChats() {
		return allChats;
	}

	public String getScreenName() {
		return screenName;
	}

	public JList getOnlineJList() {
		return onlineList;
	}

}