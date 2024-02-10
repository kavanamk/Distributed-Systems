package csc435.app;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import csc435.app.ProcessingEngine.WordInfo;

import java.util.HashMap;


public class IndexStore {

    private final Map<String, WordInfo> globalIndex;
    private final Lock globalIndexLock = new ReentrantLock();

    public IndexStore() {
        // TO-DO implement constructor
        this.globalIndex = new HashMap<>();
    }

    public void insertIndex() {
        // TO-DO implement index insert method
        
    }

    public void lookupIndex() {
        // TO-DO implement index lookup method
    }

    public Map<String, WordInfo> getGlobalIndex() {
        return globalIndex;
    }

    public void updateIndex(Map<String, WordInfo> localIndex) {
        globalIndexLock.lock();
        try {
            for (Map.Entry<String, WordInfo> entry : localIndex.entrySet()) {
                String word = entry.getKey();
                WordInfo localWordInfo = entry.getValue();
                WordInfo globalWordInfo = globalIndex.getOrDefault(word, new WordInfo());

                for (Map.Entry<String, Integer> filenameOccurrence : localWordInfo.getFilenameOccurrences().entrySet()) {
                    String filename = filenameOccurrence.getKey();
                    int occurrences = filenameOccurrence.getValue();
                    globalWordInfo.incrementOccurrences(filename, occurrences);
                }

                globalIndex.put(word, globalWordInfo);
            }
        } finally {
            globalIndexLock.unlock();
        }
    }
    

}
