/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delaylock.forms;

import delaylock.data.DataFactory;
import delaylock.data.FileHandler;
import delaylock.threads.KeyGenerator;
import wheeler.generic.data.DialogFactory;
import wheeler.generic.data.LogicHandler;
import wheeler.generic.structs.StringSimpleList;

/**
 *
 * @author Greg
 */
public class Main extends javax.swing.JFrame {

    protected KeyGenerator currentOperation = null;
    protected int numHurdles = 5;
    protected boolean delayGeneration = true;
    
    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        initialize();
    }
    private void initialize(){
        try{
            if (!FileHandler.testProgramFolder(this)) System.exit(0);
        }
        catch(Exception e){
            DialogFactory.errorMsg(this, "An error occurred checking the Program Files folder", e, 1, 0);
        }
    }
    
    /**Perform admin-esque operations typically associated with the program's infrastructure
     * @throws Exception If any sort of error is thrown
     */
    protected void adminOptions() throws Exception{
        // The options to choose from
        StringSimpleList options = new StringSimpleList();
        String optRandomString = "Generate a string of random characters"; options.add(optRandomString);
        String choice = DialogFactory.chooseOption(this, options.toArray(), "Perform an infrastructure operation");
        
        // Perform an action
        if(choice == null){
            // Do nothing
        }else if(choice.equals(optRandomString)){
            // Get the length of the string
            String numDigits = DialogFactory.getString(this, "Please enter the length of the string to generate");
            if (numDigits == null) return;
            // Generate and display the string
            new DisplayString(DataFactory.generateRandomString(Integer.valueOf(numDigits))).setVisible(true);
        }else{
            DialogFactory.message(this, "No backend coded up for \"" + choice + "\"");
        }
    }
    
    /**Create a new keyfile as per the user's specifications, then generates and displays the key
     * @throws Exception If an error occurs writing the keyfile or the user enters bad data
     */
    protected void createNewKeyfile() throws Exception{
        // Get the length of the string to generate
        String value = DialogFactory.getString(this, "How long should the generated string be?");
        if (value == null) return;
        int length = Integer.valueOf(value);
        
        // Get the number of seconds to delay during generation
        value = DialogFactory.getString(this, "How many seconds should the program delay\nbefore producing the key?");
        if (value == null) return;
        int delay = Integer.valueOf(value);
        
        // Get the path to write the keyfile to
        String path = DialogFactory.saveFile(this, null);
        if (path == null) return;
        
        // Call on KeyGenerator to create a keyfile with the appropriate specifications
        KeyGenerator.generateKeyfile(length, delay, path);
        
        // Create a KeyGenerator instance, disable its delay, and generate the key (call run() directly)
        KeyGenerator generator = new KeyGenerator(null, path);
        generator.delayResult = false;
        generator.run();
        
        // Display the key or an error as appropriate
        if(generator.ex != null){
            DialogFactory.errorMsg(this, "The generator encountered an error", generator.ex, 1, 0);
        }else if(generator.result == null){
            DialogFactory.message(this, "The generator did not actually produce a key");
        }else{
            new DisplayString(generator.result).setVisible(true);
        }
    }
    
    
    /**Kick off a thread to generate a key using a keyfile specified by the user
     * @throws Exception If there's a problem reading the keyfile
     */
    protected void generateKey() throws Exception{
        // Make things difficult for the user; have them enter five random strings
        String[] expect = new String[numHurdles];
        String[] input = new String[numHurdles];
        for(int i = 0; i < numHurdles; i++){
            String str = DataFactory.generateReadableRandomString(30);
            expect[i] = str;
            input[i] = DialogFactory.getString(this, "Type this string:\n" + str);
            if (input[i] == null) return;
            LogicHandler.sleep(10 * 1000);
        }
        for(int i = 0; i < numHurdles; i++){
            if(!expect[i].equals(input[i])){
                DialogFactory.message(this, "One of the strings you entered was incorrect");
                return;
            }
        }
        
        // Point out the keyfile and kick off the generation
        String keyfile = DialogFactory.chooseFile(this, null);
        if (keyfile == null) return;
        currentOperation = new KeyGenerator(this, keyfile);
        currentOperation.delayResult = delayGeneration;
        btnGenerateKey.setText("(Cancel)");
        LogicHandler.startThread(currentOperation);
    }
    
    
    /**Stop the operation before it completes
     * @throws Exception If there's an issue stopping the operation (i.e. deleting the session file)
     */
    protected void cancelGeneration() throws Exception{
        currentOperation.earlyStop(10);
    }
    
    
    /**Used by KeyGenerator to call the main interface when a key has been produced
     * @param generator The KeyGenerator making the call
     */
    public void signalDone(KeyGenerator generator){
        // Set the button's text back to normal
        btnGenerateKey.setText("Generate Key");
        // Clear the current operation variable
        currentOperation = null;
        // If a result was produced, open a window to display it
        if (generator.result != null) new DisplayString(generator.result).setVisible(true);
        // The an error occurred, display it
        if (generator.ex != null) DialogFactory.errorMsg(
                this, "The key generator encountered an issue", generator.ex, 1, 0
            );
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        btnNewKeyfile = new javax.swing.JButton();
        btnGenerateKey = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Delay Lock");
        lblTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTitleMouseClicked(evt);
            }
        });

        btnNewKeyfile.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnNewKeyfile.setText("New Keyfile");
        btnNewKeyfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewKeyfileActionPerformed(evt);
            }
        });

        btnGenerateKey.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnGenerateKey.setText("Generate Key");
        btnGenerateKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateKeyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNewKeyfile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGenerateKey)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewKeyfile)
                    .addComponent(btnGenerateKey))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewKeyfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewKeyfileActionPerformed
        try{
            createNewKeyfile();
        }
        catch(Exception e){
            DialogFactory.errorMsg(this, "An error occurred", e, 1, 0);
        }
    }//GEN-LAST:event_btnNewKeyfileActionPerformed

    private void btnGenerateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateKeyActionPerformed
        try{
            if(currentOperation != null){
                cancelGeneration();
            }else{
                generateKey();
            }
        }
        catch(Exception e){
            DialogFactory.errorMsg(this, "An error ocurred", e, 1, 0);
        }
    }//GEN-LAST:event_btnGenerateKeyActionPerformed

    private void lblTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTitleMouseClicked
        try{
            adminOptions();
        }
        catch(Exception e){
            DialogFactory.errorMsg(this, "An error occurred", e, 1, 0);
        }
    }//GEN-LAST:event_lblTitleMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateKey;
    private javax.swing.JButton btnNewKeyfile;
    private javax.swing.JLabel lblTitle;
    // End of variables declaration//GEN-END:variables
}
