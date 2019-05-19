package com.example.febi.tes_kaspi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import handle_db.HttpHandler;

public class getPovinsi extends AsyncTask<Void, Void, Void> {
    public Activity activity;
    public ProgressDialog pDialog;
    public Context context;
    public Spinner spinProvinsi;
    public String TAG = MainActivity.class.getSimpleName();
    public ArrayList<HashMap<String, String>> posList;
    public HashMap posID;
    public String item[];
    public String url_provinsi = "https://kodepos-2d475.firebaseio.com/list_propinsi.json";

    public getPovinsi(Context context, Spinner spinProvinsi, Activity activity, HashMap posID) {
        this.context = context;
        this.spinProvinsi = spinProvinsi;
        this.activity = activity;
        this.posID = posID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        posList = new ArrayList<>();
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url_provinsi);
        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                Iterator<String> iter = jsonObj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObj.get(key);
                        String nama = value.toString();
                        HashMap<String, String> pos = new HashMap<>();
                        pos.put("name", nama);
                        pos.put("idp", key);
                        posList.add(pos);

                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                "Data tidak ada.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,
                            "Terjadi kesalahan coba lain waktu.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (pDialog.isShowing())
            pDialog.dismiss();
        Collections.sort(posList, new Comparator<HashMap<String, String>>() {

            @Override
            public int compare(HashMap<String, String> lhs,
                               HashMap<String, String> rhs) {
                return lhs.get("name").compareTo(rhs.get("name"));
            }
        });

        item = new String[posList.size()];
        for (int i = 0; i < posList.size(); i++) {
            item[i] = posList.get(i).get("name");
            posID.put(posList.get(i).get("name"), posList.get(i).get("idp"));
            Log.e(TAG, "name: " + posID.get(posList.get(i).get("name")).toString());
        }

        ArrayAdapter<String> adapterProvinsi =
                new ArrayAdapter<String>(context,
                        R.layout.support_simple_spinner_dropdown_item, item);
        spinProvinsi.setAdapter(adapterProvinsi);
    }
}
