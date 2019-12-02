package com.opisek.freedsb.tableparser;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Untis extends Parser {

    private static final String TABLE_METADATA_CLASS = "mon_head";
    private static final String TABLE_TITLE_CLASS = "mon_title";
    private static final String TABLE_CLASS = "mon_list";

    Untis(File htmlFile) throws IOException {
        super(htmlFile);

        List<String> headers = new ArrayList<>();

        for (Element element : mDocument.select("table > tbody > tr > td > table > thead > tr").first().select("th")) {
            headers.add(element.text());
        }

        if (!headers.isEmpty()) {
            //headers.remove(1);
            headers.remove(4);
            headers.remove(4);
            headers.set(2,"Lehrer");
            headers.set(5,"Info");
            mColumnHeaders = headers;
        }

        List<List<String>> rows = new ArrayList<>();

        for (Element element : mDocument.select("table > tbody > tr > td > table > tbody").first().select("tr")) {
            List<String> cells = new ArrayList<>();
            int index = -1;
            for (Element e : element.select("td"))
            {
                index++;
                if (index == 0){
                    if (e.text().isEmpty()){
                        cells.add(rows.get(rows.size() - 1).get(0));
                    }
                    else {
                        cells.add(e.text());
                    }
                }
                else {
                    cells.add(e.text());
                }
            }
            if (cells.size() > 1) {
                rows.add(cells);
            }
        }

        List<List<String>> filteredResults = new ArrayList<>();

        for (List<String> e : rows) {
            //if (e.get(1).contains("10") && e.get(1).contains("B")) {
                //e.remove(1);
                e.remove(4);
                e.remove(4);
                filteredResults.add(e);
            //}
        }

        if (!rows.isEmpty()) {
            mTableContent = filteredResults;
            //mTableContent = rows;
        } else {
            mTableContent = new ArrayList<>();
        }
    }

    @Override
    public String getEncoding() {
        return StandardCharsets.ISO_8859_1.name();
    }
}
