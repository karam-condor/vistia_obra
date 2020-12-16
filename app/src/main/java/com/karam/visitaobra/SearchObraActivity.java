package com.karam.visitaobra;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

public class SearchObraActivity extends AppCompatActivity {
    Spinner ufSpinner,cidadeSpinner;
    TextInputEditText cliente_editTxt;
    Button dtIncial_btn,dtFinal_btn;
    ArrayAdapter ufAdaptpter,cidadeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_obra);
        //set layout Controls
        setLayoutControls();
        //load uf spinner data
        //loadUfSpinnerData();
    }

    private void setLayoutControls() {
        cliente_editTxt = findViewById(R.id.search_obra_cliente_editTxt);
        dtIncial_btn = findViewById(R.id.search_obra_dtincial_btn);
        dtFinal_btn = findViewById(R.id.search_obra_dtfinal_btn);
        ufSpinner = findViewById(R.id.obra_uf_spinner);
        cidadeSpinner = findViewById(R.id.obra_cidade_spinner);
    }


    private void loadUfSpinnerData(){
        Estados_Cidades estados_cidades = new Estados_Cidades(this);
        Cursor c = estados_cidades.select(false,"estado",null,null,null,null,null,"id",null);
        if(c!= null && c.getCount() > 0){
            String[] ufs = new String[c.getCount()];
            c.moveToFirst();
            int counter =0;
            while(c!= null && !c.isAfterLast()){
                ufs[counter] = c.getString(c.getColumnIndex("nome"));
                counter++;
                c.moveToNext();
            }
            ufAdaptpter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ufs);
            ufSpinner.setAdapter(ufAdaptpter);
            ufSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadCidadeSpinnerData(String.valueOf(position+1));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void loadCidadeSpinnerData(String ufID){
        Estados_Cidades estados_cidades = new Estados_Cidades(this);
        Cursor c = estados_cidades.select(false,"cidade",new String[]{"nome"},"estado = ?",new String[]{ufID},null,null,"id",null);
        if(c!= null && c.getCount() > 0){
            String[] cidades = new String[c.getCount()];
            c.moveToFirst();
            int counter =0;
            while(c!= null && !c.isAfterLast()){
                cidades[counter] = c.getString(c.getColumnIndex("nome"));
                counter++;
                c.moveToNext();
            }
            cidadeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cidades);
            cidadeSpinner.setAdapter(cidadeAdapter);
        }
    }

}