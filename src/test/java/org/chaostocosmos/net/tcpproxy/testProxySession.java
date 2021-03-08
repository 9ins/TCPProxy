package org.chaostocosmos.net.tcpproxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.exception.OutOfRangeException;

class testProxySession {

    	/**
	 * Get remote host index by ratio list
	 * @param ratioList
	 * @return
	 */
	public static int getRandomRatioIndex(List<Float> ratioList) {
		Random random = new Random();
		int sum = ratioList.stream().reduce((a, b) -> Float.sum(a, b)).get().intValue();
		int start = 0;
		int bound = 0;
		int ran = random.nextInt(sum);			
		for(int i=0; i<ratioList.size(); i++) {
			int ratio = ratioList.get(i).intValue();
			bound += ratio;			
			if(ran >= start && ran < bound) {
				//System.out.println("VAl: "+ran+" Start: "+start+"   End : "+bound);
				return i;
			}
			start += ratio;
		}
		throw new OutOfRangeException(ran, 0, sum);
    }
    
    public static void main(String[] args) {
		String ratio = "10:10:10";
		List<Float> ratioList = Arrays.asList(ratio.split(":")).stream().map(s -> Float.valueOf(s)).collect(Collectors.toList());
		Map<String, Integer> map = new HashMap<String, Integer>();		
		map.put(0+"", 0);
		map.put(1+"", 0);
		map.put(2+"", 0);
		for(int i=0; i<10000; i++) {
			int idx = getRandomRatioIndex(ratioList);
			map.put(idx+"", map.get(idx+"")+1);
		}
		System.out.println(map.toString());
	}
}