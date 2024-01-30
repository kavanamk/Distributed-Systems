import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;

public class CleanDataset {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.exit(1);
        }

        String inputDir = args[0];
        String outputDir = args[1];
         long start = System.currentTimeMillis(); 
        
        try {
            cleanTextFiles(inputDir, outputDir);
            System.out.println("Cleaning is successfully completed.");
        } catch (IOException e) {
            System.err.println("Error during cleaning: " + e.getMessage());
        }

        long end = System.currentTimeMillis(); 
        long totalExecutionTime = end - start;
        System.out.println("Total execution time: " + totalExecutionTime + " ms");
        
    }

    private static void cleanTextFiles(String inputDir, String outputDir) throws IOException {
        Files.walk(Paths.get(inputDir))
                .filter(Files::isRegularFile)
                .forEach(fileLoc -> {
                    try {
                        String textFileContent = new String(Files.readAllBytes(fileLoc), StandardCharsets.UTF_8);

                        textFileContent = textFileContent.replaceAll("\r", "");
                        textFileContent = textFileContent.replaceAll("[\t ]+", " ");
                        textFileContent = textFileContent.replaceAll("\\s", " ");
                        textFileContent = textFileContent.replaceAll("[^0-9a-zA-Z \n]", "");

                        Path outputFileLocation = Paths.get(outputDir, fileLoc.toString().substring(1));
                        Files.createDirectories(outputFileLocation.getParent());
                        Files.write(outputFileLocation, textFileContent.getBytes(StandardCharsets.UTF_8));

                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                });
    }
}
