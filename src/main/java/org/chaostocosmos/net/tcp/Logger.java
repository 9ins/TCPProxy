package org.chaostocosmos.net.tcp;


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
 * Logger : 로거
 * Description : 
 * 각종 어플리케이션에서 사용할 수 있는 로그정보를 관리하는 클래스
 *  
 * Modification Information  
 *  ---------   ---------   -------------------------------
 *  20161118	9ins		최초작성
 *  
 * @author 9ins
 * @since 20161118
 * @version 1.0
 */
public class Logger {
	/**
	 * 정렬 문자수
	 */
	public static final int alignCharNum = 180;	
	/**
	 * 정보 필터
	 */
	public static boolean INFO_FILTER = true;	
	/**
	 * 디버그 필터
	 */
	public static boolean DEBUG_FILTER = true;	
	/**
	 * 에러 필터
	 */
	public static boolean ERROR_FILTER = true;	
	/**
	 * 치명적 에러 필터 
	 */
	public static boolean FATAL_FILTER = true;	
	/**
	 * 예외 필터
	 */
	public static boolean THROWABLE_FILTER = true;
	/**
	 * 로그 크기(default : 100MB)
	 */
	public static long LOG_SIZE = 1024*1024*100;
	/**
	 * 로그 엔코딩 타입(default : UTF-8)
	 */
	public static String ENCODING = "UTF-8";
	/**
	 * 로그 시간간격(default : 24시간)
	 */
	public static int LOG_HOUR = 24;
	/**
	 * 어팬드 여부
	 */
	private boolean isAppend;
	/**
	 * 프린트쓰기 객체
	 */
    private PrintWriter log;    
    /**
     * 표준 입/출력 객체
     */
    private PrintStream ps;    
   /**
     * 로그 시작 시간
     */
    private long startTime;    
    /**
     * 로그 기록 간격
     */
    private long logInterval;    
    /**
     * 시스템 파일 분리자
     */
    private String fs = System.getProperty("file.separator");    
    /**
     * 로그 접미부
     */
    private String logSuffix = ".log";    
    /**
     * 로그 파일 객체
     */
    private File logFile;    
    /**
     * 로그 이름
     */
    private String logName;    
    /**
     * 로거 객체 
     */
    private static Logger logger;    
    /**
     * 로거 인스턴스를 얻는다.
     * @return 로거 인스턴스
     */
    public static Logger getInstance() {
    	if(logger == null) {
    		logger = new Logger(LOG_HOUR, LOG_SIZE, "main");
    	}
    	return logger;
    }    
    /**
     * 로거 인스턴스를 얻는다.
     * @param hour 로그 간격(시간)
     * @return 로거 인스턴스
     */
    public static Logger getInstance(int hour) {
    	return getInstance(hour, LOG_SIZE, "main");
    }    
    /**
     * 로거 인스턴스를 얻는다.
     * @param logPath 로그 경로
     * @return 로거 인스턴스
     */
    public static Logger getInstance(long logSize) {
    	return getInstance(LOG_HOUR, logSize, "main");
    }    
    /**
     * 로거 인스턴스를 얻는다.
     * @param hour 로그 간격(시간)
     * @param logSize 로그 사이즈
     * @return 로거 인스턴스
     */
    public static Logger getInstance(int hour, long logSize) {
    	return getInstance(hour, logSize, "main");
    }
    /**
     * 로거 인스턴스를 얻는다.
     * @param hour 로그 간격
     * @param logSize 로그 크기
     * @param logPath 로그 경로
     * @return 로거 인스턴스
     */
    public static Logger getInstance(int hour, long logSize, String logPath) {
    	return getInstance(hour, logSize, logPath, ENCODING);
    }    
    /**
     * 로거 인스턴스를 얻는다.
     * @param hour 로그 간격
     * @param logPath 로그 경로
     * @param encoding 엔코딩 타입
     * @return 로거 인스턴스 
     */
    public static Logger getInstance(int hour, long logSize, String logPath, String encoding) {
    	if(logger == null)
    		logger = new Logger(hour, logSize, logPath, encoding);
    	return logger;    	
    }    
    /**
     * 로거 생성자
     * @param hour 로그 생성 간격(시간 단위)
     * @param logSize 로그파일 사이즈
     * @param logPath 로그 경로
     * @param ENCODING 문자 엔코딩 방법
     */
    private Logger(int hour, long logSize, String logPath) {
    	this(hour, logSize, logPath, ENCODING);
    }    
    /**
     * 로거 생성자
     * @param period 로그생성간격(밀리초 단위)
     * @param logSize 로그파일 사이즈
     * @param logPath 로그 경로
     * @param encoding 문자 엔코딩 방법
     */
    private Logger(int hour, long logSize, String logPath, String encoding) {
    	init(hour, logSize, logPath, encoding);
    }    
    /**
     * 로거를 초기화 한다.
     * @param hour 간격
     * @param LOG_SIZE 로그 크기
     * @param logPath 로그경로
     * @param encoding 문자 엔코딩
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
     * 로그 파일을 생성한다.
     * @param millis 현재 시간(밀리초)
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
     * 로그 파일 이름을 주어진 밀리초의 이름으로 얻는다.
     * @param millis 밀리초
     * @return 로그 파일 이름
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
     * 로그 간격 시간이 됬는지 비교한다.
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
     * 정보 메시지를 로그 기록 한다.
     * @param msg 메시지
     */
    public void info(String msg) {
    	info(msg, true);
    }
    
    /**
     * 정보 메시지를 로그 기록한다.
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
     * 디버그 메시지를 로그 기록 하고 계행한다.
     * @param msg 메시지
     */
    public void debug(String msg) {
    	debug(msg, true);
    }

    /**
     * 디버그 메시지를 로그 기록 하고 계행한다.
     * @param msg 메시지
     * @param isSysOut 표준 입/출력 여부
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
     * 에러 메시지를 로그에 기록한다.
     * @param msg
     */
    public void error(String msg) {
    	error(msg, true);
    }
    
    /**
     * 에러 메시지를 로그에 기록한다.
     * @param msg 메시지
     * @param isSysOut 표준 입/출력 여부
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
     * 치명적 메러 메시지를 로그에 기록한다.
     * @param msg
     */
    public void fatal(String msg) {
    	fatal(msg, true);
    }
    
    /**
     * 치명적 에러 메시지를 로그에 기록한다.
     * @param msg 메시지
     * @param isSysOut 표준 입/출력 여부
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
     * 해당 예외를 로그 기록 한다.
     * @param e 예외 객체
     */
    public void throwable(Throwable e) {
    	throwable(e, true);
    }
    
    /**
     * 해당 예외를 로그 기록 한다.
     * @param e 예외 객체
     * @param isSysout 표준 입/출력 여부
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
     * 16진수 코드를 로그에 출력한다.
     * @param bytes
     */
    public void debugHexCode(byte[] bytes) {
    	debugHexCode(bytes, true);
    }
    
	/**
	 * 16진수 코드를 로그에 출력한다.
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
     * Map을 로그에 출력한다.
     * @param map
     */
    public void debugMap(Map<?, ?> map) {
    	this.debugMap(map, true);
    }
    
    /**
     * Map을 로그에 출력한다.
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
     * 문자 정렬된 스트링을 얻는다.
     * @param msg 스트링
     * @return 스트링
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
     * 스트링을 표준 입/출력으로 출력한다.
     * @param str 스트링
     */
	public void stdOut(String str) {
		ps.println(str);
	}
	
	/**
	 * 인포 출력여부를 설정한다.
	 * @param isInfo
	 */
	public void setInfo(boolean isInfo) {
		INFO_FILTER = isInfo;
	}
	
	/**
	 * 디버그 출력여부를 설정한다.
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		DEBUG_FILTER = isDebug;
	}
	
	/**
	 * 에러 출력여부를 설정한다.
	 * @param isError
	 */
	public void setError(boolean isError) {
		ERROR_FILTER = isError;
	}
	
	/**
	 * 치명적 에러 출력여부를 설정한다.
	 * @param isFatal
	 */
	public void setFatal(boolean isFatal) {
		FATAL_FILTER = isFatal;
	}
	
	/**
	 * 예외 출력여부를 설정한다.
	 * @param isThrowable
	 */
	public void setThrowable(boolean isThrowable) {
		THROWABLE_FILTER = isThrowable;
	}
	
	/**
	 * 인포 여부를 얻느다.
	 * @return
	 */
	public boolean isInfo() {
		return INFO_FILTER;
	}
	
	/**
	 * 디버그 여부를 얻는다.
	 * @return
	 */
	public boolean isDebug() {
		return DEBUG_FILTER;
	}
	
	/**
	 * 에러 출력여부를 얻는다.
	 * @return
	 */
	public boolean isError() {
		return ERROR_FILTER;
	}
	
	/**
	 * 치명적 에러 출력여부를 얻는다.
	 * @return
	 */
	public boolean isFatal() {
		return FATAL_FILTER;
	}
	
	/**
	 * 예외 기록 여부를 얻는다.
	 * @return
	 */
	public boolean isThrowable() {
		return THROWABLE_FILTER;
	}
}
