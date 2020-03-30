import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class SpaceManagemnetIndexed{


    int DISK_SIZE = 20 ; //onstant by KB
    String Blocks = "00000000000000000000";
    int nOfFreeBlocks = 20;




    public IndexAllocator allocateSpace(int fileSize) {

        int counter = 0;
        IndexAllocator indexAllocator = new IndexAllocator();

        if (fileSize + 1 > nOfFreeBlocks) return indexAllocator;



        for (int i = 0; i < DISK_SIZE; i++){

            if (Blocks.charAt(i) == '0'){

                indexAllocator.index = i;
                Blocks = Blocks.substring(0, i) + "1" + Blocks.substring(i + 1);
                break;
            }
        }

        for (int i = 0; i < DISK_SIZE; i++){

            if (counter == fileSize) break;

            if (Blocks.charAt(i) == '0'){

                counter++;
                Blocks = Blocks.substring(0, i) + "1" + Blocks.substring(i + 1);
                indexAllocator.blocks.add(i);

            }
        }

        int numOfFree = 0;

        for (int i = 0; i < DISK_SIZE; i++){

            if (Blocks.charAt(i) == '0') numOfFree++;
        }
        this.nOfFreeBlocks = numOfFree;

        return indexAllocator;
    }

    void deAllocateSpace(ArrayList<Integer> blocks, int index, int filesize){

        int counter = 0;

        Blocks = Blocks.substring(0,index) + "0" + Blocks.substring(index + 1);

        for (int  i = 0; i < DISK_SIZE; i++) {

            if (counter == filesize) break;

            if (i == blocks.get(counter)) {
                Blocks = Blocks.substring(0, i) + "0" + Blocks.substring(i + 1);
                counter++;
            }
        }

        int numOfFree = 0;

        for (int i = 0; i < DISK_SIZE; i++){

            if (Blocks.charAt(i) == '0') numOfFree++;
        }
        this.nOfFreeBlocks = numOfFree;
    }



}



public class Indexed {

    int DISK_SIZE = 20 ; //onstant by KB
    String Blocks = "00000000000000000000";
    int nOfFreeBlocks = 20;
    SpaceManagemnetIndexed sm = new SpaceManagemnetIndexed();

    Directory root = new Directory("root/", "root");

    String[] fileName(String path){

        String[] names = path.split("/");

        return names;
    }

    Directory checkPathValid(Directory start, String[] paths, int i, int size){

        if (paths.length == 2) return start;


        for (Directory dir: start.subDirectories) {

            if (paths[i].equals(dir.name)){

                if (i == size && paths[i].equals(dir.name)) return dir;

                checkPathValid(dir, paths, ++i, size);
            }
        }
        return null;
    }

    void createFolder(String path){

        String[] names = path.split("/");

        // get the folder name
        String dirName = names[names.length - 1];

        // check if path exist
        Directory dir = checkPathValid(root, names, 1, names.length - 2);

        if (dir == null){

            System.out.println("There is no such Directory");
            return;
        }


        Directory newDir = new Directory(path, dirName);

        // allocate the space
        dir.subDirectories.add(newDir);
    }

    void deleteFolder(String path){

        String[] names = path.split("/");

        // get the folder name
        String dirName = names[names.length - 1];

        // check if path exist
        Directory dir = checkPathValid(root, names, 1, names.length - 2);

        if (dir == null){

            System.out.println("There is no such Directory");
            return;
        }


        // Delete the Folder

        Directory dirToDelete = new Directory();
        boolean flag = false;
        for (Directory directory : dir.subDirectories){


            System.out.println(directory.name);
            if (directory.name.equals(dirName) ){

                dirToDelete = directory;
                flag = true;
                break;
            }
        }
        if (flag) dir.subDirectories.remove(dirToDelete);
        else System.out.println("There is no such file");
    }

    void createFile(String path, int size){

        String[] names = fileName(path);

        // get the file name
        String fileName = names[names.length - 1];

        // check if path exist
        Directory dir = checkPathValid(root, names, 1, names.length - 2);

        if (dir == null){

            System.out.println("There is no such Directory");
            return;
        }


        // check if the size of file can be satisfied
        IndexAllocator indexAllocator = sm.allocateSpace(size);
        if (indexAllocator.index == -1){

            System.out.println("There is no space for the file");
            return;
        }
        System.out.println(indexAllocator.index);
        // create the file
        FileClass newFile = new FileClass(path, fileName, size, indexAllocator.index, indexAllocator.blocks);

        // allocate the space
        dir.files.add(newFile);
    }

    void deleteFile(String path){

        String[] names = fileName(path);


        // get the file name
        String fileName = names[names.length - 1];

        // check if path exist
        Directory dir = checkPathValid(root, names, 1, names.length - 2);

        if (dir == null){

            System.out.println("There is no such Directory");
            return;
        }


        // Delete the file

        FileClass fileToDelete = new FileClass();
        boolean flag = false;
        for (FileClass file : dir.files){


            System.out.println(file.name);
            if (file.name.equals(fileName) ){

                fileToDelete = file;

                int index = file.allocatedBlocks;
                int size = file.size;

                for (Integer i : file.blocks){

                    System.out.println(i);
                }

                sm.deAllocateSpace(file.blocks, index, size);
                flag = true;
                break;
            }
        }
        if (flag) dir.files.remove(fileToDelete);
        else System.out.println("There is no such file");
    }

    void DisplayDiskStructure(Directory root, int n){

        for (int i = 0; i < n; i++) System.out.print(' ');
        System.out.println("<" + root.name + ">\n");

        for (FileClass file : root.files) {

            for (int i = 0; i < n + 1; i++) System.out.print(' ');
            System.out.println(file.name + "\n");
        }

        for (Directory dir:root.subDirectories){


            DisplayDiskStructure(dir, n + 1);
        }
    }

    void saveToFile() throws IOException {

        File file = new File("structure1.txt");
        FileWriter myWriter = new FileWriter(file);

        myWriter.write("Indexed-");
        myWriter.write(sm.Blocks + '-');
        myWriter.write(String.valueOf(sm.DISK_SIZE) + '-');
        myWriter.write(String.valueOf(sm.nOfFreeBlocks) + '-');
        saveRec(root, myWriter);
        myWriter.close();

    }

    void saveRec(Directory root, FileWriter file) throws IOException {

        file.write("D^" + root.directoryPath + '-');

        for (FileClass fil : root.files) {

            file.write("F^" + fil.filePath + "^" + fil.name + "^"
                    + fil.size + "^" + fil.allocatedBlocks + "^");

            for (int i = 0; i < fil.blocks.size(); i++){


                String val = String.valueOf(fil.blocks.get(i));
                //+ fil.blocks + "^-"
                if (i == fil.blocks.size() - 1) file.write(val + "^-");

                else file.write(val + "^");

            }
        }

        for (Directory dir : root.subDirectories){

            saveRec(dir, file);
        }
    }

    void laodFromFile() throws FileNotFoundException {

        File file = new File("structure1.txt");
        Scanner myReader = new Scanner(file);


        while (myReader.hasNextLine()){

            String line = myReader.nextLine();
            System.out.println(line);

            String[] data = line.split("\\-");

            sm.Blocks = data[1];
            sm.DISK_SIZE = Integer.parseInt(data[2]);
            sm.nOfFreeBlocks = Integer.parseInt(data[3]);

            for (int i = 4; i < data.length; i++){

                String[] cur = data[i].split("\\^");


                System.out.println(cur[1]);

                if (cur[0].equals("D")){

                    if (cur[1].equals("root/")) continue;

                    createFolder(cur[1]);
                }
                else if (cur[0].equals("F")){

                    String path = cur[1];
                    String name = cur[2];
                    int size = Integer.parseInt(cur[3]);

                    int index = Integer.parseInt(cur[4]);

                    ArrayList<Integer> blocks = new ArrayList<>();

                    for (int j = 4; j < cur.length; j++){

                        blocks.add(Integer.parseInt(cur[j]));
                    }

                    FileClass newFile = new FileClass(path, name, size, index);

                    importFile(newFile, path);
                }
            }
        }
    }

    void importFile(FileClass file, String path){

        String[] names = fileName(path);



        // check if path exist
        Directory dir = checkPathValid(root, names, 1, names.length - 2);

        if (dir == null){

            System.out.println("There is no such Directory");
            return;
        }


        // allocate the space
        dir.files.add(file);
    }

    public static void main(String[] args) throws IOException {

        Indexed con = new Indexed();

        con.laodFromFile();

        con.DisplayDiskStructure(con.root, 1);

        /*con.createFile("root/file1.txt", 2);
        con.createFile("root/file2.txt", 2);
        con.createFile("root/file3.txt", 2);
        con.createFile("root/file4.txt", 2);
        con.createFile("root/file5.txt", 3);
        con.createFolder("root/ahmed");
        con.createFolder("root/folder6");
        con.createFile("root/ahmed/file4.txt", 2);
        con.createFile("root/ahmed/file5.txt", 2);

        con.DisplayDiskStructure(con.root, 1);

        for (int i = 0; i < con.sm.Blocks.length(); i++){

            System.out.print(" " + con.sm.Blocks.charAt(i));
        }

        System.out.println("\n");

        con.deleteFile("root/file3.txt");
        con.deleteFolder("root/ahmed");

        con.DisplayDiskStructure(con.root, 1);

        for (int i = 0; i < con.sm.Blocks.length(); i++){

            System.out.print(" " + con.sm.Blocks.charAt(i));
        }

        con.saveToFile();*/


        //System.out.println("\n");
    }
}
