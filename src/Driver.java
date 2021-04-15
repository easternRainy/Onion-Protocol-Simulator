import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Driver {
    // Create a list of OnionAgents
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        int keySize = 512;
        int numRelay = 3;
        Nodes nodes = new Nodes(100, keySize);
        OnionAgent from = new OnionAgent(keySize);
        OnionAgent to = new OnionAgent(keySize);
        String message = "This is a very important message, you should always encrypt it with RSA and use at least 10 relay nodes";
        nodes.sendMessage(from, to, message,numRelay);
    }

}
