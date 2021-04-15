import java.util.ArrayList;

/**
 * Data Structure of the message
 */
public class Message {
    private int next; // next node to send the message, if next = -1, no need to sent
    private String data; // content of message
    private static final String separator = "SEPARATOR";
    public static final int chunkSize = 20;

    public Message(int next, String data) {
        this.next = next;
        this.data = data;
    }

    public String toString() {
        String result = "";
        result += next;
        result += this.separator;
        result += this.data;

        return result;
    }

    public String display() {
        String result = "{\n";
        result += "\tNext: " + this.next + "\n";
        result += "\tContent: " + this.data + "\n";
        result += "}";
        return result;
    }

    /**
     * convert a string to Message, usually for decrypted message to get which one to sent next
     * @param data
     * @return
     */
    public static Message toMessage(String data) {
        String[] splited = data.split(Message.separator);

        if(splited.length != 2) {
            return null;
        }

        return new Message(Integer.parseInt(splited[0]), splited[1]);
    }

    public int getNext() {
        return this.next;
    }

    public String getData() {
        return this.data;
    }

    public static void main(String[] args) {
        Message message = new Message(1, "La9AEAcSzGhkThNjw/Fa1mH2ROjLBl3K5RUNXaSS2v0iN2EKTTwaDJzxXx22cl2YUNmSMiCx/xS1pSzqpcd9ag==");
        System.out.println(message.toString());
        Message message2 = Message.toMessage("1SEPARATORLa9AEAcSzGhkThNjw/Fa1mH2ROjLBl3K5RUNXaSS2v0iN2EKTTwaDJzxXx22cl2YUNmSMiCx/xS1pSzqpcd9ag==");
        System.out.println(message2.display());
    }
}
