package org.embroideryio.embroideryio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Tat on 12/19/2017.
 */

public class ColReader extends EmbReader {

    @Override
    protected void read() throws IOException {
        BufferedReader d = new BufferedReader(new InputStreamReader(stream));
        Scanner scanner = new Scanner(d.readLine());
        int numberOfColors = scanner.nextInt();
        for (int i = 0; i < numberOfColors; i++) {
            int num, blue, green, red;
            String line = d.readLine();
            if (line == null || line.isEmpty()) {
                i--;
                continue;
            }
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(",");
            num = lineScanner.nextInt();
            blue = lineScanner.nextInt();
            green = lineScanner.nextInt();
            red = lineScanner.nextInt();
            EmbThread t = new EmbThread(red, green, blue, "", "");
            pattern.addThread(t);
        }
    }
}
