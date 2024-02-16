package com.example.mytab;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tab2 extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView header;
    EditText editText2;
    Button postButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        header = view.findViewById(R.id.header);
        postButton = view.findViewById(R.id.postButton);
        editText2 = view.findViewById(R.id.editText2);

        editText2.setText(sharedViewModel.getScannedResult());

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = editText2.getText().toString();
                makeNetworkRequest(barcode);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        editText2.setText(sharedViewModel.getScannedResult());
        // Diğer işlemler buraya eklenebilir
    }

    private void makeNetworkRequest(String barcode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"q\":\"" + barcode + "\",\"gl\":\"tr\",\"hl\":\"tr\"}");

                Request request = new Request.Builder()
                        .url("https://google.serper.dev/search")
                        .method("POST", body)
                        .addHeader("X-API-KEY", "632cf2a7a0fb081ba2086fe5069693fdaf5bab16")
                        .addHeader("Content-Type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        updateUI(responseData);
                    } else {
                        Log.e("NetworkRequest", "Response not successful");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("NetworkRequest", "Error: " + e.getMessage());
                }
            }
        }).start();
    }

    private void updateUI(String responseData) {
        try {
            JSONObject jsonResponse = new JSONObject(responseData);
            JSONArray organics = jsonResponse.getJSONArray("organic");

            List<JSONObject> organicList = new ArrayList<>();

            for (int i = 0; i < organics.length(); i++) {
                JSONObject organic = organics.getJSONObject(i);

                if (organic.has("currency") && organic.has("price")) {
                    organicList.add(organic);
                }
            }

            Collections.sort(organicList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    double price1 = o1.optDouble("price");
                    double price2 = o2.optDouble("price");
                    return Double.compare(price1, price2);
                }
            });

            StringBuilder resultBuilder = new StringBuilder();

            for (JSONObject organic : organicList) {
                String title = organic.optString("title");
                String link = organic.optString("link");
                String currency = organic.optString("currency");
                double price = organic.optDouble("price");

                resultBuilder.append("<a href=\"").append(link).append("\">").append(title).append("</a><br>");
                resultBuilder.append("Price: ").append(price).append(currency).append("<br><br>");

            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    header.setText(Html.fromHtml(resultBuilder.toString()));
                    header.setMovementMethod(LinkMovementMethod.getInstance());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            header.setText("Error: " + e.getMessage());
        }
    }


    public void setSharedViewModel(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }

    public SharedViewModel getSharedViewModel() {
        return sharedViewModel;
    }
}
