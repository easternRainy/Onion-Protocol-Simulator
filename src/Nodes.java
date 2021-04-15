import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.SQLOutput;
import java.util.*;

/**
 * collection of OnionAgents
 */
public class Nodes {
    private int num; // number of agents
    private OnionAgent nodes[];
    private HashMap<PublicKey, Integer> pkToAgent;

    public Nodes(int n) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        this.num = n;
        this.nodes = new OnionAgent[n];
        for(int i=0; i<n; i++) {
            System.out.println("Creating agent " + i + " now!");
            this.nodes[i] = new OnionAgent();
        }
    }

    public Nodes(int n, int keySize) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException{
        this.num = n;
        this.nodes = new OnionAgent[n];
        for(int i=0; i<n; i++) {
            System.out.println("Creating agent " + i + " now!");
            this.nodes[i] = new OnionAgent(keySize);
        }
    }

    /**
     * randomly select nodes to pass information
     * @param n
     * @return
     */
    public int[] randomPath(int n) {
        if(n > num) {
            throw new IllegalArgumentException();
        }

        ArrayList<Integer> agentIndex = new ArrayList<>();
        for(int i=0; i<this.num; i++) {
            agentIndex.add(i);
        }

        Collections.shuffle(agentIndex);
        //System.out.println(agentIndex);

        int result[] = new int[n];
        for(int i=0; i<n; i++) {
            result[i] = agentIndex.get(i);
        }

        //System.out.println(Arrays.toString(result));
        return result;
    }

    /**
     * send message but user make specific paths
     * @param from
     * @param to
     * @param data
     * @param path
     */
    public void sendMessage(OnionAgent from, OnionAgent to, String data, int[] path) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        for(int i=0; i<path.length; i++) {
            if(path[i] >= this.num) {
                throw new IllegalArgumentException();
            }
        }

        System.out.println("The path should be: " + Arrays.toString(path));

        String encryptedMessage = this.encrypt(from, to, data, path);

        System.out.println("Begin Now!");

        OnionAgent inputNode = this.nodes[path[0]];
        Message tmp = new Message(path[0], encryptedMessage);
        System.out.println(tmp.display());
        // Suppose the encrypted message is sent to inputNode, now inputNode is decrypting
        tmp = Message.toMessage(inputNode.decrypt(encryptedMessage, inputNode.getEncryptSize()));

        int next;
        while((next = tmp.getNext()) != -2) {
            System.out.println(tmp.display());
            System.out.println("Decrypt the content now...");
            tmp = Message.toMessage(this.nodes[next].decrypt(tmp.getData(), this.nodes[next].getEncryptSize()));
        }

        // Now next=-2, which means next target should be "to"
        System.out.println(tmp.display());
        tmp = Message.toMessage(to.decrypt(tmp.getData(), to.getEncryptSize()));
        System.out.println(tmp.display());

    }

    /**
     * send message through random number of middle agents
     * @param from
     * @param to
     * @param data
     */
    public void sendMessage(OnionAgent from, OnionAgent to, String data) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException{
        Random rand = new Random();
        int numPath = rand.nextInt(this.num);
        this.sendMessage(from, to, data, numPath);
    }

    /**
     * sent message, but user specifies the number of middle agents
     * @param from
     * @param to
     * @param data
     * @param numPath
     */
    public void sendMessage(OnionAgent from, OnionAgent to, String data, int numPath) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException{
        int[] randomPath = this.randomPath(numPath);
        this.sendMessage(from, to, data, randomPath);
    }

    /**
     * encrypt message in an onion way
     * we can assume the path is valid because it is validated by sendMessage method
     * @param from
     * @param to
     * @param data
     * @param path
     * @return
     */
    private String encrypt(OnionAgent from, OnionAgent to, String data, int[] path) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        Message message = new Message(-1, data);
        int chunkSize = 53;
        String result = from.encrypt(message.toString(), to.getPublicKey(), chunkSize);

        Message tmp;
        for(int i=path.length-1; i>=0; i--) {
            // process out node
            if(i == path.length-1) {
                tmp = new Message(-2, result);
            } else {
                tmp = new Message(path[i+1], result);
            }

            result = from.encrypt(tmp.toString(), this.nodes[path[i]].getPublicKey(), chunkSize);
        }

        System.out.println("The final encryped message to be sent to input OnionAgent is:\n " + result);
        return result;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        Nodes nodes = new Nodes(10);
        nodes.randomPath(9);
    }
}

