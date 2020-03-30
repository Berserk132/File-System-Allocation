import java.util.ArrayList;

public class Directory {


    protected String directoryPath;
    protected String name;
    protected ArrayList<FileClass> files = new ArrayList<>();
    protected ArrayList<Directory> subDirectories;
    protected boolean deleted = false;

    Directory(String directoryPath, String name) {

        this.directoryPath = directoryPath;
        this.name = name;
        this.subDirectories = new ArrayList<>();
    }

    Directory(){


    }

}
