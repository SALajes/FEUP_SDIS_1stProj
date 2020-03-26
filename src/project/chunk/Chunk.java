package project.chunk;

public class Chunk {
    public final static int chunk_size = 64000;

    final int chunk_no;
    final byte[] content;
    final int size;

    public Chunk(int chunk_no, byte[] content, int size) {
        this.chunk_no = chunk_no;
        this.content = content;
        this.size = size;
    }
}
