package GroupChat.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Message {

    private String type;
    private byte[] b;
    private File file;

    public void setType(String x) {
        this.type = x;
    }

    public String getType() {
        return this.type;
    }

    public void setBody(String x) {
        b = x.getBytes();
    }

    public byte[] getBody() {
        return this.b;
    }

    public void setFile(File f) {
        this.file = f;
    }

    public byte[] getFile() {
        Path f = file.toPath();
        try {
            return Files.readAllBytes(f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
