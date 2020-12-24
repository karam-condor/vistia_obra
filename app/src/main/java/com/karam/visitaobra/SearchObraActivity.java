package com.karam.visitaobra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SearchObraActivity extends AppCompatActivity implements View.OnClickListener,TaskListener {
    Spinner ufSpinner,cidadeSpinner;
    TextInputEditText cliente_editTxt;
    Button dtIncial_btn,dtFinal_btn,search_btn;
    ArrayAdapter ufAdaptpter,cidadeAdapter;
    ListView obrasList;
    String codusu;
    String nomeusu;
    boolean searchOrNew = false;


    public void setSelector(int selector) {
        this.selector = selector;
    }

    int selector = 0;//0 show search result, 1 insert visita of new obra
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_obra);
        //set SH
        setSh();
        //set layout Controls
        setLayoutControls();
        //set Click Listeners of layout components
        setOnClickListners();
        //set default date
        setButtonText();
        //load uf spinner data
        loadUfSpinnerData();
    }

    private void setSh() {
        codusu = (String) Methods.getSharedPref(this,"string",getString(R.string.sh_login_cod_usu));
        nomeusu = (String) Methods.getSharedPref(this,"string",getString(R.string.sh_login_nome_usu));
        Intent intent = this.getIntent();
        searchOrNew = intent.getBooleanExtra("searchOrNew",false);
    }

    private void setLayoutControls() {
        cliente_editTxt = findViewById(R.id.search_obra_cliente_editTxt);
        dtIncial_btn = findViewById(R.id.search_obra_dtincial_btn);
        dtFinal_btn = findViewById(R.id.search_obra_dtfinal_btn);
        search_btn = findViewById(R.id.search_obra_search_btn);
        ufSpinner = findViewById(R.id.search_obra_uf_spinner);
        cidadeSpinner = findViewById(R.id.search_obra_cidade_spinner);
        obrasList = findViewById(R.id.search_obra_result_listVw);
    }

    private void setOnClickListners() {
        dtIncial_btn.setOnClickListener(this);
        dtFinal_btn.setOnClickListener(this);
        search_btn.setOnClickListener(this);
    }

    private void setButtonText(){
        dtIncial_btn.setText(getDateToday());
        dtFinal_btn.setText(getDateToday());
    }

    //dd/mm/yyyy
    private String getDateToday(){
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
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
            ufAdaptpter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ufs);
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
        Cursor c = estados_cidades.select(false,"cidade",null,"estado = ?",new String[]{ufID},null,null,"id",null);
        if(c!= null && c.getCount() > 0){
            String[] cidades = new String[c.getCount()+1];
            cidades[0] = "";
            c.moveToFirst();
            int counter = 1;
            while(c!= null && !c.isAfterLast()){
                cidades[counter] = c.getString(c.getColumnIndex("nome"));
                counter++;
                c.moveToNext();
            }
            cidadeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cidades);
            cidadeSpinner.setAdapter(cidadeAdapter);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_obra_dtincial_btn:
                Methods.showDatePicker(this,dtIncial_btn);
                break;
            case R.id.search_obra_dtfinal_btn:
                Methods.showDatePicker(this,dtFinal_btn);
                break;
            case R.id.search_obra_search_btn:
                HashMap<String, String> map = Methods.stringToHashMap("UF%CIDADE%CLIENTE%DTINCIAL%DTFINAL",ufSpinner.getSelectedItem() != null?ufSpinner.getSelectedItem() .toString():"",
                        cidadeSpinner.getSelectedItem() != null?cidadeSpinner.getSelectedItem() .toString():"",String.valueOf(cliente_editTxt.getText()),String.valueOf(dtIncial_btn.getText()),
                        String.valueOf(dtFinal_btn.getText()));
                String encodedParams = "";
                try {
                    encodedParams = Methods.encode(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_query_search_obras),encodedParams);
                break;
        }
    }
    @Override
    public void onTaskFinish(String response) {
        switch (selector){
            case 0:
                if(Methods.checkValidJson(response)){
                    ArrayList<Obra> obras = new ArrayList<>();
                    List<HashMap<String,String>> responseArr = Methods.toList(response);
                    if(responseArr.size()>0){
                        Obra obra;
                        for (HashMap<String,String> item:
                                responseArr) {
                            obra = new Obra();
                            obra.setId(item.get("ID"));
                            obra.setCliente(item.get("CLIENTE"));
                            obra.setUf(item.get("UF"));
                            obra.setCidade(item.get("CIDADE"));
                            obra.setCnpj(item.get("CNPJ"));
                            obra.setDtultimavisita(item.get("DTULTIMAVISTIA"));
                            obras.add(obra);
                        }
                        SearchObrasAdapter adapter = new SearchObrasAdapter(this,this,obras,codusu,nomeusu,searchOrNew);
                        obrasList.setAdapter(adapter);
                    }else{
                        Toast.makeText(this, "Não há resultados para exibir", Toast.LENGTH_SHORT).show();
                        SearchObrasAdapter adapter = new SearchObrasAdapter(this,this,new ArrayList<>(obras),codusu,nomeusu,searchOrNew);
                        obrasList.setAdapter(adapter);
                    }
                }else{
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                HashMap<String, String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
                String id = String.valueOf(responseMap.get("id")).trim();
                String id_obra = String.valueOf(responseMap.get("id_obra")).trim();
                if (!id.matches("") && !id_obra.matches("")) {
                    Intent obraIntent = new Intent(this,ObraActivity.class);
                    Methods.setSharedPref(this,"boolean",getString(R.string.sh_is_new),false);
                    Methods.setSharedPref(this,"string",getString(R.string.sh_id_obra),id_obra);
                    Methods.setSharedPref(this,"string",getString(R.string.sh_id_visita),id);
                    if(Methods.loadingDialog != null){
                        Methods.closeLoadingDialog();
                    }
                    startActivity(obraIntent);
                    finish();
                    break;
                }else {
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show();
                }
        }
        if(Methods.loadingDialog != null){
            Methods.closeLoadingDialog();
        }
    }
}