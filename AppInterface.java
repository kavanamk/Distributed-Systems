package csc435.app;

import java.util.Scanner;

public class AppInterface {
    private ProcessingEngine engine;

    public AppInterface(ProcessingEngine engine) {
        this.engine = engine;
    }

    public void readCommands() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim();
            
            if (command.compareTo("quit") == 0) {
                engine.stopWorkers();
                break;
            }

            // if the command begins with index, index the files from the specified directory
            if (command.length() >= 5 && command.substring(0, 5).compareTo("index") == 0) {
                // TO-DO implement index operation
                String[] parts = command.split("\\s+", 2);
                if (parts.length == 2) {
                    String datasetPath = parts[1];
                    engine.indexFiles(datasetPath);
                    continue;
                } else {
                    System.out.println("Invalid index command.");
                }
                
            }

            // if the command begins with search, search for files that matches the query
            if (command.length() >= 6 && command.substring(0, 6).compareTo("search") == 0) {
                // TO-DO implement index operation
                String[] parts = command.split("\\s+", 2);
                if (parts.length == 2) {
                    String query = parts[1];
                    engine.searchFiles(query);
                    continue;
                } else {
                    System.out.println("Invalid search command.");
                }
            }

            System.out.println("unrecognized command!");


            
        }

        scanner.close();
    }
}

