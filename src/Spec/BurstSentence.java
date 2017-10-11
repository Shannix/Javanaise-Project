package Spec;

public class BurstSentence implements java.io.Serializable {

    String data;

    public void write(String text) {
        data = text;
    }

    public String read() {
        return data;
    }

}