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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObraInfoActivity extends AppCompatActivity implements TaskListener{
    String id_obra;
    TextView ufTxtVw,cidadeTxtVw;
    TextInputEditText cliente_editTxt,empren_editTxt,bairro_editTxt,end_EditTxt,amox_EditTxt,eng_EditTxt,tele1_EditTxt,tele2_EditTxt,tele3_EditTxt;
    ArrayAdapter<String> ufAdaptpter,cidadeAdapter;
    VisitaInfoAdapter visitaInfoAdapter;
    ExpandableListView visitaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obra_info);
        //set codusu,nomeusu,cnpj
        setId_obra_Id();
        //set layout Controls
        setLayoutControls();
        //load obra info
        loadObra();
    }


    private void setLayoutControls() {
        cliente_editTxt = findViewById(R.id.obra_info_cliente_editTxt);
        empren_editTxt = findViewById(R.id.obra_info_emprendedor_editTxt);
        bairro_editTxt = findViewById(R.id.obra_info_bairro_editTxt);
        end_EditTxt = findViewById(R.id.obra_info_endereco_editTxt);
        amox_EditTxt = findViewById(R.id.obra_info_amox_editTxt);
        eng_EditTxt = findViewById(R.id.obra_info_engenherio_editTxt);
        tele1_EditTxt = findViewById(R.id.obra_info_telefone1_editTxt);
        tele2_EditTxt = findViewById(R.id.obra_info_telefone2_editTxt);
        tele3_EditTxt = findViewById(R.id.obra_info_telefone3_editTxt);
        ufTxtVw = findViewById(R.id.obra_info_uf_txtVw);
        cidadeTxtVw = findViewById(R.id.obra_info_cidade_txtVw);
        visitaList = findViewById(R.id.obra_info_expandable_listWw);
    }

    private void setId_obra_Id(){
        Intent intent = this.getIntent();
        id_obra = intent.getStringExtra("id_obra");
    }





    private void loadObra(){
        //Methods.showLoadingDialog(this);
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
        ufTxtVw.setText(String.valueOf(obraMap.get(0).get("UF")));
        cidadeTxtVw.setText(String.valueOf(obraMap.get(0).get("CIDADE")));
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
        for (HashMap<String,String> item:
                visitasMap) {
            items.put(item.get("DT"),setOneItemParams(item));
            titles.add(item.get("DT"));
        }
        if(titles != null && items != null){
             visitaInfoAdapter = new VisitaInfoAdapter(this,titles,items);
            visitaList.setAdapter(visitaInfoAdapter);
        }
    }

    private ArrayList<String> setOneItemParams(HashMap<String,String> item){
        ArrayList<String> arr = new ArrayList<>();
        arr.add(item.get("PERIODO"));
        arr.add(item.get("FASE_OBRA"));
        arr.add(item.get("OBS"));
        return arr;
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
        if (Methods.checkValidJson(response)) {
            HashMap<String, String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
            fillLayout(responseMap);
        } else {
            showErrorDialog("Aconteceu um erro para carregar os dados, entre em contato com o desenvolvidor");
        }
        if(Methods.loadingDialog != null){
            Methods.closeLoadingDialog();
        }
    }
}