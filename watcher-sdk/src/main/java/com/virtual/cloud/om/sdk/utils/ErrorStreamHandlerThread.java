package com.virtual.cloud.om.sdk.utils;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ErrorStreamHandlerThread extends Thread {

    private StringBuffer stderr;
    private Process process;

    public ErrorStreamHandlerThread(Process proc) {
        stderr = new StringBuffer();
        this.process = proc;
    }

    public String getStdErrorMessage() {
        return stderr.toString();
    }

    boolean getStdError() {
        BufferedReader bufferReader = null;
        try {
            bufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),"UTF-8"));
            do {
                if (!bufferReader.ready()) {
                    try {
                    	process.exitValue();
                        break;
                    } catch(IllegalThreadStateException _ex) {
                        try {
                            Thread.sleep(100L);
                        }
                        catch(InterruptedException _ex2) {
                            // Restore interrupted state...
                            Thread.currentThread().interrupt();
                        }
                    }
                } else {
                    String s = bufferReader.readLine();
                    stderr.append(s + "\n");
                }
            } while(true);
            log.info(stderr.toString());
        } catch (IOException ioexception) {
            return false;
        } finally {
        	if (bufferReader != null) {
        		try {
        			bufferReader.close();
        		} catch(IOException ioexception1) {

        		}
        	}
        }
        return true;
    }

    public void run() {
    	if (process == null) {
    		return;
    	}

    	getStdError();
    }
}
