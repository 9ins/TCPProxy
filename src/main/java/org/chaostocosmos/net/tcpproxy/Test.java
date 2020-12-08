package org.chaostocosmos.net.tcpproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

	class Node {
	    int num;
	    int depth;
	    java.util.List<Node> childs;
	    public Node(int num, java.util.List<Node> childs, int depth) {
	        this.num = num;
	        this.childs = childs;
	        this.depth = depth;
	    }             
	    
	    public String  toString() {
	        return "num : "+num+"   childs: "+childs+"   depth: "+depth;
	    }
	}

	public class Test {
    public int solution(int n, int[][] edge) throws InterruptedException {
        int answer = 0;
        Node root = new Node(1, new ArrayList<>(), 0);
        List<int[]> edgeList = Arrays.stream(edge).map(a -> a).collect(Collectors.toList());
        search(root, edgeList);
        
        print(root, 0);
        
        System.out.println(root.childs.get(1));
        return answer;
    }
    
    public void print(Node node, int depth) {
    	depth++;
    	for(int i =0; i<depth; i++) 
    		System.out.print(" ");
    	System.out.println("Node: "+node.num+"  depth: "+depth);
    	for(Node n : node.childs) {
    		print(n, depth);
    	}
    }

    public void search(Node node, List<int[]> edge) {
        //System.out.println("recursive.....parent : "+node);
        //edge.stream().forEach(a -> System.out.print(Arrays.toString(a)+" "));
//        try {
//            Thread.sleep(100);
//           } catch (Exception e) {
//        }
        List<int[]> childs = edge.stream().filter(a -> a[0] == node.num || a[1] == node.num).collect(Collectors.toList());
        childs.stream().forEach(a -> System.out.print(Arrays.toString(a)+" "));
        System.out.println();
        
        for(int i=0; i<childs.size(); i++) {
        	int[] arr = childs.get(i);
        	System.out.println(Arrays.toString(arr));
            int num = arr[0];
            int child = arr[1];
            int nNum = node.num == num ? child : node.num == child ? num : 0;
            if(nNum != 0) {
	            Node n = new Node(nNum, new ArrayList<Node>(), node.depth+1);
	            System.out.println("add. array: "+nNum+"   depth: "+n.depth+"   ");
	           	node.childs.add(n);
	           	edge.remove(arr);
	          	search(n, edge.stream().filter(a -> a[0] == n.num || a[1] == n.num).collect(Collectors.toList()));
            }            
        }
    }

    public Point calculateIntersectionPoint(double m1, double b1, double m2, double b2) {    
        if (m1 == m2) {
            return null;
        }    
        double x = (b2 - b1) / (m1 - m2);
        double y = m1 * x + b1;
    
        return new Point(x, y);
    }

    class Point {
        double x, y;
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }        
    }
     
    public static void main(String[] args) throws InterruptedException {

        /*
        Test test = new Test();
        int[][] edge = {
            {3, 6},
            {4, 3},
            {3, 2},
            {1, 3},
            {1, 2},
            {2, 4},
            {5, 2},
        };
        int num = test.solution(6, edge);
        System.out.println("count: "+num);
        */
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", null);
        list.add(map);

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("2", 4);

        boolean is = list.contains(map1);
        System.out.println(is);
    }
}


