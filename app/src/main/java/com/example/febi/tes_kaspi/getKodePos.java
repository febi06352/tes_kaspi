package com.example.febi.tes_kaspi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import handle_db.HttpHandler;

public class getKodePos extends AsyncTask<Void, Void, Void> implements TextWatcher {
    public Activity activity;
    public ProgressDialog pDialog;
    private String id;
    public Context context;
    public EditText edtKelurahan;
    public ListView lv;
    public String TAG = MainActivity.class.getSimpleName();
    public ArrayList<HashMap<String, String>> posList;
    public String url_kelurahan;
    public ArrayList<HashMap<String, String>> tmpList;

    public getKodePos(Context context, EditText edtKelurahan,
                      Activity activity, String id, ListView lv) {
        this.context = context;
        this.edtKelurahan = edtKelurahan;
        this.activity = activity;
        this.id = id;
        this.lv = lv;
        edtKelurahan.addTextChangedListener(this);
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
        url_kelurahan = " https://kodepos-2d475.firebaseio.com/kota_kab/" + id + ".json";
        posList = new ArrayList<>();
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(url_kelurahan);

        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONArray data = new JSONArray(jsonStr);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);

                    String kecamatan = c.getString("kecamatan");
                    String kelurahan = c.getString("kelurahan");
                    String kodepos = c.getString("kodepos");

                    HashMap<String, String> pos = new HashMap<>();

                    pos.put("kecamatan", kecamatan);
                    pos.put("kelurahan", kelurahan);
                    pos.put("kodepos", kodepos);

                    posList.add(pos);
                }
            } catch (final JSONException e) {
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
        ListAdapter adapter = new SimpleAdapter(
                context, posList, R.layout.kode_pos,
                new String[]{"kecamatan", "kelurahan", "kodepos"},
                new int[]{R.id.kecamatan, R.id.kelurahan, R.id.kodepos});

        lv.setAdapter(adapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String title = edtKelurahan.getText().toString().trim().toLowerCase();
        if(!TextUtils.isEmpty(title)) {
            tmpList = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                String kecamatan = posList.get(i).get("kecamatan").toLowerCase();
                String kelurahan = posList.get(i).get("kelurahan").toLowerCase();
                if (kecamatan.contains(title) || kelurahan.contains(title))  {
                    tmpList.add(posList.get(i));
                }
            }
            ListAdapter adapter = new SimpleAdapter(
                    context, tmpList,
                    R.layout.kode_pos, new String[]{"kecamatan", "kelurahan", "kodepos"},
                    new int[]{R.id.kecamatan, R.id.kelurahan, R.id.kodepos});

            lv.setAdapter(adapter);
        } else {
            ListAdapter adapter = new SimpleAdapter(
                    context, posList,
                    R.layout.kode_pos, new String[]{"kecamatan", "kelurahan", "kodepos"},
                    new int[]{R.id.kecamatan, R.id.kelurahan, R.id.kodepos});
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
