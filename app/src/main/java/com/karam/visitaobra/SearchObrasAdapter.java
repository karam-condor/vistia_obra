package com.karam.visitaobra;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class SearchObrasAdapter extends BaseAdapter {
    Context context;
    ArrayList<Obra> items;

    public SearchObrasAdapter(Context context, ArrayList<Obra> items) {
        this.context = context;
        this.items = items;
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
                AlertDialog diag = new AlertDialog.Builder(context)
                        .setTitle("Nova visita")
                        .setMessage("Deseja inciar uma nova visita da obra: \n Cliente: "+clientTextVw.getText()+"\n UF: "+ufTextVw.getText()+"\n Cidade: "+cidadeTextVw.getText())
                        .setNegativeButton("Ignorar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                diag.show();
            }
        });
        return convertView;
    }
}
