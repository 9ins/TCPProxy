package org.chaostocosmos.net.tcpproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Solution {

    
    class Slope {
        int ski;
        int board;
    }

    public static  int solution2(int n, int m, int k) {
        int answer = -1;

        List<Boolean> allcase = new ArrayList<Boolean>();
        for(int i=0; i<n; i++) {
            for(int j=0; j<m; j++) {
                //allcase.add
            }
        }
        return answer;
    }

    

    public static int solution1(int[][] part_times) {
        List<Integer> list = new ArrayList<Integer>();
        Arrays.sort(part_times, (a, b) -> Integer.compare(a[1], b[1]));
        for(int i=0; i< part_times.length; i++) {
            int[] row = part_times[i];            
            int money = row[2];
            int start = row[0];
            int end = row[1];
            for(int j=0; j<part_times.length; j++) {
                if(j != i && start <= part_times[j][1] && end <= part_times[j][0]) {
                    money += part_times[j][2];
                    start = part_times[j][0];
                    end = part_times[j][1];
                }                
            }
            list.add(money);
        }
        return Collections.max(list);
    }

    public static void main(String[] args) {
        int num = solution2(3, 8, 4);

        int[][] times = {{3, 6, 3}, {2, 4, 2}, {10, 12, 8}, {11, 15, 5}, {1, 8, 10}, {12, 13, 1}};
        int money = solution1(times);
        System.out.println(money);
    }
}
