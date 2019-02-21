import sun.plugin2.message.Message;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {
    // Main.java [dictionary] [input file]
    // Main.java [dictionary]
    // Main.java
    public static void main(String[] args) throws IOException{
        Scanner s = new Scanner(System.in);
        String dictPath = "";
        String targetPath = "";
        switch(args.length){
            // No paths given, ask for dictionary path
            case 0:
                System.out.println("What is the path to your dictionary? > ");
                dictPath = s.nextLine();

            // Dictionary path given in args[0], prompt for target path
            case 1:
                System.out.println("What is the path to the target file? (hit Enter for CLI) > ");
                targetPath = s.nextLine();
                if(args.length != 0){ dictPath = args[0];}
                break;

            // Both paths given
            case 2:
                break;

            // Too many arguments
            default:
                System.out.println("Incorrect number of arguments");
                break;
        }
        DictionaryAttack da = new DictionaryAttack(dictPath);
        if(!targetPath.isEmpty()){
            da.attackFile(targetPath);
        }
        else{
            da.attackCLI();
        }
    }
}

class DictionaryAttack {
    String dictPath;
    String[] dictionary;
    String targetPath;
    public DictionaryAttack(String dictPath){
        //Read in dictionary file and target file
        this.dictPath = dictPath;
    }

    // Dictionary attack on a file, line by line
    public void attackFile(String targetPath) throws IOException {
        // For each line (hash) in target file
        BufferedReader br = new BufferedReader(new FileReader(targetPath));
        String line;
        line = br.readLine();
        while(line != null){
            processHash(line);
            line = br.readLine();
        }
    }
    // Prompt for line by line dictionary attack
    public void attackCLI(){
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the hash you'd like to crack (type e for exit) > ");
        String hash = in.nextLine();
        while(hash != "e"){
            System.out.println("Enter the hash you'd like to crack (type e for exit) > ");
            try {
                processHash(hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
            hash = in.nextLine();
        }
    }
    // Decodes a given hash and returns the value
    public String processHash(String hash) throws IOException {
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e){
            System.out.println("No such algorithm");
        }
        String guessHash;
        System.out.println("Cracking hash " + hash + "...");
        BufferedReader br = new BufferedReader(new FileReader(dictPath));
        String dictLine = br.readLine();
        while(dictLine != null){
            md.update(dictLine.getBytes());
            byte[] digest = md.digest();
            guessHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            //System.out.println("Guess Hash: " + guessHash);
            if(guessHash == hash){
                System.out.println("Password found: " + dictLine);
                return dictLine;
            }
            else{
                dictLine = br.readLine();
            }
        }
        System.out.println("--------------------------------------------");
        return null;
    }

    // Sets dictionary
    public void setDict(String dictPath){
        this.dictPath = dictPath;
    }
}
