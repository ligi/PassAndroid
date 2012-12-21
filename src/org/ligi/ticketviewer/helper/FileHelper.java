package org.ligi.ticketviewer.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import android.util.Log;
/**
 * Helper to read the content of a File
 * 
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 */
public class FileHelper {

	/**
	 * reads a file to a string
	 * 
	 * @param file
	 * @return the content of the file
	 * @throws IOException
	 */
	public static String file2String(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}

	}
	
	
	public static void DeleteRecursive(File dir)
    {
        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) 
            {
               File temp =  new File(dir, children[i]);
               if(temp.isDirectory())
               {
                   Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                   DeleteRecursive(temp);
               }
               else
               {
                   Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                   boolean b = temp.delete();
                   if(b == false)
                   {
                       Log.d("DeleteRecursive", "DELETE FAIL");
                   }
               }
            }

            dir.delete();
        }
    }
}
