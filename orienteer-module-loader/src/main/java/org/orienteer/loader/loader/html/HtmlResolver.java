package org.orienteer.loader.loader.html;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public abstract class HtmlResolver {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlResolver.class);

    private static final String PERVIOUS_DIR = "../";

    public static List<String> getFilesInDirectory(String url) {
        Document document = null;
        try {
            document = url.endsWith("/") ? getDocument(url) : null;
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return document != null ? getFilesInDirectory(document) : null;
    }

    public static List<String> getFilesInDirectory(URL url) {
        return getFilesInDirectory(url.toString());
    }

    public static List<String> getFilesInDirectory(Document document) {
        List<String> files = new ArrayList<>();
        Elements links = document.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("href");
            String linkText = link.text();
            if (!linkHref.equals(PERVIOUS_DIR)) {
                files.add(linkText);
            }
        }
        return !files.isEmpty() ? files : null;
    }

    public static boolean isDirectory(String url) {
        boolean isExists;
        try {
            isExists = url.endsWith("/") && getDocument(url) != null;
        } catch (IOException e) {
            isExists = false;
        }
        return isExists;
    }

    public static boolean isFile(URL url) {
        try {
            return isFile(url.toString());
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) e.printStackTrace();
        }
        return false;
    }

    public static boolean isFile(String url) throws IOException {
        boolean isExists;
        try {
            isExists = !url.endsWith("/") && getDocument(url) != null;
        } catch (UnsupportedMimeTypeException e) {
            isExists = true;
        }
        return isExists;
    }

    private static Document getDocument(String url) throws IOException {
        Connection connect = Jsoup.connect(url);
        try {
            Document document = connect.get();
            return document;
        } catch (HttpStatusException ex) {
//            if (LOG.isDebugEnabled()) ex.printStackTrace();
        }
        return null;
    }
}
