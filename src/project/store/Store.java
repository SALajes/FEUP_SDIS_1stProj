package project.store;

import project.chunk.Chunk;
import project.peer.Peer;

import java.io.File;
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
    private static String stored_directory_path;
    private static String restored_directory_path;

    public void initializeStore(){
        this.peer_directory_path = Peer.id + "_directory/";
        this.files_directory_path = peer_directory_path + "files/";
        this.stored_directory_path = peer_directory_path + "stored/";
        this.restored_directory_path = peer_directory_path + "restored/";

        File directory = new File(this.peer_directory_path);

        if(directory.exists()){
            //what?
        }
        else{
            new File(this.files_directory_path).mkdirs();
            new File(this.stored_directory_path).mkdirs();
            new File(this.restored_directory_path).mkdirs();
        }
    }

    public static Store getStore(){
        return store;
    }

    public static String getFiles(String file_name) {
        return files.get(file_name);
    }

    public static void addFile(String file_name, String file_id) {
        if(getStore() != null ) {
            System.out.println("Other version detected, storing new by replacing");
        }
        files.put(file_name, file_id);
    }

    public static void removeFile(String file_name) {
        files.remove(file_name);
    }

    public static void storeChunk(String file_id, int chunk_no, Chunk chunk){
        stored_chunks.put(file_id + "_" + chunk_no, chunk);
    }

    public static Chunk retrieveChunk(String file_id, int chunk_no){
        if(stored_chunks.containsKey(file_id + "_" + chunk_no))
            return stored_chunks.get(file_id + "_" + chunk_no);
        else return null;
    }


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
}
