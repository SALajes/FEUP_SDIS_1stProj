package project.chunk;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ChunkFactory {
    private final File file;
    private final int replication_degree;
    private ArrayList<Chunk> chunks;

    public ChunkFactory(File file, int replication_degree) {
        this.file = file;
        this.replication_degree = replication_degree;

        chunks = new ArrayList<>();

        produce_chunks();
    }

    private void produce_chunks() {
        int chunk_no = 0;

        byte[] buffer = new byte[Chunk.chunk_size];

        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(this.file))) {
            int size;
            while((size = stream.read(buffer)) > 0){
                Chunk chunk = new Chunk(chunk_no, Arrays.copyOf(buffer, size), size);
                this.chunks.add(chunk);

                chunk_no++;

                buffer = new byte[Chunk.chunk_size];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Chunk> get_chunks(){
        return chunks;
    }
}
