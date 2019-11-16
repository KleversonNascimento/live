/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ufabc.live;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Live {
    public static void main(String args[]) throws IOException{
        byte[] file = getFileInByteArray();
        System.out.print(file.length);
    }
    
    private static byte[] getFileInByteArray() throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(new File("res/canetaazul.mov"));

        byte[] buf = new byte[1024];
        int n;
        
        while (-1 != (n = fis.read(buf))) {
            baos.write(buf, 0, n);
        }
        
        return baos.toByteArray();
    }
}