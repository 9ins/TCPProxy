package org.chaostocosmos.net.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
/**
 * 프로퍼티 핸들러 객체
 * @author 9ins
 * @since 2017. 8. 17.
 */
public class PropertyHandler {
	Properties prop;	
	File file;
	BufferedReader reader;
	/**
	 * 생성자
	 * @param path
	 * @throws IOException
	 */
	public PropertyHandler(String path) throws IOException {
		this(new File(path));
	}
	/**
	 * 생성자
	 * @param file
	 * @throws IOException
	 */
	public PropertyHandler(File file) throws IOException {
		this.file = file;
		this.prop = new Properties();
		this.reader = new BufferedReader(new FileReader(this.file));
		reload();
	}
	/**
	 * 프로퍼티를 재로딩 한다.
	 * @throws IOException
	 */
	public void reload() throws IOException {
		this.prop.clear();
		//System.out.println(this.reader.markSupported());
		if(this.file.length() > 0) {
			this.reader.mark((int)(this.file.length()*3));
		}
		String line = null;
		while((line=this.reader.readLine()) != null) {
			if(line.indexOf("#") != -1) {
				line = line.substring(0, line.indexOf("#"));
			}
			String[] vals = line.split("=");
			if(vals.length == 2 && !vals[0].trim().equals("")) {
				this.prop.put(vals[0].trim(), vals[1].trim());
			} else if(vals.length == 1 && !vals[0].trim().equals("")) {
				this.prop.put(vals[0], "");
			}
		}
		if(this.file.length() > 0) {
			this.reader.reset();
		}
	}
	/**
	 * 키로 프로퍼티 값을 얻는다.
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		return this.prop.getProperty(key);
	}
	/**
	 * 인자로 주어진 키를 유형검색하여 목록으로 얻는다.
	 * @param likeKey
	 * @return
	 */
	public List<String> getLike(String likeKey) {
		Enumeration<Object> enu = this.prop.keys();
		List<String> list = new ArrayList<String>();
		while(enu.hasMoreElements()) {
			String key = enu.nextElement().toString();
			if(key.contains(likeKey)) {
				list.add(this.prop.getProperty(key));
			}
		}
		return list;
	}
	/**
	 * 프로퍼티 객체를 얻는다.
	 * @return
	 */
	public Properties getProperties() {
		return this.prop;
	}
	/**
	 * 프로퍼티 핸들러를 닫는다.
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.reader.close();
	}
	/**
	 * 인자로 주어진 프로퍼티 객체를 인자로 주어진 키값으로 유형검색하여 목록으로 얻는다.
	 * @param prop
	 * @param likeKey
	 * @return
	 */
	public static List<String> getLike(Properties prop, String likeKey) {
		Enumeration<Object> enu = prop.keys();
		List<String> list = new ArrayList<String>();
		while(enu.hasMoreElements()) {
			String key = enu.nextElement().toString();
			if(key.contains(likeKey)) {
				list.add(prop.getProperty(key));
			}
		}
		return list;
	}
	/**
	 * 인자로 주어진 프로퍼티 객체를 인자로 주어진 구분자로 만들어진 스트링을 얻는다.
	 * @param prop
	 * @param delim
	 * @return
	 */
	public static String getPropertyToString(Properties prop, String delim) {
		String str = "";
		Enumeration<Object> enu = prop.keys();
		while(enu.hasMoreElements()) {
			Object key = enu.nextElement();
			str += key+"="+prop.get(key)+delim;
		}
		str = str.substring(0, str.length()-delim.length());
		return str;
	}
	/**
	 * 프로퍼티 스트링을 인자로 주어진 구분자로 프로퍼티 객체를 얻는다.
	 * @param propertyString
	 * @param delim
	 * @return
	 */
	public static Properties getStringToProperties(String propertyString, String delim) {
		Properties properties = new Properties();
		String[] pairArr = propertyString.split(delim);
		for(String pair : pairArr) {
			String[] keyVal = pair.split("=");
			if(keyVal.length == 2) {
				properties.put(keyVal[0].trim(), keyVal[1].trim());
			}
		}
		return properties;
	}
	/**
	 * 프로퍼티를 저장한다.
	 * @param properties
	 * @param saveFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveProperties(Properties properties, File saveFile, String charset) throws FileNotFoundException, IOException {
		if(saveFile.exists()) {
			saveFile.delete();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), charset));
		Enumeration enu = properties.keys();
		while(enu.hasMoreElements()) {
			String key = enu.nextElement()+"";
			String value = properties.getProperty(key);
			String line = key+" = "+value+System.getProperty("line.separator");
			bw.write(line);
		}
		bw.close();
	}
}
