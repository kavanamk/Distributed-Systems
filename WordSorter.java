import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class WordSorter {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.exit(1);
        }

        String inputDir = args[0];
        String outputDir = args[1];
        long start = System.currentTimeMillis(); 

        try {
            processAndWriteSortedWords(inputDir, outputDir);
            System.out.println("Word sorting is completed successfully.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        long end = System.currentTimeMillis(); 
        long totalExecutionTime = end - start;
        System.out.println("Total execution time: " + totalExecutionTime + " ms");
    }

    private static class WordFrequency implements Comparable<WordFrequency> {
        private final String word;
        private final int frequency;

        public WordFrequency(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }

        public String getWord() {
            return word;
        }

        public int getFrequency() {
            return frequency;
        }

        @Override
        public int compareTo(WordFrequency other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    private static void processAndWriteSortedWords(String sourceDirectory, String destinationDirectory) throws IOException {
        Files.walk(Paths.get(sourceDirectory))
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        List<WordFrequency> wordFrequencyDictionary = new ArrayList<>();
                        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] sections = line.split(" ");
                                if (sections.length == 2) {
                                    String term = sections[0];
                                    int freq = Integer.parseInt(sections[1]);
                                    wordFrequencyDictionary.add(new WordFrequency(term, freq));
                                }
                            }
                        }
                        wordFrequencyDictionary.sort(Collections.reverseOrder()); 
    
                        Path outputFileLocation = Paths.get(destinationDirectory, filePath.toString().substring(1));
                        Files.createDirectories(outputFileLocation.getParent());
    
                        try (BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(outputFileLocation.toFile()))) {
                            for (int i = 0; i < wordFrequencyDictionary.size(); i++) {
                                WordFrequency wordFrequency = wordFrequencyDictionary.get(i);
                                bufferWriter.write(wordFrequency.getWord() + " " + wordFrequency.getFrequency());
                                bufferWriter.newLine();
                            }
                            
                        }
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                });
    }
    

    
}
