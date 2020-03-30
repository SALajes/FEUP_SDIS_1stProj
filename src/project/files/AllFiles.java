package project.files;

import java.util.Hashtable;

public class AllFiles {

    private static AllFiles allFiles = new AllFiles();
    private static Hashtable<String, String> all_files = new Hashtable();

    AllFiles() { }

    public static AllFiles getAllFiles(){
        return allFiles;
    }

    public String getFileId(String file_name) {
        return all_files.get(file_name);
    }

    public void addFile(String file_name, String file_id) {
        if(getAllFiles() != null ) {
            System.out.println("Other version detected, storing new by replacing");
        }
        all_files.put(file_name, file_id);
    }

    public void removeFile(String file_name) {
        all_files.remove(file_name);
    }

}
