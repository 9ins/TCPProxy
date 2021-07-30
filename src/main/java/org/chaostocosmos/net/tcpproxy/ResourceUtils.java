package org.chaostocosmos.net.tcpproxy;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 
 * ResourceUtils
 *
 * @author Kooin-Shin
 * 2020. 9. 2.
 */
public class ResourceUtils {
	
    public static URI findClassLoaderResource(String resourceName) throws URISyntaxException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader().getSystemClassLoader();
        URL f = classLoader.getResource(resourceName);
        if (f == null) {
            throw new RuntimeException("Unable to find " + resourceName);
        }
        return f.toURI();
    }
}
