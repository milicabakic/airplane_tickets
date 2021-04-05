package app.utils;

import java.util.Map;
import java.util.Map.Entry;

public class Utils {

	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	public static void parseAllRoutes(Map<String, String> map, String allRoutes) {
		String output = allRoutes.replace("{", "");
		output = output.replace("}", "");
		output = output.replace("\"", "");
		String[] sections = output.split(",");				
		for(int i=0; i<sections.length; i++) {
			String[] parts = sections[i].split(":");
			map.put(parts[0].substring(0, parts[0].length()-2), parts[1]);
		}
	}

}
