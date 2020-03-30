package project.chunk;

public class Chunk {
    final int chunk_no;
    final String file_id;
    final byte[] content;
    final int size;

    public Chunk(int chunk_no, String file_id, byte[] content, int size) {
        this.chunk_no = chunk_no;
        this.file_id = file_id;
        this.content = content;
        this.size = size;
    }
}
