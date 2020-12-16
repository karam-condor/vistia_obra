package com.karam.visitaobra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObraActivity extends AppCompatActivity implements TaskListener{
    String codusu,nomeusu,cnpj,id_obra;
    ArrayList<HashMap<String,String>> cidades;
    Spinner ufSpinner,cidadeSpinner;
    TextInputEditText cliente_editTxt,empren_editTxt,bairro_editTxt,end_EditTxt,amox_EditTxt,eng_EditTxt,tele1_EditTxt,tele2_EditTxt,tele3_EditTxt;
    boolean isNew;
    LinearLayout obra_linear_layout;
    ArrayAdapter<String> ufAdaptpter,cidadeAdapter;
    VisitaAdapter visitaAdapter;
    ExpandableListView visitaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obra);
        //set codusu,nomeusu,cnpj
        setSh();
        //set cnpj on app bar
        getSupportActionBar().setTitle("CNPJ: " + cnpj);
        //set layout Controls
        setLayoutControls();
        //load uf spinner data
        loadUfSpinnerData();
        //check if new
        checkIfNew();
        //load obra info
        loadObra();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.obra_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.obra_gravar_menu:

                return true;
            case R.id.obra_fotos_menu:

                return true;
            case R.id.obra_finalizar_menu:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setLayoutControls() {
        cliente_editTxt = findViewById(R.id.obra_cliente_editTxt);
        empren_editTxt = findViewById(R.id.obra_emprendedor_editTxt);
        bairro_editTxt = findViewById(R.id.obra_bairro_editTxt);
        end_EditTxt = findViewById(R.id.obra_endereco_editTxt);
        amox_EditTxt = findViewById(R.id.obra_amox_editTxt);
        eng_EditTxt = findViewById(R.id.obra_engenherio_editTxt);
        tele1_EditTxt = findViewById(R.id.obra_telefone1_editTxt);
        tele2_EditTxt = findViewById(R.id.obra_telefone2_editTxt);
        tele3_EditTxt = findViewById(R.id.obra_telefone3_editTxt);
        ufSpinner = findViewById(R.id.obra_uf_spinner);
        cidadeSpinner = findViewById(R.id.obra_cidade_spinner);
        obra_linear_layout = findViewById(R.id.obra_linear_master);
        visitaList = findViewById(R.id.obra_expandable_listWw);
    }

    private void setSh(){
        codusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_cod_usu));
        nomeusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_nome_usu));
        cnpj = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_cnpj));
        id_obra = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_id_obra));
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
            loadCidadeSpinnerData(String.valueOf(0));
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

    private void checkIfNew() {
        Intent selectIntent = this.getIntent();
        isNew = selectIntent.getBooleanExtra("isNew",true);
        if(isNew){
            obra_linear_layout.setEnabled(true);
        }else{
            obra_linear_layout.setEnabled(false);
        }
    }



    private void loadObra(){
        Methods.showLoadingDialog(this);
        HashMap<String, String> map = Methods.stringToHashMap("ID_OBRA",id_obra);
        String encodedParams = "";
        try {
            encodedParams = Methods.encode(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_query_uma_obra),encodedParams);
    }

    private void fillLayout(HashMap<String, String> resMap){
        //set obra
        List<HashMap<String,String>> obraMap = Methods.toList(resMap.get("OBRA"));
        cliente_editTxt.setText(String.valueOf(obraMap.get(0).get("CLIENTE")));
        setUfCidade(String.valueOf(obraMap.get(0).get("UF")),String.valueOf(obraMap.get(0).get("CIDADE")));
        bairro_editTxt.setText(String.valueOf(obraMap.get(0).get("BAIRRO")));
        end_EditTxt.setText(String.valueOf(obraMap.get(0).get("ENDERECO")));
        eng_EditTxt.setText(String.valueOf(obraMap.get(0).get("ENGENHEIRO")));
        tele1_EditTxt.setText(String.valueOf(obraMap.get(0).get("TELEFONE1")));
        tele2_EditTxt.setText(String.valueOf(obraMap.get(0).get("TELEFONE2")));
        tele3_EditTxt.setText(String.valueOf(obraMap.get(0).get("TELEFONE3")));
        empren_editTxt.setText(String.valueOf(obraMap.get(0).get("EMPRENDEDOR")));
        amox_EditTxt.setText(String.valueOf(obraMap.get(0).get("ALMOX")));
        //set visitas
        ArrayList<String> titles = new ArrayList<>();
        List<HashMap<String,String>> visitasMap = Methods.toList(resMap.get("VISITAS"));
        HashMap<String,ArrayList<String>> items = new HashMap<>();
        boolean f = false;
        int counter = 0;
        for (HashMap<String,String> item:
             visitasMap) {
            counter++;
            if(f == false){
                items.put(item.get("DT"),setOneItemParams(item,"yes"));
            }else{
                items.put(item.get("DT"),setOneItemParams(item,"no"));
            }
            f = true;
            titles.add(item.get("DT"));
        }
        if(titles != null && items != null){
            visitaAdapter = new VisitaAdapter(this,titles,items);
            visitaList.setAdapter(visitaAdapter);
        }
    }

    private ArrayList<String> setOneItemParams(HashMap<String,String> item ,String yesNo){
        ArrayList<String> arr = new ArrayList<>();
        arr.add(item.get("PERIODO"));
        arr.add(item.get("OBS"));
        arr.add(item.get("FASE_OBRA"));
        arr.add(yesNo);
        return arr;
    }

    private void setUfCidade(String uf, String cidade){
        try{
            if (uf != null && uf.trim() != "") {
                int spinnerPosition = ufAdaptpter.getPosition(uf);
                ufSpinner.setSelection(spinnerPosition);
                if(cidade != null && cidade.trim() != ""){
                    spinnerPosition = cidadeAdapter.getPosition(cidade);
                    cidadeSpinner.setSelection(spinnerPosition);
                }
            }
        }catch (Exception ex){

        }
    }

    @Override
    public void onTaskFinish(String response) {
        if(Methods.checkValidJson(response)) {
            HashMap<String, String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
            fillLayout(responseMap);
        } else {
            AlertDialog diag = new AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Aconteceu um erro para carregar os dados, entre em contato com o desenvolvidor")
                    .create();
            diag.show();
        }
        if(Methods.loadingDialog != null){
            Methods.closeLoadingDialog();
        }
    }
}