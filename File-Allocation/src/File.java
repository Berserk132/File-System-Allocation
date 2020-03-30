import java.util.ArrayList;

class FileClass {


    protected String filePath;
    protected String name;
    protected int size;
    protected int allocatedBlocks;
    protected ArrayList<Integer> blocks = new ArrayList<>();
    protected boolean deleted = false;

    FileClass(String filePath, String name, int size,int index) {

        this.filePath = filePath;
        this.name = name;
        this.size = size;
        this.allocatedBlocks = index;
        this.blocks = new ArrayList<>();
    }

    FileClass(String filePath, String name, int size,int index, ArrayList<Integer> blocks) {

        this.filePath = filePath;
        this.name = name;
        this.size = size;
        this.allocatedBlocks = index;
        this.blocks = blocks;
    }

    FileClass() {

    }

}
