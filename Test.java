package csc435.app;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test{
    public static long dataset_size = 0;
    public double execution_time = 0.0;
    final static List<String> terms = new ArrayList<>();

    public void clean_dataset(String input_dir) throws IOException {
        Path inputPath = Paths.get(input_dir);

         

        Files.walkFileTree(inputPath, new SimpleFileVisitor<Path>() {
            int i=0;
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String textFileContent = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);

                textFileContent = textFileContent.replaceAll("\r", "");
                textFileContent = textFileContent.replaceAll("[\t ]+", " ");
                textFileContent = textFileContent.replaceAll("\\s", " ");
                textFileContent = textFileContent.replaceAll("[^0-9a-zA-Z \n]", "");
                String[] words = textFileContent.split("\\s+");

                // Add each word to the terms list
                if (i<=1){
                    i++;
                for (String word : words) {
                    // Skip empty strings
                    if (!word.isEmpty()) {
                        terms.add(word);
                    }
                }
            }

                

                dataset_size += textFileContent.getBytes(StandardCharsets.UTF_8).length;

                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter their name
        System.out.print("Enter dir: ");

        // Read the input provided by the user as a String
        String name = scanner.nextLine();

        Test cleanDataset = new Test();

        cleanDataset.clean_dataset(name);
        int size = Math.min(terms.size(), 1000);

        // Print the first 10 terms
        for (int i = 0; i < size; i++) {
            System.out.println("Term " + (i + 1) + ": " + terms.get(i));
        }
        
    }
}
