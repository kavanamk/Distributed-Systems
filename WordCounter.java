import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class WordCounter {

    public static void main(String[] cmdArgs) {
        if (cmdArgs.length != 2) {
            System.exit(1);
        }

        String sourceDirectory = cmdArgs[0];
        String destinationDirectory = cmdArgs[1];

        long startTime = System.currentTimeMillis(); 

        try {
            processFilesAndWriteUniqueWords(sourceDirectory, destinationDirectory);
            System.out.println("Word counting completed successfully.");
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }

        long endTime = System.currentTimeMillis(); 
        long executionTime = endTime - startTime;
        System.out.println("Total execution time: " + executionTime + " ms");
    }

    private static void processFilesAndWriteUniqueWords(String sourceDirectory, String destinationDirectory) throws IOException {
        Files.walk(Paths.get(sourceDirectory))
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        String DocumentContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                        String[] words = DocumentContent.split("[^0-9a-zA-Z]+");

                        Map<String, Integer> wordFrequencyMap = new HashMap<>();
                        for (String word : words) {
                            if (!word.isEmpty()) {
                                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                            }
                        }

                        writeWordFrequencyToFile(wordFrequencyMap, filePath, destinationDirectory);
                    } catch (IOException ioException) {
                        System.err.println(ioException.getMessage());
                    }
                });
    }

    private static void writeWordFrequencyToFile(Map<String, Integer> wordFrequencyMap, Path sourceFilePath, String destinationDirectory) throws IOException {
        Path destinationFilePath = Paths.get(destinationDirectory, sourceFilePath.toString().substring(1));
        Files.createDirectories(destinationFilePath.getParent());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFilePath.toFile()))) {
            for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        }
    }
}
