import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

public class OnionAgent {
    private int keySize;
    private int encryptSize;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public OnionAgent() throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException{

        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        this.keySize = keyPairGenerator.getKeySize();
        this.privateKey = keyPairGenerator.getPrivateKey();
        this.publicKey = keyPairGenerator.getPublicKey();
        this.encryptSize = RSAUtil.keySizeToEncryptSize(this.keySize);
    }

    public OnionAgent(int keySize) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        this.keySize = keySize;
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator(keySize);
        this.privateKey = keyPairGenerator.getPrivateKey();
        this.publicKey = keyPairGenerator.getPublicKey();
        this.encryptSize = RSAUtil.keySizeToEncryptSize(this.keySize);
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * encrypt using itself's public key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     */
    public String encrypt(String data) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        return this.encrypt(data, this.publicKey);
    }

    /**
     * encrypt using other's public key
     * assume data is in proper length
     * @param data
     * @param publicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     */
    public String encrypt(String data, PublicKey publicKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        String strPublicKey = RSAUtil.publicKeyToString(publicKey);
        return Base64.getEncoder().encodeToString(RSAUtil.encrypt(data, strPublicKey));
    }

    public String decrypt(String encryptedString) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        return RSAUtil.decrypt(encryptedString, RSAUtil.privateKeyToString(this.privateKey));
    }

    //-------if the data is long, need to chunk them--------
    public String encrypt(String data, PublicKey publicKey, int chunkSize) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        ArrayList<String> chunks = RSAUtil.split(data, chunkSize);
        StringBuilder sb = new StringBuilder();
        for(String chunk: chunks) {
            sb.append(this.encrypt(chunk, publicKey));
        }

        return sb.toString();
    }

    public String decrypt(String encryptedString, int chunkSize) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException{
        ArrayList<String> chunks = RSAUtil.split(encryptedString, chunkSize);
        StringBuilder sb = new StringBuilder();
        for(String chunk: chunks) {
            sb.append(this.decrypt(chunk));
        }

        return sb.toString();
    }

    public int getKeySize() {
        return this.keySize;
    }

    public int getEncryptSize() {
        return this.encryptSize;
    }




    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        OnionAgent agent1 = new OnionAgent();
        OnionAgent agent2 = new OnionAgent();
        int chunkSize = 20;

        String data = "This is an very looooooonnnnnnnnggggggggggggggggggggggggggggg articleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";

        String agent1Encrypt = agent1.encrypt(data, agent1.getPublicKey(), chunkSize);
        System.out.println("Agent 1 encrypt: " + agent1Encrypt);

        String agent2Encrypt = agent2.encrypt(agent1Encrypt, agent2.getPublicKey(), chunkSize);
        System.out.println("Agent 2 encrypt: " + agent2Encrypt);

        String agent2Decrypt = agent2.decrypt(agent2Encrypt, agent2.getEncryptSize());
        System.out.println("Agent2 decrpyt: " + agent2Decrypt);

        String agent1Decrypt = agent1.decrypt(agent2Decrypt, agent2.getEncryptSize());
        System.out.println("Agent1 decrypt: " + agent1Decrypt);
    }
}

