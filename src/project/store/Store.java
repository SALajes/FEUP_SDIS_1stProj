package project.store;

import project.Macros;
import project.chunk.Chunk;
import project.peer.Peer;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

public class Store {

    private static Store store = new Store();

    private static Hashtable<String, Chunk> stored_chunks = new Hashtable<>();
    private static Hashtable<String, String> restored_files = new Hashtable<>();

    private static String peer_directory_path;
    private static String files_directory_path;
    private static String files_info_directory_path;
    private static String backup_directory_path;
    private static String backup_info_directory_path;
    private static String restored_directory_path;

    private int space_with_storage = 0; //in bytes
    private Integer space_allow = -1; //Initial there isn't restrictions of space

    private Store() {
        initializeStore();
    }

    /**
     * get store instance
     * @return store instance
     */
    public static Store getStore(){
        return store;
    }

    /**
     * creates the four needed directory
     */
    public void initializeStore(){

        //setting the directory name
        peer_directory_path = Peer.id + "_directory/";
        files_directory_path = peer_directory_path + "files/";
        files_info_directory_path = peer_directory_path + "files_info.txt";
        backup_directory_path = peer_directory_path + "backup/";
        backup_info_directory_path = peer_directory_path + "backup_info.txt";
        restored_directory_path = peer_directory_path + "restored/";

        create_directory(peer_directory_path);
        create_directory(files_directory_path);
        create_empty_file(files_info_directory_path);
        create_directory(backup_directory_path);
        //if exists return true but doesn't creates a new file
        create_empty_file(backup_info_directory_path);
        create_directory(restored_directory_path);
    }

    /**
     * Idempotent Method that creates a directory given the path
     * @return success if directory was or is created and false if not
     */
    public boolean create_directory(String directory_path) {

        File directory = new File(directory_path);
        if (!directory.exists()) {
            //mkdirs() returns true if created and false on failure or if exists
            if (directory.mkdir()) {
                return true;
            } else {
                System.out.println("Directory " + directory + " wasn't created!");
                return false;
            }
        }
        return true;
    }

    /**
     * gets the path for the file with the files info
     * @return path for files info
     */
    public String get_files_info_directory_path(){
        return files_info_directory_path;
    }

    /**
     *
     * @param file_id encoded
     * @param chunk class chunk (with body)
     */
    public static void storeChunk(String file_id, Chunk chunk){
        stored_chunks.put(file_id + "_" + chunk.chunk_no, chunk);
    }

    /**
     *
     * @param file_id encoded
     * @param chunk_number number of the chunk
     * @return null if doesn't exists
     */
    public static Chunk retrieveChunk(String file_id, int chunk_number){
        if(stored_chunks.containsKey(file_id + "_" + chunk_number))
            return stored_chunks.get(file_id + "_" + chunk_number);
        else return null;
    }

    /**
     * encodes a file name with a sha-256 cryptographic hash function
     * @param file used to get name and lastModified date
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
     * @param file_id encoded
     * @param chunk_number number of the chunk
     * @param chunk_body data
     * @return true if successful or false otherwise
     */
    public boolean storeChunk(String file_id, int chunk_number, byte[] chunk_body) {

        //check if the chunk already exists
        if (retrieveChunk(file_id, chunk_number) != null) {
            System.out.println("A chunk with number " + chunk_number + " and file_id " + file_id + " already exists.");
            return true;
        }

        //check if there is enough storage
        if (!this.hasSpace(chunk_body.length)) {
            System.out.println("A chunk with number " + chunk_number + " and file_id " + file_id + " can't be store because there isn't space left.");
            return false;
        }

        String chunk_dir =  this.backup_directory_path + "/" + file_id + "/";

       // storeChunk(file_id, new Chunk(chunk_number,));

        // Idempotent Method
        create_directory(chunk_dir);
        String chunk_path =  chunk_dir + chunk_number;

        try {
            //FileOutputStream.write() method automatically create a new file and write content to it.
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
     * @param space_wanted space we pretend to use
     * @return true if space exist or false otherwise
     */
    public boolean hasSpace(Integer space_wanted) {
        //don't exit restrictions
        if(this.space_allow == -1)
            return true;
        return (this.space_allow >= this.space_with_storage + space_wanted);
    }

    /**
     * used when a new chunk is store ( by backup )
     * @param space_with_storage storage space added
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
     * @param space_with_storage the amount of space in bytes used for storage
     */
    public void setSpace_with_storage(int space_with_storage) {
        this.space_with_storage = space_with_storage;
    }

    /**
     * used when a chunk is deleted
     * @param space_with_storage the amount of space in bytes used for storage
     */
    public static void remove_space_with_storage(int space_with_storage) {
        space_with_storage -= space_with_storage;
    }

    /**
     * Creates an empty file to start the restoring procedure or the backup
     * @return true if successful, and false otherwise
     */
    public boolean create_empty_file_for_restoring(String file_name ) {
        String restore_file_path =  restored_directory_path + "/" + file_name;
        return create_empty_file(restore_file_path);
    }

    /**
     * Creates an empty file to start the restoring procedure or the backup/info file
     * if exists return true but doesn't creates a new file
     * @param file_path path of the file that is being created
     */
    public boolean create_empty_file(String file_path) {

        try {
            File file = new File(file_path);
            if(file.exists())
                return true;

            if(file.createNewFile()) {
                System.out.println("File: " + file + " was created");
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't create an empty file to start restoring");
            return false;
        }
        return false;
    }


    /**
     * This functions append the body of a chunk (file data) in the position desired ( calculated with chunk number)
     * Used for restoring a file with a certain given filename
     * @param file_name name of the file
     * @param chunk_number number of the chunk
     * @param chunk_data the array with the bytes to put
     * @return true if success and false otherwise
     */
    public boolean write_chunk_to_file(String file_name, int chunk_number, byte[] chunk_data) {

        String file_path =  restored_directory_path + "/" + file_name;

        //Random access file offers a seek feature that can go directly to a particular position
        try (RandomAccessFile file = new RandomAccessFile(file_path, "rw")) {
            file.seek(chunk_number * Macros.CHUNK_MAX_SIZE);
            file.write(chunk_data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't append chunk number " + chunk_number + " to file " + file_name + ".");
            return false;
        }
    }

    /**
     *
     * @param file_id encoded
     * @param chunk_number first chunk is number 0 and so on
     * @return data of the wanted file and chunk number
     */
    public byte[] get_chunk_data(String file_id, int chunk_number) {
        //check if the chunk is stored
        if (!stored_chunks.containsKey(file_id + "_" + chunk_number)) {
            System.err.println("Chunk isn't stored, can't get data!");
            return null;
        }

        //get chunk_path
        String chunk_path = backup_directory_path + "/" + file_id + "/" +  chunk_number;
        File file = new File(chunk_path);

        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fileIn = new FileInputStream(chunk_path)) {
            if(fileIn.read(data) < 0) {
                System.err.println("Couldn't read chunk data!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't get chunk number " + chunk_number + "of file "+ file_id);
            return null;
        }
        return data;
    }

    /**
     * Deletes a chunk
     * "Actually, deletion of the chunks of a file, may happen not only when the file is deleted on its file system,
     * but also when it is modified, because, for the backup system, it will be a different file."
     * @param file_id encoded
     * @param chunk_number number of the chunk
     * @return true if chunk was removed and false if it was
     */
    public static boolean removeChunk(String file_id, int chunk_number) {

        System.out.println("Deleting chunk "+ chunk_number + " with id:" + file_id);

        //check if the chunk exists
        if (retrieveChunk(file_id, chunk_number) == null) {
            System.out.println("A chunk with number " + chunk_number + " and file_id " + file_id + " doesn't exists.");
            return true;
        }

        //chunk will be in backup_directory/file_id/chunk_no
        String chunk_dir =  backup_directory_path + "/" + file_id + "/"+ chunk_number;

        File chunk_file = new File(chunk_dir);

        if( !chunk_file.exists() ){
            System.out.println("File doesn't exists in the correct path ");
            return false;
        }

        if (chunk_file.delete()) {
            remove_space_with_storage((int) chunk_file.length());
            return true;
        }

        return false;
    }

}
