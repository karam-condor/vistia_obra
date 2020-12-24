package com.karam.visitaobra;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchObrasAdapter extends BaseAdapter {
    Context context;
    Activity activity;
    ArrayList<Obra> items;
    String codusu,nomeusu;
    boolean searchOrNew;

    public SearchObrasAdapter(Context context,Activity activity, ArrayList<Obra> items,String codusu,String nomeusu,boolean searOrNew) {
        this.context = context;
        this.activity = activity;
        this.items = items;
        this.codusu = codusu;
        this.nomeusu = nomeusu;
        this.searchOrNew = searOrNew;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_obra_list_layout, null);
        }
        TextView clientTextVw = convertView.findViewById(R.id.search_obra_list_cliente_txtVw);
        TextView ufTextVw = convertView.findViewById(R.id.search_obra_list_uf_txtVw);
        TextView cidadeTextVw = convertView.findViewById(R.id.search_obra_list_cidade_txtVw);
        TextView cnpjTextVw = convertView.findViewById(R.id.search_obra_list_cnpj_txtVw);
        TextView dtTextVw = convertView.findViewById(R.id.search_obra_list_dt_txtVw);
        //set content
        clientTextVw.setText(items.get(position).getCliente());
        ufTextVw.setText(items.get(position).getUf());
        cidadeTextVw.setText(items.get(position).getCidade());
        cnpjTextVw.setText(items.get(position).getCnpj());
        dtTextVw.setText(items.get(position).getDtultimavisita());

        //set on Click

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchOrNew){
                    AlertDialog diag = new AlertDialog.Builder(context)
                            .setTitle("Nova visita")
                            .setMessage("Deseja inciar uma nova visita da obra: \nCliente: "+clientTextVw.getText()+"\nUF: "+ufTextVw.getText()+"\nCidade: "+cidadeTextVw.getText())
                            .setNegativeButton("Ignorar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Methods.showLoadingDialog(context);
                                    HashMap<String, String> map = Methods.stringToHashMap("ID_OBRA%CODUSU%NOMEUSU",items.get(position).getId(),codusu,nomeusu);
                                    String encodedParams = "";
                                    try {
                                        encodedParams = Methods.encode(map);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    ((SearchObraActivity)activity).setSelector(1);
                                    SRVConnection connection = new SRVConnection(activity,"response",context.getString(R.string.url_master) + context.getString(R.string.url_cadstrar_visita),encodedParams);
                                }
                            })
                            .create();
                    diag.show();
                }else{
                    Intent intent = new Intent(activity,ObraInfoActivity.class);
                    intent.putExtra("id_obra" ,items.get(position).getId());
                    activity.startActivity(intent);
                }
            }
        });
        return convertView;
    }
}