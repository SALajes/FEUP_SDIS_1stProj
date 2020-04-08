package project.store;

import project.Macros;
import project.chunk.Chunk;
import project.peer.Peer;
import project.protocols.ReclaimProtocol;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileManager {

    /**
     * Creates an empty file to start the restoring procedure or the backup
     * @return true if successful, and false otherwise
     */
    public static boolean create_empty_file_for_restoring(String file_name) {
        String restore_file_path = Store.getInstance().get_restored_directory_path() + file_name;
        return create_empty_file(restore_file_path);
    }

    /**
     * Creates an empty file to start the restoring procedure or the backup/info file
     * if exists return true but doesn't creates a new file
     * @param file_path path of the file that is being created
     */
    public static boolean create_empty_file(String file_path) {

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
     * Idempotent Method that creates a directory given the path
     * @return success if directory was or is created and false if not
     */
    public static boolean create_directory(String directory_path) {

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
     * This functions append the body of a chunk (file data) in the position desired ( calculated with chunk number)
     * Used for restoring a file with a certain given filename
     * @param file_name name of the file
     * @param chunk_data the array with the bytes to put
     * @param chunk_number number of the chunk
     * @return true if success and false otherwise
     */
    public static synchronized boolean write_chunk_to_restored_file(String file_name, byte[] chunk_data, int chunk_number) {

        String file_path = Store.getInstance().get_restored_directory_path() + "/" + file_name;

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


    public static void delete_files_folders(String file_id) {
        delete_file_folder( Store.getInstance().get_store_directory_path() + file_id );
        Store.getInstance().remove_stored_chunks(file_id);

        String file_name = FilesListing.get_files_Listing().get_file_name(file_id);
        if( file_name != null) {
            delete_file_folder( Store.getInstance().get_restored_directory_path() + file_id );
            //You should not delete the original file, when you execute the Delete protocol
            //So the folder files isn't deleted
        }

    }

    /**
     * deletes folder with chunks of a file passed in the first argument
     * @param file_path directory file
     * @return true if successful, and false other wise
     */
    public static boolean delete_file_folder(String file_path) {

        File file_directory = new File(file_path);

        if(file_directory == null){
            return false;
        }

        if (file_directory.isFile()) {
            return file_directory.delete();
        }

        if (!file_directory.isDirectory()) {
            return false;
        }

        File[] folder_files = file_directory.listFiles();
        if (folder_files != null && folder_files.length > 0) {
            for (File f : folder_files) {
                if (!f.delete()) {
                    return false;
                }
            }
        }

        return file_directory.delete();
    }

    /**
     * get chunk from stored directory
     * @param file_id encoded
     * @param chunk_no number of the chunk we want to retrieve
     * @return wanted chunk data
     */
    public static Chunk retrieveChunk(String file_id, int chunk_no){
        if(Store.getInstance().checkStoredChunk(file_id, chunk_no)) {
            Chunk chunk;
            //get the chunk information from the chunks saved file
            final String chunk_path = Store.getInstance().get_store_directory_path() + "/" + file_id + "/" + chunk_no;
            File file = new File(chunk_path);
            int chunk_size = (int) file.length();
            byte[] chunk_data = new byte[chunk_size];
            try (FileInputStream fileInputStream = new FileInputStream(chunk_path)) {
                if(fileInputStream.read(chunk_data) < 0)
                    return null;
                chunk = new Chunk(chunk_no, chunk_data, chunk_size);
                return chunk;
            } catch (IOException e) {
                System.err.println("Couldn't get chunk "+ chunk_no + " of file " + file_id);
                e.printStackTrace();
                return null;
            }

        }

        // Does not have the chunk
        return null;

    }

    /**
     * Deletes a chunk
     * @param file_id encoded
     * @param chunk_number number of the chunk
     * @return true if chunk was removed and false if it was
     */
    public static boolean removeChunk(String file_id, int chunk_number) {

        System.out.println("Deleting chunk "+ chunk_number + " with id:" + file_id);

        //check if the chunk exists
        if (!Store.getInstance().checkStoredChunk(file_id, chunk_number)) {
            System.out.println("A chunk with number " + chunk_number + " and file_id " + file_id + " doesn't exists.");
            return true;
        }

        //chunk will be in backup_directory/file_id/chunk_no
        String chunk_dir = Store.getInstance().get_store_directory_path() + file_id + "/"+ chunk_number;

        File chunk_file = new File(chunk_dir);

        if( !chunk_file.exists() ){
            System.out.println("File doesn't exists in the correct path ");
            return false;
        }

        if (chunk_file.delete()) {

            Store.getInstance().remove_space_with_storage((int) chunk_file.length());
            //removes from stored chunks Hashtable
            Store.getInstance().remove_stored_chunk(file_id, chunk_number);

            ReclaimProtocol.send_removed(Peer.version, Peer.id, file_id, chunk_number);
            return true;
        }

        return false;
    }


    /**
     * Given the chunk_body ( the data ), stores it in a file
     * @param file_id encoded
     * @param chunk_number number of the chunk
     * @param chunk_body data
     * @param replicationDegree wanted replication degree
     * @return true if successful or false otherwise
     */
    public static boolean storeChunk(String file_id, int chunk_number, byte[] chunk_body, Integer replicationDegree) {

        //check if the chunk already exists
        if (Store.getInstance().checkStoredChunk(file_id, chunk_number)) {
            return true;
        }

        //check if there is enough storage
        if (!Store.getInstance().hasSpace(chunk_body.length)) {
            System.out.println("A chunk with number " + chunk_number + " and file_id " + file_id + " can't be store because there isn't space left.");
            return false;
        }

        Store.getInstance().add_stored_chunk(file_id, chunk_number, replicationDegree);

        String chunk_directory =  Store.getInstance().get_store_directory_path() + file_id + "/";

        // Idempotent Method
        FileManager.create_directory(chunk_directory);
        String chunk_path = chunk_directory + chunk_number;

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
        Store.getInstance().AddToSpace_with_storage(chunk_body.length);
        return true;
    }

}
