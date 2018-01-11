package Client;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.awt.event.*;

public class FileSelector extends JFrame {

	private JFileChooser fc;
	private MainActivityGUI mainWindow;
	private Communication c;

	public FileSelector(String chatName, ArrayList<String>users,MainActivityGUI mainWindow,Communication c) {
		this.c=c;
		this.mainWindow=mainWindow;
		setBounds(100, 100, 512, 325);
		fc = new JFileChooser(FileSystemView.getFileSystemView());
		fc.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				File selectedFile = fc.getSelectedFile();
				if(selectedFile.length()>5120) {
					JOptionPane.showMessageDialog(null, "File too large. Maximum size: 5kb", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else {
					byte[] f = getFile(selectedFile);
					c.send(new Message("FILE", mainWindow.getScreenName() ,users, f, selectedFile.getName(), chatName));
					mainWindow.updatechat(chatName, mainWindow.getScreenName(), "File " +selectedFile.getName()+ " sent.");
					dispose();
				}
			}
		});
		fc.setDialogTitle("Send a file");
		setContentPane(fc);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public byte[] getFile(File file) {
		Path f = file.toPath();
		try {
			return Files.readAllBytes(f);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}