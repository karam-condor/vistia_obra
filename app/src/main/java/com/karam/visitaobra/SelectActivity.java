package com.karam.visitaobra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Console;
import java.util.HashMap;

public class SelectActivity extends AppCompatActivity implements View.OnClickListener,TaskListener {
    String cnpj,codusu,nomeusu,id_obra;
    Button btn_novaobra,btn_acompanhamento,btn_cnpj,btn_cancel,btn_pesquisa;
    EditText editTxt_cnpj;
    AlertDialog cnpjDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        //get cnpj,codus,nomeusu from SH
        setCnpjCodNomeUsu();
        //check cnpj, go to next activity if it is not empty
        if(id_obra.trim().equals("")){
            //Prepare layout
            setLayoutConstrols();
            //set Click Listeners
            setOnClickListeners();
        }else{
            Intent obraIntent = new Intent(SelectActivity.this,ObraActivity.class);
            startActivity(obraIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.select_logout_menu:
                Methods.setSharedPref(this,"string",getString(R.string.sh_login_cod_usu),"");
                Methods.setSharedPref(this,"string",getString(R.string.sh_login_nome_usu),"");
                Intent loginIntent = new Intent(this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setLayoutConstrols() {
        btn_novaobra = findViewById(R.id.select_novaobra_button);
        btn_acompanhamento = findViewById(R.id.select_acompanhamento_button);
        btn_pesquisa = findViewById(R.id.select_pesquisa_button);
    }

    private View setCnpjDialogControls(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.insert_cnpj_dialog,null);
        btn_cnpj = layout.findViewById(R.id.insertCnpj_button);
        btn_cancel = layout.findViewById(R.id.insertCnpjCancel_button);
        editTxt_cnpj = layout.findViewById(R.id.insertCnpj_editText);
        btn_cancel.setOnClickListener(SelectActivity.this);
        btn_cnpj.setOnClickListener(SelectActivity.this);
        return  layout;
    }

    private void setOnClickListeners(){
        btn_novaobra.setOnClickListener(this);
        btn_acompanhamento.setOnClickListener(this);
        btn_pesquisa.setOnClickListener(this);
    }

    private void setCnpjCodNomeUsu(){
        codusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_cod_usu));
        nomeusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_nome_usu));
        cnpj = (String) Methods.getSharedPref(this,"string",getString(R.string.sh_cnpj));
        id_obra = (String) Methods.getSharedPref(this,"string",getString(R.string.sh_id_obra));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_novaobra_button:
                cnpjDialog = new AlertDialog.Builder(this)
                        .setView(setCnpjDialogControls())
                        .setCancelable(false)
                        .create();
                cnpjDialog.show();
                break;
            case R.id.select_acompanhamento_button:
                goToSearchObra(true); //true insert
                break;
            case R.id.select_pesquisa_button:
                goToSearchObra(false); //false search
                break;
            case R.id.insertCnpj_button:
                if(cnpjDialog != null){
                    cnpjDialog.dismiss();
                }
                cnpj = String.valueOf(editTxt_cnpj.getText()).trim();
                if(!cnpj.equals("") && cnpj.length() == 14){
                    Methods.showLoadingDialog(this);
                    GoogleLocation.requestSingleUpdate(this, new GoogleLocation.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(GoogleLocation.GPSCoordinates location) {
                            HashMap<String, String> map = Methods.stringToHashMap("LAT%LONGT%CODUSU%NOMEUSU%CNPJ",String.valueOf(location.latitude),String.valueOf(location.longitude),codusu,nomeusu,cnpj);
                            String encodedParams = "";
                            try {
                                encodedParams = Methods.encode(map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            SRVConnection connection = new SRVConnection(SelectActivity.this,"response",getString(R.string.url_master) + getString(R.string.url_cadstrar_nova_obra),encodedParams);
                        }
                    });
                }else{
                    Toast.makeText(SelectActivity.this, "Cnpj invalido", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.insertCnpjCancel_button:
                cnpjDialog.dismiss();
                break;
        }
    }

    private void goToSearchObra(boolean searchOrNew){
        Intent searchObraIntent = new Intent(SelectActivity.this,SearchObraActivity.class);
        searchObraIntent.putExtra("searchOrNew",searchOrNew); // 0 insert
        startActivity(searchObraIntent);
    }


    @Override
    public void onTaskFinish(String response) {
        if(Methods.checkValidJson(response)) {
            HashMap<String, String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
            String id = String.valueOf(responseMap.get("id")).trim();
            String id_obra = String.valueOf(responseMap.get("id_obra")).trim();
            if (!id.matches("") && !id_obra.matches("")) {
                Intent obraIntent = new Intent(SelectActivity.this, ObraActivity.class);
                Methods.setSharedPref(this, "boolean", getString(R.string.sh_is_new), true);
                Methods.setSharedPref(this, "string", getString(R.string.sh_cnpj), cnpj);
                Methods.setSharedPref(this, "string", getString(R.string.sh_id_visita), id);
                Methods.setSharedPref(this, "string", getString(R.string.sh_id_obra), id_obra);
                if(Methods.loadingDialog != null){
                    Methods.closeLoadingDialog();
                }
                startActivity(obraIntent);
                finish();
            } else {
                Toast.makeText(this, "Aconteceu um erro", Toast.LENGTH_SHORT).show();
                if(Methods.loadingDialog != null){
                    Methods.closeLoadingDialog();
                }
            }
        }else{
            if(Methods.loadingDialog != null){
                Methods.closeLoadingDialog();
            }
        }
    }
}