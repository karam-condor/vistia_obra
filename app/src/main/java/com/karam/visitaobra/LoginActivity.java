package com.karam.visitaobra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,TaskListener {
    String codusu,nomeusu;
    Button button_login;
    TextInputEditText editText_login;
    RelativeLayout layout_login_master;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Create the database states,cities
        Estados_Cidades estados_cidades = new Estados_Cidades(this);
        //set the layout controls
        setLayoutConstrols();
        //check googleplay services availability
        if(Methods.checkPlayServices(this,this)){
            Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.CAMERA,Manifest.permission.ACCESS_NETWORK_STATE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    //check is logged
                    //get shared preferences codusu,nomeusu
                    setCodNomeUsu();
                    if(!codusu.trim().equals("")){
                        callNextActivity();
                    }else{
                        enableControls();
                        setClickListeners();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        }
    }



    private void setClickListeners() {
        layout_login_master.setOnClickListener(this);
        button_login.setOnClickListener(this);
    }


    private void setLayoutConstrols() {
        button_login = findViewById(R.id.login_login_button);
        editText_login = findViewById(R.id.login_codusu_editText);
        layout_login_master = findViewById(R.id.login_layout_master);
    }

    private void enableControls() {
        button_login.setEnabled(true);
        editText_login.setEnabled(true);
    }

    private void setCodNomeUsu(){
        codusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_cod_usu));
        nomeusu = (String)Methods.getSharedPref(this,"string",getString(R.string.sh_login_nome_usu));
    }

    private void callNextActivity() {
        Intent NovaObraIntent = new Intent(LoginActivity.this,SelectActivity.class);
        startActivity(NovaObraIntent);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_layout_master:
                    Methods.CloseSoftKeyboradOnTouch(this);
                break;
            case R.id.login_login_button:
                    if(!String.valueOf(editText_login.getText()).trim().equals("")){
                        Methods.showLoadingDialog(this);
                        HashMap<String, String> map = Methods.stringToHashMap("CODUSU", String.valueOf(editText_login.getText()));
                        String encodedParams = "";
                        try {
                            encodedParams = Methods.encode(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_login),encodedParams);
                    }else{
                        Toast.makeText(this, "Insere o codigo.", Toast.LENGTH_SHORT).show();
                    }
                break;
        }
    }

    @Override
    public void onTaskFinish(String response) {
        if(Methods.checkValidJson(response)){
            HashMap<String,String> responseMap = Methods.toHashMap(response);//Convert the json to list of hashmap
            if(!String.valueOf(responseMap.get("CODUSU")).trim().matches("")) {
                Methods.setSharedPref(this, "string", getString(R.string.sh_login_cod_usu), String.valueOf(responseMap.get("CODUSU")));
                Methods.setSharedPref(this, "string", getString(R.string.sh_login_nome_usu), String.valueOf(responseMap.get("NOMEUSU")));
                callNextActivity();
            }else{
                Toast.makeText(this, "Aconteceu um erro", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
        if(Methods.loadingDialog != null){
            Methods.closeLoadingDialog();
        }
    }
}



