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
public class EdrWriter extends EmbWriter {

    @Override
    public void write() throws IOException {
        for (EmbThread t : pattern.threadlist) {
            this.writeInt8(t.getRed());
            this.writeInt8(t.getGreen());
            this.writeInt8(t.getBlue());
            this.writeInt8(0);
        }
    }
}
