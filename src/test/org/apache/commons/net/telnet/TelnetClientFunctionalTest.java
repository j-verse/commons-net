/*
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.net.telnet;
import junit.framework.TestCase;
import java.io.InputStream;
import java.io.OutputStream;

/***
 * JUnit functional test for TelnetClient.
 * Connects to the weather forecast service
 * rainmaker.wunderground.com and asks for Los Angeles forecast.
 * <p>
 * @author Bruno D'Avanzo
 ***/
public class TelnetClientFunctionalTest extends TestCase
{
    protected TelnetClient tc1;

    /***
     * main for running the test.
     ***/
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(TelnetClientFunctionalTest.class);
    }

    /***
     * test setUp
     ***/
    protected void setUp()
    {
        tc1 = new TelnetClient();
    }

    /***
     * Do the functional test:
     * - connect to the weather service
     * - press return on the first menu
     * - send LAX on the second menu
     * - send X to exit
     ***/
    public void testFunctionalTest() throws Exception
    {
        boolean testresult = false;
        tc1.connect("rainmaker.wunderground.com", 3000);

        InputStream is = tc1.getInputStream();
        OutputStream os = tc1.getOutputStream();

        boolean cont = waitForString(is, "Return to continue:", 30000);
        if (cont)
        {
            os.write("\n".getBytes());
            os.flush();
            cont = waitForString(is, "city code--", 30000);
        }
        if (cont)
        {
            os.write("LAX\n".getBytes());
            os.flush();
            cont = waitForString(is, "Los Angeles", 30000);
        }
        if (cont)
        {
            cont = waitForString(is, "X to exit:", 30000);
        }
        if (cont)
        {
            os.write("X\n".getBytes());
            os.flush();
            tc1.disconnect();
            testresult = true;
        }

        assertTrue(testresult);
    }


    /***
     * Helper method. waits for a string with timeout
     ***/
    public boolean waitForString(InputStream is, String end, long timeout) throws Exception
    {
        byte buffer[] = new byte[32];
        long starttime = System.currentTimeMillis();

        String readbytes = new String();
        while((readbytes.indexOf(end) < 0) &&
              ((System.currentTimeMillis() - starttime) < timeout))
        {
            if(is.available() > 0)
            {
                int ret_read = is.read(buffer);
                readbytes = readbytes + new String(buffer, 0, ret_read);
            }
            else
            {
                Thread.sleep(500);
            }
        }

        if(readbytes.indexOf(end) >= 0)
        {
            return (true);
        }
        else
        {
            return (false);
        }
    }
}