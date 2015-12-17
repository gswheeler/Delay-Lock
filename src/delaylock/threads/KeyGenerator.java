/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delaylock.threads;

import delaylock.data.DataFactory;
import delaylock.data.FileHandler;
import delaylock.forms.Main;
import wheeler.generic.data.LogicHandler;
import wheeler.generic.data.StringHandler;
import wheeler.generic.data.TimeHandler;
import wheeler.generic.data.readers.FileReader;
import wheeler.generic.data.readers.FileWriter;
import wheeler.generic.logging.Logger;

/**
 * Handles the generation of keyfiles and passcodes from keyfiles
 */
public class KeyGenerator implements Runnable {
    
    // Related to the object instance
    protected Main parent = null;
    protected String sessionFile = null;
    protected String keyfile = null;
    
    // Key-generation parameters
    protected int length = 0;
    protected int delay = 0;
    public boolean delayResult = true;
    public String result = null;
    protected boolean done = false;
    public Exception ex = null;
    
    // Dynamic key-generation variables
    private FileReader reader = null;
    private int[] values = null;
    private int stringIndex = 0;
    private int internalIndexA = 0;
    private int internalIndexB = 0;
    
    /**
     * Constructor for KeyGenerator
     * @param frmParent The form that creates and ultimately runs instances of this class. Null if the generation will be watched directly
     * @param strKeyfile The filepath of the keyfile to produce a key from
     * @throws Exception If there's an issue creating the object
     */
    public KeyGenerator(Main frmParent, String strKeyfile) throws Exception{
        parent = frmParent;
        keyfile = strKeyfile;
        sessionFile = FileHandler.getSessionFile();
        FileHandler.ensureFileExists(sessionFile);
    }
    
    @Override
    public void run(){
        try{
            // Set up the generator; open the keyfile, grab the arguments, set variables
            setup();
            int timeleft = delay;
            
            // For each second of the delay, perform an action on the passcode
            while(running()){
                // If the delay is over, produce the result and stop looping
                if(timeleft < 1){
                    String passcode = "";
                    for (int value : values) passcode += StringHandler.charAt(DataFactory.charPool, value);
                    result = passcode;
                    reader.close();
                    close();
                    break;
                }
                
                // Perform an action on the in-progress result
                performAction();
                
                // If appropriate, delay before the next action
                if (delayResult) LogicHandler.sleep(1000);
                timeleft--;
            }
        }
        catch(Exception e){
            ex = e;
            try{
                if (reader != null) reader.close();
                close();
            }
            catch(Exception e2){
                Logger.print("A secondary exception occurred while removing the session file");
                Logger.print(e2, 1, 0);
            }
        }
        done = true;
        if (parent != null) parent.signalDone(this);
    }
    
    
    /**Setup a generation operation: open the keyfile reader, parse the arguments, initialize the passcode values
     * @throws Exception If there's an issue with the keyfile
     */
    protected void setup() throws Exception{
        // Open the reader
        reader = new FileReader(keyfile);
        
        // Grab keyfile arguments (all characters before the first newline)
        // Arguments are tab-delineated strings
        String argumentsString = ""; int c;
        while((c = reader.readChar()) != (int)'\n'){
            if (c == -1) throw new Exception("Failed to parse the keyfile's argument string");
            argumentsString += (char)c;
        }
        String[] args = StringHandler.parseIntoArray(argumentsString, "\t");
        
        // Based on the arguments version (number in args[0]), parse the arguments and set values
        if(args[0].equals("0")){
            if (args.length != 3) throw new Exception("Wrong number of arguments for version \"" + args[0] + "\"");
            length = Integer.valueOf(args[1]);
            delay = Integer.valueOf(args[2]);
        }else{
            throw new Exception("Did not recognize arguments version \"" + args[0] + "\"");
        }
        
        // Initialize the passcode
        values = new int[length];
        for(int i = 0; i < values.length; i++){
            values[i] = getKeyfileValue();
        }
    }
    
    
    /**Create a keyfile that will produce a key
     * @param stringLength The length of the key to produce
     * @param resultDelay The number of seconds to delay when producing the key
     * @param filepath The filepath to save the keyfile to
     * @throws Exception If there's an issue writing the keyfile
     */
    public static void generateKeyfile(int stringLength, int resultDelay, String filepath) throws Exception{
        // Open the keyfile
        FileWriter writer = new FileWriter(filepath);
        
        // Write the keyfile
        Exception ex = null;
        try{
            // Start with the "command arguments"
            writer.write("0"); // The arguments version
            writer.write("\t" + Integer.toString(stringLength)); // The length of the key to produce
            writer.write("\t" + Integer.toString(resultDelay)); // The number of seconds to delay while producing the key
            writer.write("\n"); // End the arguments string
            // Write 4,000 random characters
            for (int i = 0; i < 4000; i++) writer.write(DataFactory.getRandomCharacter());
        }
        catch(Exception e){
            ex = e;
        }
        
        // Close the keyfile
        try{
            writer.close();
        }
        catch(Exception e){
            if (ex == null) ex = e;
        }
        
        // If there was an issue, throw the exception
        if (ex != null) throw ex;
    }
    
    
    /**Take action on the next passcode character using an internal and keyfile value
     * @throws Exception If there's a problem getting the next value from the keyfile
     */
    protected void performAction() throws Exception{
        // Get the operators for the operation
        int value = values[stringIndex];
        int key1 = getKeyfileValue();
        int key2 = getInternalValue();
        
        // Perform an operation
        switch(value % 3){
            case 0:
                value = (value + key1 + key2) % DataFactory.charPool.length();
                break;
            case 1:
                value = value - key1 - key2;
                while (value < 0) value += DataFactory.charPool.length();
                break;
            case 2:
                value = ((value ^ key1) ^ key2) % DataFactory.charPool.length();
                break;
            default:
                throw new Exception("Somehow \"value % 3\" produced a value outside of [0,2]");
        }
        
        // Set the value and increment the index
        values[stringIndex] = value;
        stringIndex = (stringIndex + 1) % values.length;
    }
    
    
    /**Returns the next numerical value from the keyfile
     * @return The character's index in the charPool string
     * @throws Exception If there's an issue reading the keyfile
     */
    protected int getKeyfileValue() throws Exception{
        // Grab a value from the keyfile
        int value = reader.readChar();
        
        // If we hit the end of the file, re-open and skip the arguments
        if(value < 0){
            reader = new FileReader(keyfile);
            while (true) if (reader.readChar() == (int)'\n') break;
            return getKeyfileValue();
        }
        
        // Return the index of the returned character based on its position in charPool
        return DataFactory.charPool.indexOf(String.valueOf((char)value));
    }
    
    
    /**Get the next numerical value from our internal strings
     * @return The character's index in the charPool string
     */
    protected int getInternalValue(){
        // Get a character from our internal strings
        String value = StringHandler.charAt(DataFactory.innerStrings[internalIndexA], internalIndexB);
        
        // Advance the indexes
        internalIndexB++;
        if(!(internalIndexB < DataFactory.innerStrings[internalIndexA].length())){
            internalIndexA = (internalIndexA + 1) % DataFactory.innerStrings.length;
            internalIndexB = 0;
        }
        
        // Return the index of the character
        return DataFactory.charPool.indexOf(value);
    }
    
    
    /**Checks if the thread is still running (note: returns true before run() is called)
     * @return True if the thread is not finished yet (generation not done and not called off)
     */
    public boolean running(){
        return FileHandler.fileExists(sessionFile);
    }
    protected void close() throws Exception{
        FileHandler.deleteFile(sessionFile);
    }
    public void earlyStop(int wait) throws Exception{
        close();
        long deadline = TimeHandler.ticks() + (wait * 1000);
        while(!done){
            if (TimeHandler.ticks() > deadline)
                throw new Exception("Failed to stop the generator within " + wait + "seconds");
            LogicHandler.sleep(100);
        }
    }
    
}
