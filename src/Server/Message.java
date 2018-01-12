package Server;

import java.io.*;
import java.util.ArrayList;

public class Message implements Serializable {

    private String type, srcUser, messageBody, username, password, chatName, fileName, reason, screenName, oldScreenName, newScreenName;
    private byte[] b;
    private ArrayList<String> destUsers = new ArrayList<>();
    private ArrayList<String> onlineUsers = new ArrayList<>();
    private ArrayList<String> offlineUsers = new ArrayList<>();
    private boolean response;

    public Message(String type, boolean response, String reason, String screenName) {
        this.type = type;
        this.response = response;
        this.reason = reason;
        this.screenName = screenName;
    }

    //server users type=USER
    public Message(String type, boolean response, String reason, String screenName, ArrayList<String> onlineUsers, ArrayList<String> offlineUsers) {
        this.type = type;
        this.response = response;
        this.reason = reason;
        this.screenName = screenName;
        this.offlineUsers = offlineUsers;
        this.onlineUsers = onlineUsers;
    }
    //clientSendingMessages type MESSAGE

    public Message(String type, String srcUser, ArrayList<String> destUsers, String messageBody, String chatName) {
        this.type = type;
        this.srcUser = srcUser;
        this.destUsers = destUsers;
        this.messageBody = messageBody;
        this.chatName = chatName;
    }
    //clientLogin type= LOGIN

    public Message(String type, String username, String password) {
        this.type = type;
        this.username = username;
        this.password = password;
    }

    //clientRegister type=REGISTER
    public Message(String type, String username, String password, String screenName) {
        this.type = type;
        this.screenName = screenName;
        this.username = username;
        this.password = password;
    }

    //clientSendFile type=FILE
    public Message(String type, String srcUser, ArrayList<String> destUsers, byte[] file, String fileName, String chatName) {
        this.type = type;
        this.srcUser = srcUser;
        this.destUsers = destUsers;
        this.b = file;
        this.fileName = fileName;
        this.chatName = chatName;
    }
    //logout

    public Message(String type, String username) {
        this.type = type;
        this.username = username;
    }
    //client user

    public Message(String type) {
        this.type = type;
    }
    //change screenname

    public Message(String type, String oldScreenName, String newScreenName, boolean c) {
        this.type = type;
        this.oldScreenName = oldScreenName;
        this.newScreenName = newScreenName;
    }

    public Message(String type, ArrayList<String> onlineUsers, ArrayList<String> offlineUsers) {
        this.type = type;
        this.onlineUsers = onlineUsers;
        this.offlineUsers = offlineUsers;
    }

    public String getReason() {
        return this.reason;
    }

    public String getType() {
        return this.type;
    }

    public String getChatName() {
        return this.chatName;
    }

    public ArrayList<String> getOnlineUser() {
        return this.onlineUsers;
    }

    public ArrayList<String> getOfflineUser() {
        return this.offlineUsers;
    }

    public String getSrcUser() {
        return this.srcUser;
    }

    public ArrayList<String> getDestUsers() {
        return this.destUsers;
    }

    public byte[] getFileBytes() {
        return this.b;
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public boolean getResponse() {
        return this.response;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getScreenName() {
        return this.screenName;
    }

    public String getOldScreenName() {
        return this.oldScreenName;
    }

    public String getNewScreenName() {
        return this.newScreenName;
    }

}
