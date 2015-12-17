/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delaylock.data;

import javax.swing.JFrame;
import wheeler.generic.data.StringHandler;

/**
 *
 * @author Greg
 */
public class FileHandler extends wheeler.generic.data.FileHandler{
    
    protected static String programFolder = "C:\\Program Files\\Wheeler\\Delay Lock";
    
    /**Make sure that we can access our folder in Program Files
     * @param caller The calling JFrame
     * @return True if everything checks out, false if doesn't and user gives up
     * @throws Exception If there's a problem and the user didn't specify a calling JFrame
     */
    public static boolean testProgramFolder(JFrame caller) throws Exception{
        return testProgramFolder(programFolder, caller);
    }
    
    /**Return a unique session filepath
     * @return A filepath for a file under data/session that is different from all filepaths that have already been returned
     * @throws Exception If something goes wrong accessing the session folder
     */
    public static String getSessionFile() throws Exception{
        String path = composeFilepath(programFolder, "data");
        path = composeFilepath(path, "session");
        ensureFolderExists(path);
        return composeFilepath(path, StringHandler.getUnique() + ".txt");
    }
    
}
