package com.example.febi.tes_kaspi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinKota, SpinProvinsi;
    private EditText edtKelurahan;
    private HashMap posID = new HashMap();
    private HashMap posID_Kode = new HashMap();
    public getPovinsi gp;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinKota = findViewById(R.id.kota);
        SpinProvinsi = findViewById(R.id.provinsi);
        edtKelurahan = findViewById(R.id.daerah);
        lv = findViewById(R.id.list_view);
        gp = new getPovinsi(MainActivity.this, SpinProvinsi, getParent(), posID);
        gp.execute();
        spinKota.setOnItemSelectedListener(this);
        SpinProvinsi.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.provinsi) {
            posID_Kode = new HashMap();
            getKota gk = new getKota(MainActivity.this, spinKota, getParent(),
                    posID.get(SpinProvinsi.getSelectedItem().toString()).toString(), posID_Kode);
            gk.execute();
        } else if (parent.getId() == R.id.kota) {
            getKodePos gkp = new getKodePos(MainActivity.this,
                    edtKelurahan, getParent(),
                    posID_Kode.get(spinKota.getSelectedItem().toString()).toString(), lv);
            gkp.execute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
