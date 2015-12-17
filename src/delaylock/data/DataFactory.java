/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delaylock.data;

import wheeler.generic.data.LogicHandler;
import wheeler.generic.data.StringHandler;

/**
 *
 * @author Greg
 */
public class DataFactory {
    
    /** An array of the characters that appear in our passcodes */
    public static final String charPool = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    
    /**An array of strings used in conjunction with a keyfile to produce a passcode
     */
    public static String[] innerStrings = {
        "KX3NuE2NeJxdybiO742mzGGydUyBzH",
        "5AdMuxKDR76hhyMhNeviN5tW2Ni5mJ",
        "fVxc61Y8f157OJNi8KIO6E0z3TkRxD",
        "TV1IKmRMsNUkgHxMWHJxPAwygMTdfe",
        "2D3mJFTlAXRHfJXKDPLn2AqwN7mIVy",
        "Pc7eTADgNh5a7jmV4YqN1tuK9nIdqS",
        "yGUSlHk050W2wOqOihjVfn0ioX5JsB",
        "jP1GPiFJ4AxGEx5qJRud4cOlFaSLcq",
        "uvi5arhNUURGJXE83gxBBOr3GmUkrT",
        "S1GdSeftzHpAUldaHnXeVVwJw6cJ8r"
    };
    
    
    /**Randomly get one of the characters used to generate our passcodes
     * @return A random character from charPool
     */
    public static String getRandomCharacter(){
        return StringHandler.charAt(
                DataFactory.charPool,
                LogicHandler.getRandomNumber(DataFactory.charPool.length())
            );
    }
    
    
    /**Generate a random string using the characters used in our passcodes
     * @param length The length of the string to generate
     * @return A string of the specified length composed of the characters in charPool
     */
    public static String generateRandomString(int length){
        String str = "";
        for(int i = 0; i < length; i++){
            str += getRandomCharacter();
        }
        return str;
    }
    
    
    /**Generate a random string using the characters used in our passcodes that the user can read without issue.
     * Seriously, whose bright idea was it to have I and l look so similar in certain fonts?
     * @param length The length of the string to generate
     * @return A string of the specified length composed of the characters in charPool (except I and l)
     */
    public static String generateReadableRandomString(int length){
        while(true){
            String str = generateRandomString(length);
            if (StringHandler.contains(str, "I", true)) continue;
            if (StringHandler.contains(str, "l", true)) continue;
            return str;
        }
    }
    
}
