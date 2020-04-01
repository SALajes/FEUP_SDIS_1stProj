package project.store;

import project.chunk.Chunk;
import project.peer.Peer;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

public class Store {

    private static Store store = new Store();

    private static Hashtable<String, String> files = new Hashtable();
    private static Hashtable<String, Chunk> stored_chunks = new Hashtable<>();
    private static Hashtable<String, String> restored_files = new Hashtable<>();

    private static String peer_directory_path;
    private static String files_directory_path;
    private static String backup_directory_path;
    private static String restored_directory_path;

    private int space_with_storage = 0; //in bytes
    private Integer space_allow = -1; //Initial there isn't restrictions of space

    public Store() {
        initializeStore();
    }

    /**
     * creates the for needed directory
     */
    public void initializeStore(){

        //setting the directory name
        peer_directory_path = Peer.id + "_directory/";
        files_directory_path = peer_directory_path + "files/";
        backup_directory_path = peer_directory_path + "stored/";
        restored_directory_path = peer_directory_path + "restored/";

        create_directory(peer_directory_path);
        create_directory(files_directory_path);
        create_directory(backup_directory_path);
        create_directory(restored_directory_path);

    }

    /**
     * Idempotent Method
     * @return
     */
    public boolean create_directory(String directory_path) {
        File directory = new File(directory_path);
        //mkdirs() returns true if created and false on failure or if exists
        if(!directory.mkdirs() && !directory.isDirectory()){
            System.out.println("Directory " + directory + " wasn't created!");
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public static Store getStore(){
        return store;
    }

    /**
     *
     * @param file_name
     * @return
     */
    public static String getFiles(String file_name) {
        return files.get(file_name);
    }

    /**
     *
     * @param file_name
     * @param file_id
     */
    public static void addFile(String file_name, String file_id) {
        if(getStore() != null ) {
            System.out.println("Other version detected, storing new by replacing");
        }
        files.put(file_name, file_id);
    }

    /**
     * 
     * @param file_name
     */
    public static void removeFile(String file_name) {
        files.remove(file_name);
    }

    /**
     *
     * @param file_id
     * @param chunk_no
     * @param chunk
     */
    public static void storeChunk(String file_id, int chunk_no, Chunk chunk){
        stored_chunks.put(file_id + "_" + chunk_no, chunk);
    }

    /**
     *
     * @param file_id
     * @param chunk_no
     * @return
     */
    public static Chunk retrieveChunk(String file_id, int chunk_no){
        if(stored_chunks.containsKey(file_id + "_" + chunk_no))
            return stored_chunks.get(file_id + "_" + chunk_no);
        else return null;
    }

    /**
     * encodes a file name with a sha-256 cryptographic hash function
     * @param file
     * @return file id
     */
    public static String createFileId(File file) {

        String file_name = file.getName();

        //encoded file name uses the file.lastModified() that ensures that a modified file has a different fileId
        String file_name_to_encode = file_name + file.lastModified();

        //identifier is obtained by applying SHA256, a cryptographic hash function, to some bit string.
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(file_name_to_encode.getBytes(StandardCharsets.UTF_8));

        return String.valueOf(hash);
    }

    /**
     * Given the chunk_body ( the data ), stores it in a file
     * @param file_id
     * @param chunk_no
     * @param chunk_body
     * @return
     */
    public boolean storeChunk(String file_id, int chunk_no, byte[] chunk_body) {

        //check if the chunk already exists
        if (retrieveChunk(file_id, chunk_no) != null) {
            System.out.println("A chunk with number " + chunk_no + " and file_id " + file_id + " already exists.");
            return true;
        }

        //check if there is enough storage
        if (!this.hasSpace(chunk_body.length)) {
            System.out.println("A chunk with number " + chunk_no + " and file_id " + file_id + " can't be store because there isn't space left.");
            return false;
        }

        String chunk_dir =  this.backup_directory_path + "/" + file_id + "/";

        // Idempotent Method
        create_directory(chunk_dir);
        String chunk_path = String.format( chunk_dir + chunk_no);


        try {
            FileOutputStream file = new FileOutputStream(chunk_path);
            file.write(chunk_body);
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //update the current space used for storage
       this.AddToSpace_with_storage(chunk_body.length);
        return true;
    }

    /**
     * used to check if there is space to store a new chunk
     * @param space_wanted
     * @return
     */
    public boolean hasSpace(Integer space_wanted) {
        return (this.space_allow >= this.space_with_storage + space_wanted);
    }

    /**
     * used when a new chunk is store ( by backup )
     * @param space_with_storage
     */
    public void AddToSpace_with_storage(int space_with_storage) {
        this.space_with_storage += space_with_storage;
    }

    /**
     *
     * @return spaced used for storage
     */
    public Integer getSpace_with_storage() {
        return space_with_storage;
    }

    /**
     * sets the spaces used for storage
     * @param space_with_storage
     */
    public void setSpace_with_storage(int space_with_storage) {
        this.space_with_storage = space_with_storage;
    }
}
