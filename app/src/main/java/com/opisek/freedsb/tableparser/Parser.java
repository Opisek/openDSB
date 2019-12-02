package com.opisek.freedsb.tableparser;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser {

    Document mDocument;

    List<String> mDocumentMetadata = new ArrayList<>();
    List<String> mTableTitles = new ArrayList<>();
    List<String> mColumnHeaders = new ArrayList<>();
    List<List<String>> mTableContent = new ArrayList<>();

    public Parser(File htmlFile) throws IOException {
        mDocument = Jsoup.parse(htmlFile, getEncoding(), "");
    }

    public static Parser getParserByContent(File htmlFile) throws IOException {
        // no parsers other than Untis.java yet, so just return a new instance of that one
        return new Untis(htmlFile);
    }

    public String getEncoding() {
        return StandardCharsets.UTF_8.name();
    }

    public String getTitle() {
        return mDocument.select("h2").first().text();
    }

    public List<String> getDocumentMetadata() {
        return mDocumentMetadata;
    }

    public int getTableCount() {
        return mTableContent.size();
    }

    public String getTableTitle(int tableIndex) {
        return mTableTitles.get(tableIndex);
    }

    public List<String> getColumnHeaders() {
        return mColumnHeaders;
    }

    public List<List<String>> getRows() {
        return mTableContent;
    }

}
