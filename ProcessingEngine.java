package csc435.app;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessingEngine {
    private static IndexStore store;
    private final int numThreads;
    private static Lock globalIndexLock = new ReentrantLock();

    public ProcessingEngine(IndexStore store, int numThreads) {
        this.store = store;
        this.numThreads = numThreads;
    }

    public void indexFiles(String datasetPath) {
        Path inputPath = Paths.get(datasetPath);
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        try {
            Files.walkFileTree(inputPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    
                        IndexBuilder indexBuilder = new IndexBuilder(file);
                        executorService.submit(indexBuilder);
                    
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error traversing directory: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        printGlobalIndex();
    }

    

    public void printGlobalIndex() {
        int stop = 10000;
        Map<String, WordInfo>  globalIndex=store.getGlobalIndex();
        // Create a copy of the entry set to iterate over
        globalIndexLock.lock();
        try {
            Set<Map.Entry<String, WordInfo>> entrySetCopy = new HashSet<>(globalIndex.entrySet());
            for (Map.Entry<String, WordInfo> entry : entrySetCopy) {
                if (stop-- == 0) {
                    break;
                }
                String word = entry.getKey();
                WordInfo wordInfo = entry.getValue();
                System.out.println("Word: " + word + ", Filenames and Occurrences: " + wordInfo.getFilenameOccurrences());
            }
        } finally {
            globalIndexLock.unlock();
        }
    }
    


    private static class IndexBuilder implements Runnable {
        private final Path filePath;

        public IndexBuilder(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            Map<String, WordInfo> localIndex = new HashMap<>();

            try {
                String textFileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                textFileContent = textFileContent.replaceAll("[^a-zA-Z0-9]+", " ");
                textFileContent = textFileContent.replaceAll("\r", "");
                textFileContent = textFileContent.replaceAll("[\t ]+", " ");
                textFileContent = textFileContent.replaceAll("\\s", " ");
                textFileContent = textFileContent.replaceAll("[^0-9a-zA-Z \n]", "");
                String[] terms = textFileContent.split("\\s+");

                for (String term : terms) {
                    term = term.toLowerCase();
                    WordInfo wordInfo = localIndex.getOrDefault(term, new WordInfo());
                    wordInfo.incrementOccurrences(filePath.getFileName().toString(), 1);
                    localIndex.put(term, wordInfo);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }

            store.updateIndex(localIndex);
        }
    }
    
     public void searchFiles(String query) {
        String[] terms = query.split(" AND ");
        List<FileOccurrences> filesWithAllTerms = new ArrayList<>();
        Map<String, WordInfo> globalIndex=store.getGlobalIndex();

        // Iterate over the global index to find files containing all terms
        for (Map.Entry<String, WordInfo> entry : globalIndex.entrySet()) {
            boolean containsAllTerms = true;
            int totalOccurrences = 0;

            // Check if the file contains all terms
            for (String term : terms) {
                if (!entry.getValue().getFilenameOccurrences().containsKey(term)) {
                    containsAllTerms = false;
                    break;
                }
                totalOccurrences += entry.getValue().getFilenameOccurrences().get(term);
            }

            // If the file contains all terms, add it to the list
            if (containsAllTerms) {
                filesWithAllTerms.add(new FileOccurrences(entry.getKey(), totalOccurrences));
            }
        }

        // Sort the files based on the total occurrences
        filesWithAllTerms.sort(new Comparator<FileOccurrences>() {
            @Override
            public int compare(FileOccurrences fo1, FileOccurrences fo2) {
                return Integer.compare(fo2.getTotalOccurrences(), fo1.getTotalOccurrences());
            }
        });

        // Print the top 10 files
        int count = 0;
        for (FileOccurrences fileOccurrences : filesWithAllTerms) {
            if (count >= 10) {
                break;
            }
            System.out.println("File: " + fileOccurrences.getFilename() + ", Total Occurrences: " + fileOccurrences.getTotalOccurrences());
            count++;
        }
    }

    public void stopWorkers() {
        System.out.println("Stopping threads");
    }

    public static class WordInfo {
        private Map<String, Integer> filenameOccurrences = new HashMap<>();

        public Map<String, Integer> getFilenameOccurrences() {
            return filenameOccurrences;
        }

        public void incrementOccurrences(String filename, int occurrences) {
            filenameOccurrences.put(filename, filenameOccurrences.getOrDefault(filename, 0) + occurrences);
        }
    }

    private static class FileOccurrences {
        private String filename;
        private int totalOccurrences;

        public FileOccurrences(String filename, int totalOccurrences) {
            this.filename = filename;
            this.totalOccurrences = totalOccurrences;
        }

        public String getFilename() {
            return filename;
        }

        public int getTotalOccurrences() {
            return totalOccurrences;
        }
    }
}
