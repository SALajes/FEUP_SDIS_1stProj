package project.store;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class that keeps record of the conversion of file_name to file_id
 */
public class FilesListing {

    private static FilesListing filesListing = new FilesListing();
    private static ConcurrentHashMap<String, String> files;

    //singleton
    private FilesListing() {
        get_files_disk_info();
    }

    /**
     *
     * @return an instance FilesListing
     */
    public static FilesListing get_files_Listing() {
        return filesListing;
    }

    /**
     *  checks the files ConcurrentHashMap for a file
     * @param file_name encoded name of the file
     * @return file_id if file name exists
     */
    public String get_file_id(String file_name) {
        return files.get(file_name);
    }

    public void add_file(String file_name, String file_id) {

        //put returns the previous value associated with key, or null if there was no mapping for key
        String previous_file_id = files.put(file_name, file_id);

        if (previous_file_id != null) {
            System.out.println("This file_name already exists, updating the content.");

            System.out.println("Deleting chunks from the out of date file");

            //TODO delete file from network storage
            //TODO deleting own stored chunks of this file
            // for all chunks
            // Store.removeChunk(file_id, chunk_number);
            //TODO unregister chunks of the file

        }
        set_files_disk_info();
    }

    public void delete_file_record(String file_name) {
        files.remove(file_name);
    }


    /**
     * changes the content of the file to contain this current object
     * @return true if successful and false otherwise
     */
    public boolean set_files_disk_info() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Store.getInstance().get_files_info_directory_path()));
            objectOutputStream.writeObject(files);
        } catch (Exception e) {
            e.getStackTrace();
            System.out.println("Couldn't get files info to the disk");
            return false;
        }
        return true;
    }

    /**
     *
     * @return true if successful and false otherwise
     */
    public static boolean get_files_disk_info() {

        //if file is empty there is nothing to have in the concurrentMap
        if (new File(Store.getInstance().get_files_info_directory_path()).length() == 0) {
            files = new ConcurrentHashMap<>();
            System.out.println("There isn't previous file info.");
            return true;
        }

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(Store.getInstance().get_files_info_directory_path()));
            files = (ConcurrentHashMap<String, String>) objectInputStream.readObject();
            System.out.println("Files Concurrent map is updated according to disk info");

        } catch (Exception ignored) {
            System.out.println("Couldn't put files info into the disk");
            return false;
        }
        return true;
    }
}
