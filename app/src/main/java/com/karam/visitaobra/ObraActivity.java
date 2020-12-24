package com.karam.visitaobra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObraActivity extends AppCompatActivity implements TaskListener{
    String codusu;
    String nomeusu;
    String cnpj;
    String id_obra;
    String id;
    String fase_obra = "";
    String obs = "";
    int photo_seq = 0;
    Spinner ufSpinner,cidadeSpinner;
    TextInputEditText cliente_editTxt,empren_editTxt,bairro_editTxt,end_EditTxt,amox_EditTxt,eng_EditTxt,tele1_EditTxt,tele2_EditTxt,tele3_EditTxt;
    boolean isNew =true;
    ArrayAdapter<String> ufAdaptpter,cidadeAdapter;
    VisitaAdapter visitaAdapter;
    ExpandableListView visitaList;
    int selector=0;/*
    0 incial load
    1 update obra,visita,
    2 update visita
    3 finalizar
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obra);
        //set codusu,nomeusu,cnpj
        setSh();
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
                //Methods.showLoadingDialog(this);
                updateAll();
                return true;
            case R.id.obra_fotos_menu:
                takePhoto();
                return true;
            case R.id.obra_finalizar_menu:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Finalizar visita")
                        .setMessage("Deseja finalizar esta visita?")
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selector = 3;
                                finalizarVisita();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void takePhoto() {
        String filename = id_obra + "-" + id + photo_seq;
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(filename,".jpg",storageDirectory);
            String currentPhotoPath = imageFile.getAbsolutePath();
            Uri imgUri = FileProvider.getUriForFile(this,"com.karam.visitaobra.fileprovider",imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
            startActivityForResult(intent,1);
        } catch (IOException e) {
            e.printStackTrace();
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
        visitaList = findViewById(R.id.obra_expandable_listWw);
        //Set telephone mask
        tele1_EditTxt.addTextChangedListener(Mask.insert("(##)#####-####", tele1_EditTxt));
        tele2_EditTxt.addTextChangedListener(Mask.insert("(##)#####-####", tele2_EditTxt));
        tele3_EditTxt.addTextChangedListener(Mask.insert("(##)#####-####", tele3_EditTxt));
    }

    private void setSh(){
        codusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_cod_usu));
        nomeusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_nome_usu));
        cnpj = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_cnpj));
        id_obra = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_id_obra));
        id = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_id_visita));
    }

    private void loadUfSpinnerData(){
        Estados_Cidades estados_cidades = new Estados_Cidades(this);
        Cursor c = estados_cidades.select(false,"estado",new String[]{"uf"},null,null,null,null,"id",null);
        if(c!= null && c.getCount() > 0){
            String[] ufs = new String[c.getCount()];
            c.moveToFirst();
            int counter =0;
            while(c!= null && !c.isAfterLast()){
                ufs[counter] = c.getString(c.getColumnIndex("uf"));
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
        isNew = (boolean) Methods.getSharedPref(this,"boolean",getString(R.string.sh_is_new));
        setObraEnabledDisabled(isNew);
    }

    private void finalizarVisita(){
        Methods.showLoadingDialog(this);
        HashMap<String, String> map = Methods.stringToHashMap("ID",id);
        String encodedParams = "";
        try {
            encodedParams = Methods.encode(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_finalizar),encodedParams);
    }

    private void loadObra(){
        selector = 0;
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


    private void updateAll(){
        Methods.showLoadingDialog(this);
        if(isNew){
            selector = 1;
            HashMap<String, String> map = Methods.stringToHashMap("ID%ID_OBRA%EMPRENDEDOR%CLIENTE%UF%CIDADE%ENDERECO%BAIRRO%ALMOX%ENGENHEIRO%FASE_OBRA%TELEFONE1%TELEFONE2%TELEFONE3%OBS",id,id_obra,String.valueOf(empren_editTxt.getText()),String.valueOf(cliente_editTxt.getText()),
                    ufSpinner.getSelectedItem() == null?"":ufSpinner.getSelectedItem().toString(),cidadeSpinner.getSelectedItem() == null ? "" : cidadeSpinner.getSelectedItem().toString(),String.valueOf(end_EditTxt.getText()),String.valueOf(bairro_editTxt.getText()),String.valueOf(amox_EditTxt.getText()),String.valueOf(eng_EditTxt.getText()),
                    fase_obra,String.valueOf(tele1_EditTxt.getText()),String.valueOf(tele2_EditTxt.getText()),String.valueOf(tele3_EditTxt.getText()),obs);
            String encodedParams = "";
            try {
                encodedParams = Methods.encode(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_update_new_obra),encodedParams);
        }else{
            selector = 2;
            HashMap<String, String> map = Methods.stringToHashMap("ID%FASE_OBRA%OBS",id,fase_obra,obs);
            String encodedParams = "";
            try {
                encodedParams = Methods.encode(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_update_visita),encodedParams);
        }
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
        for (HashMap<String,String> item:
             visitasMap) {
            if(f == false){
                items.put(item.get("DT"),setOneItemParams(item,"yes"));
            }else{
                items.put(item.get("DT"),setOneItemParams(item,"no"));
            }
            f = true;
            titles.add(item.get("DT"));
        }
        if(titles != null && items != null){
            visitaAdapter = new VisitaAdapter(this,this,titles,items);
            visitaList.setAdapter(visitaAdapter);
            visitaList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                    if(groupPosition == 0){
                        items.get(titles.get(0)).set(1,fase_obra);
                        items.get(titles.get(0)).set(2,obs);
                    }
                }
            });
            //visitaList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            //    @Override
            //    public void onGroupExpand(int groupPosition) {
            //        if(obs != null && fase_obra != null && !obs.equals("") && !fase_obra.equals("")){
            //            if(groupPosition == 0){
            //                items.get(titles.get(0)).set(1,fase_obra);
            //                items.get(titles.get(0)).set(2,obs);
            //            }
            //        }
            //    }
            //});
        }
    }

    private ArrayList<String> setOneItemParams(HashMap<String,String> item ,String yesNo){
        ArrayList<String> arr = new ArrayList<>();
        arr.add(item.get("PERIODO"));
        arr.add(item.get("FASE_OBRA"));
        arr.add(item.get("OBS"));
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

    private void showErrorDialog(String msg){
        AlertDialog diag = new AlertDialog.Builder(this)
                .setTitle("Erro")
                .setMessage(msg)
                .create();
        diag.show();
    }

    @Override
    public void onTaskFinish(String response) {
        switch (selector) {
            case 0:
                if (Methods.checkValidJson(response)) {
                    HashMap<String, String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
                    fillLayout(responseMap);
                } else {
                    showErrorDialog("Aconteceu um erro para carregar os dados, entre em contato com o desenvolvidor");
                }
                break;
            case 1:
            case 2:
                if (response.equals("ok")) {
                    Toast.makeText(this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(response.equals("ok")){
                    Methods.setSharedPref(ObraActivity.this,"string",getString(R.string.sh_id_obra),"");
                    Methods.setSharedPref(ObraActivity.this,"string",getString(R.string.sh_id_visita),"");
                    Methods.setSharedPref(ObraActivity.this,"string",getString(R.string.sh_cnpj),"");
                    Intent selectIntent = new Intent(ObraActivity.this,SelectActivity.class);
                    startActivity(selectIntent);
                    finish();
                }else{
                    showErrorDialog(response);
                }
                break;
        }
        if (Methods.loadingDialog != null) {
            Methods.closeLoadingDialog();
        }
    }

    private void setObraEnabledDisabled(boolean isActive){
        cliente_editTxt.setEnabled(isActive);
        empren_editTxt.setEnabled(isActive);
        bairro_editTxt.setEnabled(isActive);
        end_EditTxt.setEnabled(isActive);
        amox_EditTxt.setEnabled(isActive);
        eng_EditTxt.setEnabled(isActive);
        tele1_EditTxt.setEnabled(isActive);
        tele2_EditTxt.setEnabled(isActive);
        tele3_EditTxt.setEnabled(isActive);
        ufSpinner.setEnabled(isActive);
        cidadeSpinner.setEnabled(isActive);
    }


    public void setFase_obra(String fase_obra) {
        this.fase_obra = fase_obra;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}