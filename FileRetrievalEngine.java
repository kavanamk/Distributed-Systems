package csc435.app;

public class FileRetrievalEngine 
{
    public static void main( String[] args )
    {
        IndexStore store = new IndexStore();
        ProcessingEngine engine = new ProcessingEngine(store, 8);
        AppInterface appInterface = new AppInterface(engine);

        appInterface.readCommands();
    }
}
///Users/kavanamanvi/Desktop/sem2/DS/Assignment2/Dataset1