package project.files;

import java.util.HashMap;

public class BackupFiles {

    private static HashMap<String, String > backup_files = new HashMap();

    /**
     *
     * @param file_path
     * @param file_id
     */
    public static void add_file(String file_path, String file_id) {
        backup_files.put(file_path, file_id);
    }

    /**
     *
     * @param file_path
     * @return
     */
    public static boolean check_if_file_exists(String file_path) {
        return backup_files.containsKey(file_path);
    }

}
