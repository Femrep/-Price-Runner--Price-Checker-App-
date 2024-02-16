package com.example.mytab;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private String scannedResult;

    public void setScannedResult(String result) {
        scannedResult = result;
    }

    public String getScannedResult() {
        return scannedResult;
    }
}
