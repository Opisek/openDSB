package com.opisek.freedsb.viewmodels;

import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.opisek.freedsb.Application;
import com.opisek.freedsb.database.Node;
import com.opisek.freedsb.network.download.NodeDownloader;
import com.opisek.freedsb.tableparser.Parser;
import com.opisek.freedsb.utils.LogUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableviewViewModel extends ViewModel implements NodeDownloader.DownloadEventListener {

    private final String TAG = getClass().getCanonicalName();

    private MutableLiveData<String> mTitle = new MutableLiveData<>();
    private MutableLiveData<List<String>> mColumnHeaders = new MutableLiveData<>();
    private MutableLiveData<List<List<String>>> mTableContent = new MutableLiveData<>();

    private final MutableLiveData<Integer> mProgress = new MutableLiveData<>();
    private final MutableLiveData<Exception> mException = new MutableLiveData<>();

    private Node mNode;

    public TableviewViewModel() {
        super();
    }

    public Date getDate() {
        if (mNode == null) {
            return null;
        }
        return mNode.mDate;
    }

    public LiveData<String> getTitle() {
        return mTitle;
    }

    public LiveData<List<String>> getColumnHeaders() {
        return mColumnHeaders;
    }

    public LiveData<List<List<String>>> getTableContent() {
        return mTableContent;
    }

    public void loadTable(Node node) {
        mNode = node;
        new NodeDownloader(mNode, this, NodeDownloader.GET_CONTENT);
    }

    @Override
    public void onProgress(int progress) {
        mProgress.setValue(progress);
    }

    @Override
    public void onException(Exception exception) {
        mException.setValue(exception);
    }

    @Override
    public void onResult(Node node, NodeDownloader.Result result) {
        if (result != NodeDownloader.Result.FAIL) {
            Parser parser;
            try {
                parser = Parser.getParserByContent(node.getContentCache());
            } catch (IOException e) {
                Log.e(TAG, LogUtils.getStackTrace(e));
                return;
            }
            int tableCount = parser.getTableCount();
            if (tableCount >= 0) {

                List<List<String>> rows = parser.getRows();
                List<List<String>> filteredRows = new ArrayList<>();

                boolean toBeFiltered = Application.getInstance().getClassFiltering();

                String classToFilter = Application.getInstance().getClassToFilter();

                if (classToFilter == null) classToFilter = "";

                String num;
                char letter;

                if (classToFilter.length() > 3 || classToFilter.length() < 2) toBeFiltered = false;

                if (classToFilter.length() == 2)
                {
                    letter = classToFilter.charAt(1);
                    num = String.valueOf(classToFilter.charAt(0));
                } else {
                    letter = classToFilter.charAt(0) == 'Q' ? 'Q' : classToFilter.charAt(2);
                    num = letter == 'Q' ? classToFilter.substring(1, 2) : classToFilter.substring(0, 1);
                }

                Log.println(2, "num", num);
                Log.println(2, "letter", String.valueOf(letter));

                if (toBeFiltered) {
                    for (List<String> row : rows) {
                        if ((row.get(1).contains(num) && row.get(1).contains(String.valueOf(letter))))
                            filteredRows.add(row);
                    }
                } else {
                    filteredRows = rows;
                }
                for (int i = filteredRows.size() - 1; i > 0; i--)
                {
                    if (filteredRows.get(i).get(0) == filteredRows.get(i - 1).get(0)) filteredRows.get(i).set(0, "");
                }


                mTitle.setValue(parser.getTitle());
                mColumnHeaders.setValue(parser.getColumnHeaders());
                mTableContent.setValue(filteredRows);
            }
        }
    }
}
