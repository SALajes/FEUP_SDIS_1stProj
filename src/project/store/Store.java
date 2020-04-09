package project.store;

import project.Macros;
import project.peer.Peer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    private static Store store = new Store();

    //state of others chunks
    private ConcurrentHashMap<String, Pair<Integer,ArrayList<Integer>>> stored_chunks = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Pair<Integer,ArrayList<Integer>>> stored_chunks_occurrences = new ConcurrentHashMap<>();

    //state of restored files - key file_id - value file_name
    private Hashtable<String, String> restored_files = new Hashtable<>();

    //state of our files - key file_id + chunk and value wanted_replication degree and list of peers
    private ConcurrentHashMap<String, Pair<Integer,ArrayList<Integer>>> backup_chunks_occurrences = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean>  getchunk_reply = new ConcurrentHashMap<>();

    private static String peer_directory_path;
    private static String files_directory_path;
    private static String files_info_directory_path;
    private static String store_directory_path;
    private static String store_info_directory_path;
    private static String restored_directory_path;

    private long occupied_storage = 0; //in bytes
    private long storage_capacity = Macros.INITIAL_STORAGE; //Initial there isn't restrictions of space


    /**
     * creates the four needed directory
     */
    private Store() {
        initializeStore();
    }

    /**
     * creates the four needed directory
     */
    public void initializeStore(){
        //setting the directory name
        peer_directory_path = Peer.id + "_directory/";
        files_directory_path = peer_directory_path + "files/";
        files_info_directory_path = peer_directory_path + "files.txt";
        store_directory_path = peer_directory_path + "stored/";
        store_info_directory_path = peer_directory_path + "stored.txt";
        restored_directory_path = peer_directory_path + "restored/";

        FileManager.create_directory(peer_directory_path);
        FileManager.create_directory(files_directory_path);
        FileManager.create_empty_file(files_info_directory_path);
        FileManager.create_directory(store_directory_path);
        //if exists return true but doesn't creates a new file
        FileManager.create_empty_file(store_info_directory_path);
        FileManager.create_directory(restored_directory_path);
    }

    public static Store getInstance(){
        return store;
    }



    // ---------------------------------------------- RECLAIM -----------------------------------
    public void setStorageCapacity(Integer new_capacity) {
        this.storage_capacity = new_capacity;

        Set<String> keys = stored_chunks.keySet();

        //Obtaining iterator over set entries
        Iterator<String> itr = keys.iterator();
        String file_id;

        //deletes necessary chunk to have that space
        while((new_capacity < occupied_storage) && itr.hasNext()) {
            // Getting Key
            file_id = itr.next();

            ArrayList<Integer> chunks_nos = new ArrayList<>(stored_chunks.get(file_id).second);

            for(Integer chunk_number : chunks_nos) {
                FileManager.removeChunk(file_id, chunk_number);
                if(new_capacity >= occupied_storage){
                    return;
                }
            }
        }
    }

    //-------------------- STORAGE ------------------
    public long getStorageCapacity() {
        return storage_capacity;
    }

    public long getOccupiedStorage() {
        return occupied_storage;
    }

    public boolean hasSpace(Integer space_wanted) {
        return (this.storage_capacity >= this.occupied_storage + space_wanted);
    }
    /**
     * used when a new chunk is store ( by backup )
     * @param space_wanted storage space added
     */
    public void AddOccupiedStorage(int space_wanted) {
        this.occupied_storage += space_wanted;
    }

    /**
     * used when a chunk is deleted
     * @param occupied_space the amount of space in bytes used for storage
     */
    public void RemoveOccupiedStorage(int occupied_space) {
        this.occupied_storage -= occupied_space;
    }


    // -----------  stored chunks

    public ConcurrentHashMap<String, Pair<Integer, ArrayList<Integer>>> get_stored_chunks() {
        return stored_chunks;
    }

    public synchronized void add_stored_chunk(String file_id, int chunk_number, Integer replicationDegree) {

        if(!stored_chunks.containsKey(file_id)) {

            ArrayList<Integer> chunks_stored = new ArrayList<>();
            chunks_stored.add(chunk_number);
            Pair pair = new Pair<>(replicationDegree, chunks_stored);
            stored_chunks.put(file_id, pair);

            add_stored_chunks_occurrences(file_id, chunk_number, replicationDegree);

        } else if( !checkStoredChunk(file_id, chunk_number)) {

            Pair<Integer, ArrayList<Integer>> pair = stored_chunks.get(file_id);
            pair.second.add(chunk_number);
            stored_chunks.replace(file_id, pair);

            add_stored_chunks_occurrences(file_id, chunk_number, replicationDegree);
        }
    }

    public boolean checkStoredChunk(String file_id, int chunk_no){
        if(stored_chunks.containsKey(file_id)) {
            return stored_chunks.get(file_id).second.contains(chunk_no);
        }
        else return false;
    }

    void remove_stored_chunk(String file_id, Integer chunk_number) {

        if(stored_chunks.containsKey(file_id)) {
            Pair<Integer, ArrayList<Integer>> pair = stored_chunks.get(file_id);

            stored_chunks_occurrences.remove(file_id + "_" + chunk_number);
            if(stored_chunks.get(file_id).second.size() == 1) {
                System.out.println("No more chunks of that file, removing folder of file " + file_id);
                stored_chunks.remove(file_id);
                FileManager.delete_file_folder( this.get_store_directory_path() + file_id);
            } else {
                pair.second.remove(chunk_number);
                stored_chunks.replace(file_id, pair);
            }
        }
    }

    public void remove_stored_chunks(String file_id ){
        stored_chunks.remove(file_id);
    }

    //-------------------- Stored Chunks Occurrences ------------------
    private void add_stored_chunks_occurrences(String file_id, int chunk_number, Integer replicationDegree) {
        ArrayList<Integer> occurrences = new ArrayList<>();
        occurrences.add(Peer.id);
        Pair pair1 = new Pair<>(replicationDegree, occurrences);
        stored_chunks_occurrences.put(file_id + "_" + chunk_number, pair1 );
        System.out.println(Peer.id + " add stored chunk " + chunk_number + " of file " + file_id);
    }

    public boolean add_replication_degree(String file_id, Integer chunk_number, Integer peer_id) {

        String chunk_id = file_id + "_" + chunk_number;
        //Peer doesn't have that chunk stored
        if(!stored_chunks_occurrences.containsKey(chunk_id)) {
            return false;
        }

        //already add that peer as a owner of a stored chunk
        if(stored_chunks_occurrences.get(chunk_id).second.contains(peer_id))
            return true;

        stored_chunks_occurrences.get(chunk_id).second.add(peer_id);
        return true;
    }

    public boolean has_replication_degree(String chunk_id) {
       return (check_stored_chunks_occurrences(chunk_id) >= this.stored_chunks_occurrences.get(chunk_id).first);
    }

    public Integer get_replication_degree(String chunk_id) {
        if(!this.stored_chunks_occurrences.containsKey(chunk_id))
            return -1;
        return this.stored_chunks_occurrences.get(chunk_id).first;
    }

    public Integer check_stored_chunks_occurrences(String chunk_id) {
        if(this.stored_chunks_occurrences.get(chunk_id) != null)
            return this.stored_chunks_occurrences.get(chunk_id).second.size();

        return -1;
    }

    public void remove_stored_chunk_occurrence(String chunk_id, Integer peer_id) {
        Pair<Integer,ArrayList<Integer>> value = this.stored_chunks_occurrences.get(chunk_id);

        if(value != null ){
            ArrayList<Integer> peersList = value.second;
            peersList.remove(peer_id);
            Pair<Integer, ArrayList<Integer>> pair = new Pair<>(value.first, peersList);
            this.stored_chunks_occurrences.replace(chunk_id, pair);
        }

    }

    public void remove_stored_chunks_occurrences(String chunk_id) {
        this.stored_chunks_occurrences.remove(chunk_id);
    }

    //---------------------------- BACKUP CHUNKS ----------------------------------

    public void new_Backup_chunk(String chunk_id, int replication_degree) {
        if(this.backup_chunks_occurrences.containsKey(chunk_id)){
            Pair<Integer, ArrayList<Integer>> pair = this.backup_chunks_occurrences.get(chunk_id);

            pair.first = replication_degree;

            this.backup_chunks_occurrences.replace(chunk_id, pair);
        }
        else this.backup_chunks_occurrences.put(chunk_id, new Pair<>(replication_degree, new ArrayList<>()));
    }

    public void add_Backup_chunks_occurrences(String chunk_id, int peer_id) {
        if(this.backup_chunks_occurrences.containsKey(chunk_id)){
            Pair<Integer, ArrayList<Integer>> pair = this.backup_chunks_occurrences.get(chunk_id);

            if(pair.second.contains(peer_id)){
                return;
            }

            pair.second.add(peer_id);
            this.backup_chunks_occurrences.replace(chunk_id, pair);
        }
    }

    public int check_backup_chunks_occurrences(String chunk_id) {
        if(this.backup_chunks_occurrences.get(chunk_id) != null)
            return this.backup_chunks_occurrences.get(chunk_id).second.size();
        return -1;
    }

    public int get_backup_chunk_replication_degree(String file_id) {
        return backup_chunks_occurrences.get(file_id).first;
    }

    public void remove_Backup_chunk_occurrence(String chunk_id, Integer peer_id) {
        Pair<Integer,ArrayList<Integer>> value = this.backup_chunks_occurrences.get(chunk_id);

        if(value != null ){
            ArrayList<Integer> peersList = value.second;
            peersList.remove(peer_id);
            Pair<Integer, ArrayList<Integer>> pair = new Pair<>(value.first, peersList);
            this.backup_chunks_occurrences.replace(chunk_id, pair);
        }

    }

    public void remove_Backup_chunks_occurrences(String chunk_id) {
        this.backup_chunks_occurrences.remove(chunk_id);
    }

    // --------------------------- GETCHUNK -----------------------------------

    public void add_getchunk_reply(String chunk_id){
        this.getchunk_reply.put(chunk_id, false);
    }

    public void check_getchunk_reply(String chunk_id){
        if(this.getchunk_reply.containsKey(chunk_id))
            this.getchunk_reply.replace(chunk_id, true);
    }

    public void remove_getchunk_reply(String chunk_id){
        this.getchunk_reply.remove(chunk_id);
    }

    public boolean get_getchunk_reply(String chunk_id){
        return this.getchunk_reply.get(chunk_id);
    }

    public void add_restored_file(String file_id, String file_name){
        restored_files.put(file_id, file_name);
    }

    // ----------------------------------- GET PATHS ------------------------------------------------------

    public String get_files_info_directory_path() {
        return files_info_directory_path;
    }

    public String get_restored_directory_path() {
        return restored_directory_path;
    }

    public String get_store_directory_path() {
        return store_directory_path;
    }

    public String get_files_directory_path() {
        return files_directory_path;
    }
}


