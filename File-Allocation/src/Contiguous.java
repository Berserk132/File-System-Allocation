import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class SpaceManagemnetContiguous{


    int DISK_SIZE = 20 ; //onstant by KB
    String Blocks = "00000000000000000000";
    int nOfFreeBlocks = 20;




    int allocateSpace(int fileSize){

        int index = -1;
        for (int i = 0; i < Blocks.length(); i++){


            String c = String.valueOf(Blocks.charAt(i));

            if (c.equals("0") && Blocks.length() > i + fileSize){

                String block = Blocks.substring(i, i + fileSize);

                boolean result = block.contains("1");
                if (result){

                    i += fileSize + 1;
                    continue;
                }
                else {

                    index = i;
                    break;
                }
            }

        }

        if (index != -1) {

            String tmp = new String();
            for (int  k = 0; k < DISK_SIZE; k++) {

                if (k == index) {

                    for (int i = index; i < index + fileSize; i++) {

                        tmp += "1";
                    }
                    k += fileSize;
                }

                tmp += String.valueOf(Blocks.charAt(k));


            }

            Blocks = tmp;
        }

        int numOfFree = 0;

        for (int i = 0; i < DISK_SIZE; i++){

            if (Blocks.charAt(i) == '0') numOfFree++;
        }
        this.nOfFreeBlocks = numOfFree;
        return index;
    }

    void deAllocateSpace(int index, int size){

        String tmp = new String();
        for (int  k = 0; k < DISK_SIZE; k++) {

            if (k == index) {

                for (int i = index; i < index + size; i++) {

                    tmp += "0";
                }
                k += size;
            }

            tmp += String.valueOf(Blocks.charAt(k));


        }

        Blocks = tmp;

        int numOfFree = 0;

        for (int i = 0; i < DISK_SIZE; i++){

            if (Blocks.charAt(i) == '0') numOfFree++;
        }
        this.nOfFreeBlocks = numOfFree;
    }



}


public class Contiguous {

    SpaceManagemnetContiguous sm = new SpaceManagemnetContiguous();
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

                return  checkPathValid(dir, paths, ++i, size);
            }
        }
        return null;
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
        int index = sm.allocateSpace(size);
        if (index == -1){

            System.out.println("There is no space for the file");
            return;
        }
        System.out.println(index);
        // create the file
        FileClass newFile = new FileClass(path, fileName, size, index);

        // allocate the space
        dir.files.add(newFile);
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

                sm.deAllocateSpace(index, size);
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

        File file = new File("structure.txt");
        FileWriter myWriter = new FileWriter(file);

        myWriter.write("Contiguous-");
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
            + fil.size + "^" + fil.allocatedBlocks + "^-");
        }

        for (Directory dir : root.subDirectories){

            saveRec(dir, file);
        }
    }

    void laodFromFile() throws FileNotFoundException {

        File file = new File("structure.txt");
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

                    FileClass newFile = new FileClass(path, name, size, index);

                    importFile(newFile, path);
                }
            }
        }
        myReader.close();
    }

    void DisplayDiskStatus(Directory root, int n){

        for (int i = 0; i < n; i++) System.out.print(' ');
        System.out.println("<" + root.name + ">\n");

        for (FileClass file : root.files) {

            for (int i = 0; i < n + 1; i++) System.out.print(' ');
            System.out.println(file.name + " " + file.allocatedBlocks + " " + file.size + "\n");
        }

        for (Directory dir:root.subDirectories){


            DisplayDiskStructure(dir, n + 1);
        }
    }

    void DisplayDiskStatusSpace(){

        System.out.println("Disk Size is : " + sm.DISK_SIZE);
        System.out.println("number of Free Blocks : " + sm.nOfFreeBlocks);
        System.out.println("Blocks : " + sm.Blocks);
        System.out.println("Number of Allocated Blocks : " + (sm.DISK_SIZE - sm.nOfFreeBlocks));
    }


    public static void main(String[] args) throws IOException {

        Contiguous con = new Contiguous();
        Scanner sc = new Scanner(System.in);

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
        con.deleteFile("root/ahmed/file5.txt");
        con.deleteFolder("root/ahmed");

        con.DisplayDiskStructure(con.root, 1);

        for (int i = 0; i < con.sm.Blocks.length(); i++){

            System.out.print(" " + con.sm.Blocks.charAt(i));
        }

        con.saveToFile();*/


        //System.out.println("\n");

        while (true){

            System.out.println("1-Create File\n" +
                    "2-Create Folder\n" +
                    "3-Delete File\n" +
                    "4-Delete Folder\n" +
                    "5-Display Disk Status\n" +
                    "6-Display Disk Structure\n" +
                    "7-Exit\n");

            System.out.println("Please Enter your choice : ");
            String tmp = sc.nextLine();
            int choice = Integer.parseInt(tmp);
            System.out.println("Please Enter The Command : ");




            if (choice == 1){

                String command = sc.nextLine();
                String[] arg = command.split(" ");
                if (arg.length < 2) System.out.println("Error in the command");
                else con.createFile(arg[0],Integer.parseInt(arg[1]));
            }
            else if (choice == 2){

                String command = sc.nextLine();
                String[] arg = command.split(" ");
                if (arg.length < 1) System.out.println("Error in the command");
                else con.createFolder(arg[0]);
            }
            else if (choice == 3){

                String command = sc.nextLine();
                String[] arg = command.split(" ");
                if (arg.length < 1) System.out.println("Error in the command");
                else con.deleteFile(arg[0]);
            }
            else if (choice == 4){

                String command = sc.nextLine();
                String[] arg = command.split(" ");
                if (arg.length < 1) System.out.println("Error in the command");
                else con.deleteFolder(arg[0]);
            }
            else if (choice == 5){

                con.DisplayDiskStatus(con.root, 1);
                con.DisplayDiskStatusSpace();
            }
            else if (choice == 6){

                con.DisplayDiskStructure(con.root,1);
            }
            else break;
        }

        sc.close();
        con.saveToFile();
    }
}
