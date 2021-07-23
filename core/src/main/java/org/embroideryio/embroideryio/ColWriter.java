/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embroideryio.embroideryio;

import java.io.IOException;

/**
 *
 * @author Tat
 */
public class ColWriter extends EmbWriter {

    @Override
    public void write() throws IOException {
        write(pattern.threadlist.size() + "\r\n");
        int index = 0;
        for (EmbThread t : pattern.threadlist) {
            write(index + "," + t.getRed() + "," + t.getGreen() + "," + t.getBlue() + "\r\n");
            index += 1;
        }
    }
}
