package com.common;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by txt on 2017/10/10.
 */

public class TextResourceReader {
        public static String readTextFileFromResource(Context context, int resourceid)
        {
            StringBuilder body = new StringBuilder();
            try
            {
                InputStream inputStream =
                        context.getResources().openRawResource(resourceid);
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String nextline;
                while((nextline = bufferedReader.readLine())!=null)
                {
                    body.append(nextline);
                    body.append('\n');
                }
            }
            catch(IOException e)
            {
                throw  new RuntimeException("Could not open the resource: " + resourceid,e);
            }
            catch (Resources.NotFoundException nfe)
            {
                throw new RuntimeException("Resource not found: "+resourceid,nfe);
            }
            return body.toString();
        }
}

