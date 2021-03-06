package it.polimi.deib.privacydataflow.utils;

import java.io.*;
import java.nio.file.Path;

/**
 * Created by miik on 02/10/17.
 */
public class TimestampedFileMerger {

    public static void merge(File file1, File file2, File out) throws IOException {
        try(
                BufferedReader br1 = new BufferedReader(new FileReader(file1));
                BufferedReader br2 = new BufferedReader(new FileReader(file2));
                PrintWriter pw = new PrintWriter(new FileWriter(out));
        ) {
            String line1 = br1.readLine();
            String line2 = br2.readLine();
            while(line1 != null || line2 != null){
                if(line1 != null && line2 != null){
                    Long t1 = Long.parseLong(line1.split(" ")[0].substring(1));
                    Long t2 = Long.parseLong(line2.split(" ")[0].substring(1));
                    if(t2 >= t1){
                        pw.write(line1 + "\n");
                        line1 = br1.readLine();
                    } else{
                        line2 = br2.readLine();
                        pw.write(line2 + "\n");
                    }
                } else if(line1 != null && line2 == null){
                    Long t1 = Long.parseLong(line1.split(" ")[0].substring(1));
                    pw.write(line1 + "\n");
                    line1 = br1.readLine();
                } else if(line1 == null && line2 != null){
                    Long t2 = Long.parseLong(line2.split(" ")[0].substring(1));
                    pw.write(line2 + "\n");
                    line2 = br2.readLine();
                }
            }

            pw.close();
            br1.close();
            br2.close();
        }
    }
}
