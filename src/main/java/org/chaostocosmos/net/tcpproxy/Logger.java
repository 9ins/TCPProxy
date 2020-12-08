package org.chaostocosmos.net.tcpproxy;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**  
 * Logger : Logger
 * Description : 
 *  
 * Modification Information  
 *  ---------   ---------   -------------------------------
 *  20161118	9ins		First draft
 *  
 * @author 9ins
 * @since 20161118
 * @version 1.0
 */
public class Logger {
	/**
	 * Align character num
	 */
	public static final int alignCharNum = 180;	
	/**
	 * Info filter
	 */
	public static boolean INFO_FILTER = true;	
	/**
	 * Debug filter
	 */
	public static boolean DEBUG_FILTER = true;	
	/**
	 * Error filter
	 */
	public static boolean ERROR_FILTER = true;	
	/**
	 * Fatal filter 
	 */
	public static boolean FATAL_FILTER = true;	
	/**
	 * Exception filter
	 */
	public static boolean THROWABLE_FILTER = true;
	/**
	 * Log file size(default : 100MB)
	 */
	public static long LOG_SIZE = 1024*1024*100;
	/**
	 * Log encoding type
	 */
	public static String ENCODING = "UTF-8";
	/**
	 * Log split interval (default : 24)
	 */
	public static int LOG_HOUR = 24;
	/**
	 * whether append
	 */
	private boolean isAppend;
	/**
	 * Print object
	 */
    private PrintWriter log;    
    /**
     * Print stream 
     */
    private PrintStream ps;    
   /**
     * Log start time
     */
    private long startTime;    
    /**
     * Log interval
     */
    private long logInterval;    
    /**
     * File separator
     */
    private String fs = System.getProperty("file.separator");    
    /**
     * Log extention
     */
    private String logSuffix = ".log";    
    /**
     * Log file object
     */
    private File logFile;    
    /**
     * Log name
     */
    private String logName;    
    /**
     * Logger instance
     */
    private static Logger logger;    
    /**
     * Get logger instance
     * @return
     */
    public static Logger getInstance() {
    	if(logger == null) {
    		logger = new Logger(LOG_HOUR, LOG_SIZE, "main");
    	}
    	return logger;
    }    
    /**
     * Get logger instance
     * @param hour 
     * @return 로거 
     */
    public static Logger getInstance(int hour) {
    	return getInstance(hour, LOG_SIZE, "main");
    }    
    /**
     * Get logger instance
     * @param logPath
     * @return
     */
    public static Logger getInstance(long logSize) {
    	return getInstance(LOG_HOUR, logSize, "main");
    }    
    /**
     * Get logger instance
     * @param hour 
     * @param logSize 
     * @return 
     */
    public static Logger getInstance(int hour, long logSize) {
    	return getInstance(hour, logSize, "main");
    }
    /**
     * Get logger instance
     * @param hour 
     * @param logSize
     * @param logPath
     * @return
     */
    public static Logger getInstance(int hour, long logSize, String logPath) {
    	return getInstance(hour, logSize, logPath, ENCODING);
    }    
    /**
     * Get logger instance
     * @param hour
     * @param logPath
     * @param encoding
     * @return 
     */
    public static Logger getInstance(int hour, long logSize, String logPath, String encoding) {
    	if(logger == null)
    		logger = new Logger(hour, logSize, logPath, encoding);
    	return logger;    	
    }    
    /**
     * Constructor
     * @param hour
     * @param logSize
     * @param logPath
     * @param ENCODING
     */
    private Logger(int hour, long logSize, String logPath) {
    	this(hour, logSize, logPath, ENCODING);
    }    
    /**
     * Constructor
     * @param period
     * @param logSize
     * @param logPath
     * @param encoding
     */
    private Logger(int hour, long logSize, String logPath, String encoding) {
    	init(hour, logSize, logPath, encoding);
    }    
    /**
     * Constructor
     * @param hour 
     * @param LOG_SIZE
     * @param logPath 
     * @param encoding 
     */
    private void init(int hour, long logSize, String logPath, String enc) {
    	LOG_SIZE = logSize;
    	ENCODING = enc;    	
    	this.logInterval = (long)(hour*60*60*1000);
    	this.startTime = System.currentTimeMillis();
    	this.logName = logPath.replace("/", fs)+logSuffix;
    	this.logFile = new File(this.logName);
    	this.ps = System.out;
  		createLogFile(startTime);
    }       
    /**
     * Create log file
     * @param millis 
     */
    private void createLogFile(long millis) {
    	try {  		
    		if(this.logFile.exists()) {
    			if(log != null)
    				log.close();
    			if(this.logFile.length() > LOG_SIZE) {
    				File oldFile = new File(getLogFileName(millis));
    				//System.out.println("file : "+newFile.toString());
    				if(!this.logFile.renameTo(oldFile)) {
    					throw new IOException("Can't change log file name. BEFORE : "+this.logFile.getAbsolutePath());
    				}
    				this.logFile = new File(this.logName);
    			}
    		} else {
    			if(!this.logFile.createNewFile()) {
    				throw new IOException("Can't create new file : "+this.logFile.getAbsolutePath());
    			}
    		}
			this.isAppend = true;
    		log = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.logFile, this.isAppend), ENCODING), true);
    	} catch(IOException e) { 
    		e.printStackTrace();
		}    	
    }
    
    /**
     * Get log file name
     * @param millis
     * @return
     * @throws IOException 
     */
    public String getLogFileName(long millis) throws IOException {
    	SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");    	
    	String date = df.format(new Date());
    	String logPath = logFile.getAbsolutePath(); 
    	String path = logPath.substring(0, logPath.lastIndexOf(fs)+1);
    	String name = logName.substring(logName.lastIndexOf(fs)+1);
        return path+name+"_"+date+logSuffix;
    }
    
    /**
     * Compare 
     */
    private void compare() {
    	long currentMillis = System.currentTimeMillis();
    	long elapse = currentMillis-this.startTime;
    	if(elapse > this.logInterval || this.logFile.length() > LOG_SIZE) {
    		//System.out.println("elapse time : "+elapse+"   interval : "+this.logInterval+"  LogSize : "+this.logFile.length()+"  LOG_LIMIT : "+LOG_SIZE);
    		this.startTime = currentMillis;
   			createLogFile(this.startTime);
    	} 
    }    
    
    /**
     * Log info
     * @param msg
     */
    public void info(String msg) {
    	info(msg, true);
    }
    
    /**
     * Log info
     * @param msg 메시지
     * @param isSysOut 표준 입/출력 여부
     */
    public void info(String msg, boolean isSysOut) {
    	synchronized(log) {
            if(INFO_FILTER) {
                compare();
                StackTraceElement[] ele = (new Exception()).getStackTrace();
                String className = ele[ele.length-1].getClassName();
                className = className.substring(className.lastIndexOf(".")+1);
                int lineNumber = ele[ele.length-1].getLineNumber();
            	String msgStr = "[INFO]["+className+":"+lineNumber+"]"+alignString(msg);        	
                if(isSysOut) {
                	stdOut(msgStr);
                }
                log.println(msgStr);
            }
    	}
    }
    
    /**
     * Log debug
     * @param msg
     */
    public void debug(String msg) {
    	debug(msg, true);
    }

    /**
     * Log debug
     * @param msg
     * @param isSysOut
     */
    public void debug(String msg, boolean isSysOut) {
        synchronized (log) {
            if(DEBUG_FILTER) {
                compare();
                StackTraceElement[] ele = (new Exception()).getStackTrace();
                String className = ele[ele.length-1].getClassName();
                className = className.substring(className.lastIndexOf(".")+1);
                int lineNumber = ele[ele.length-1].getLineNumber();
            	String msgStr = "[DEBUG]["+className+":"+lineNumber+"]"+alignString(msg);        	
                if(isSysOut) {
                	stdOut(msgStr);
                }
                log.println(msgStr);
            }
    	}
    }    
    
    /**
     * Log error
     * @param msg
     */
    public void error(String msg) {
    	error(msg, true);
    }
    
    /**
     * Log error
     * @param msg
     * @param isSysOut
     */
    public void error(String msg, boolean isSysOut) {
        synchronized (log) {
            if(ERROR_FILTER) {
                compare();
                StackTraceElement[] ele = (new Exception()).getStackTrace(); 
                String className = ele[ele.length-1].getClassName();
                className = className.substring(className.lastIndexOf(".")+1);
                int lineNumber = ele[ele.length-1].getLineNumber();
            	String msgStr = "[ERROR]["+className+":"+lineNumber+"]"+alignString(msg);  
                if(isSysOut) {
                	stdOut(msgStr);
                }
            log.println(msgStr);
            }
    	}
    }    

    /**
     * Log fatal
     * @param msg
     */
    public void fatal(String msg) {
    	fatal(msg, true);
    }
    
    /**
     * Log fatal
     * @param msg
     * @param isSysOut
     */
    public void fatal(String msg, boolean isSysOut) {
        synchronized (log) {
            if(FATAL_FILTER) {
                compare();
                StackTraceElement[] ele = (new Exception()).getStackTrace();
                String className = ele[ele.length-1].getClassName();
                className = className.substring(className.lastIndexOf(".")+1);
                int lineNumber = ele[ele.length-1].getLineNumber();
            	String msgStr = "[FATAL]["+className+":"+lineNumber+"]"+alignString(msg);  
                if(isSysOut) {
                	stdOut(msgStr);
                }
            log.println(msgStr);
            }
    	}
    }    

    /**
     * Log throwable
     * @param e 
     */
    public void throwable(Throwable e) {
    	throwable(e, true);
    }
    
    /**
     * Log throwable
     * @param e
     * @param isSysout
     */
    public void throwable(Throwable e, boolean isSysout) {
        synchronized(log) {
        	if(THROWABLE_FILTER) {
                compare();
                if(e != null) {            	
                	StackTraceElement[] ele = (new Exception()).getStackTrace();
                    String className = ele[ele.length-1].getClassName();
                    className = className.substring(className.lastIndexOf(".")+1);
                    int lineNumber = ele[ele.length-1].getLineNumber();
                	String msgStr = "[THROWABLE]["+className+":"+lineNumber+"]"+alignString(e.toString()+" : "+e.getMessage());  
                  	if(isSysout) {
                  		stdOut(msgStr);
                  	}
               		log.println(msgStr);
                    StackTraceElement[] elements = e.getStackTrace();
                    for(int i=0; i<elements.length; i++) {
                    	String msg = "\tat "+elements[i].toString();
                       	if(isSysout) {
                       		stdOut(msg);
                       	}
                    	log.println(msg);
                    }
                }
        	}
    	}
    }
    
    /**
     * Log debug HEX code
     * @param bytes
     */
    public void debugHexCode(byte[] bytes) {
    	debugHexCode(bytes, true);
    }
    
	/**
	 * Log debug HEX code
	 * @param id
	 * @param bytes
	 */
    public void debugHexCode(byte[] bytes, boolean isSysOut) {
    	String hex = "";
        for(int i=0; i<bytes.length; i++)
        {
            String str = Integer.toHexString((new Byte(bytes[i])).intValue());
            hex += (str.length() > 2)?str.substring(str.length()-2)+" ":(str.length()==1)?"0"+str+" ":str+" ";
        }
        this.debug(hex, true);
    }
    
    /**
     * Log map
     * @param map
     */
    public void debugMap(Map<?, ?> map) {
    	this.debugMap(map, true);
    }
    
    /**
     * Log map
     * @param map
     * @param isSysOut
     */
    public void debugMap(Map<?, ?> map, boolean isSysOut) {
    	String str = "";
    	Iterator<?> iter = map.keySet().iterator();
    	while(iter.hasNext()) {
    		Object key = iter.next();
    		str += key + "=" + map.get(key)+System.getProperty("line.separator");
    	}
    	this.debug(str, true);
    }
    
    /**
     * Get aligned string
     * @param msg
     * @return
     */
    public String alignString(String msg) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(System.currentTimeMillis());
        String year = cal.get(Calendar.YEAR)+"";
        int m = cal.get(Calendar.MONTH)+1;
        String month = (m<10)?"0"+m:m+"";
        int d = cal.get(Calendar.DAY_OF_MONTH);
        String day = (d<10)?"0"+d:d+"";
        int h = cal.get(Calendar.HOUR_OF_DAY);
        String hour = (h<10)?"0"+h:h+"";
        int mi = cal.get(Calendar.MINUTE);
        String minute = (mi<10)?"0"+mi:mi+"";
        int s = cal.get(Calendar.SECOND);
        String second = (s<10)?"0"+s:s+"";

    	String msgStr = "["+ year+month+day+hour+minute+second + "] " + msg;
    	//int tab = alignCharNum - msgStr.length();
    	//for(int i=0; i<tab; i++)
    	//	msgStr += " ";
    	return msgStr;
    }
    
    /**
     * Print STD IO
     * @param str 스트링
     */
	public void stdOut(String str) {
		ps.println(str);
	}
	
	/**
	 * Set whether log info
	 * @param isInfo
	 */
	public void setInfo(boolean isInfo) {
		INFO_FILTER = isInfo;
	}
	
	/**
	 * Set whether log debug
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		DEBUG_FILTER = isDebug;
	}
	
	/**
	 * Set whether log error
	 * @param isError
	 */
	public void setError(boolean isError) {
		ERROR_FILTER = isError;
	}
	
	/**
	 * Set whether log fatal
	 * @param isFatal
	 */
	public void setFatal(boolean isFatal) {
		FATAL_FILTER = isFatal;
	}
	
	/**
	 * Set whether log throwable
	 * @param isThrowable
	 */
	public void setThrowable(boolean isThrowable) {
		THROWABLE_FILTER = isThrowable;
	}
	
	/**
	 * Get whether log info
	 * @return
	 */
	public boolean isInfo() {
		return INFO_FILTER;
	}
	
	/**
	 * Get whether log debug
	 * @return
	 */
	public boolean isDebug() {
		return DEBUG_FILTER;
	}
	
	/**
	 * Get whether log error
	 * @return
	 */
	public boolean isError() {
		return ERROR_FILTER;
	}
	
	/**
	 * Get whether log fatal
	 * @return
	 */
	public boolean isFatal() {
		return FATAL_FILTER;
	}
	
	/**
	 * Get whether log throwable
	 * @return
	 */
	public boolean isThrowable() {
		return THROWABLE_FILTER;
	}
}
