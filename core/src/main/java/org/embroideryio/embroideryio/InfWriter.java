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
public class InfWriter extends EmbWriter {
    
    @Override
    public void write() throws IOException {
        writeInt32BE(1);
        writeInt32BE(8);
        space_holder(4);
        writeInt32BE(pattern.threadlist.size());
        int index = 0;
        for (EmbThread t : pattern.threadlist) {
            String details = t.description;
            if (details == null) {
                details = "Unknown";
            }
            String chart = t.chart;
            if (chart == null) {
                chart = "Unknown";
            }
            writeInt16BE(11 + details.length() + chart.length());
            //2 + 2 + 1 + 1 + 1 + 2 + d + 1 + c + 1 = 11 + d + c
            writeInt16BE(index); // record index
            index += 1;
            writeInt8(t.getRed());
            writeInt8(t.getGreen());
            writeInt8(t.getBlue());
            writeInt16BE(index); // needle number
            write(details);
            writeInt8(0);
            write(chart);
            writeInt8(0);
        }
        writeSpaceHolder32BE(tell());
    }
}
