package com.karam.visitaobra;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;

public class VisitaAdapter extends BaseExpandableListAdapter {
    Context context;
    Activity activity;
    ArrayList<String> titles;
    HashMap<String, ArrayList<String>> items;
    TextWatcher obsWatcher,faseWatcher;

    public VisitaAdapter(Context context, Activity activity, ArrayList<String> titles, HashMap<String, ArrayList<String>> items) {
        this.context = context;
        this.activity = activity;
        this.titles = titles;
        this.items = items;
    }

    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(titles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.visita_title_layout, null);
        }
        TextView dataText = convertView.findViewById(R.id.visita_title);
        dataText.setText(titles.get(groupPosition));
        switch (items.get(titles.get(groupPosition)).get(3)){
            case "yes":
                dataText.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case "no":
                dataText.setTextColor(ContextCompat.getColor(context, R.color.grey));
                break;
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.i("karam131313", "getChildView: ");
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.visita_layout, null);
        }
        TextInputEditText fase_obra_editTxt = convertView.findViewById(R.id.obra_fase_editTxt);
        TextInputEditText obs_obra_editTxt = convertView.findViewById(R.id.obra_obs_editTxt);
        TextView periodo_txtVw = convertView.findViewById(R.id.obra_periodo_txtVw);
        if(obsWatcher != null && faseWatcher != null){
            fase_obra_editTxt.removeTextChangedListener(obsWatcher);
            obs_obra_editTxt.removeTextChangedListener(faseWatcher);
        }
        //set texts
        switch (items.get(titles.get(groupPosition)).get(3)){
            case "yes":
                //make the edittexts enables
                fase_obra_editTxt.setEnabled(true);
                obs_obra_editTxt.setEnabled(true);
                obsWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((ObraActivity)activity).setObs(String.valueOf(s));
                        if(obsWatcher != null && faseWatcher != null){
                            fase_obra_editTxt.removeTextChangedListener(obsWatcher);
                            obs_obra_editTxt.removeTextChangedListener(faseWatcher);
                        }
                    }
                };

                faseWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        ((ObraActivity)activity).setFase_obra(String.valueOf(s));
                        if(obsWatcher != null && faseWatcher != null){
                            fase_obra_editTxt.removeTextChangedListener(obsWatcher);
                            obs_obra_editTxt.removeTextChangedListener(faseWatcher);
                        }
                    }
                };
                obs_obra_editTxt.addTextChangedListener(obsWatcher);
                fase_obra_editTxt.addTextChangedListener(faseWatcher);
                break;
            case "no":
                //disable edit texts
                fase_obra_editTxt.setEnabled(false);
                obs_obra_editTxt.setEnabled(false);
                break;
        }
        periodo_txtVw.setText(items.get(titles.get(groupPosition)).get(0));
        fase_obra_editTxt.setText(items.get(titles.get(groupPosition)).get(1));
        obs_obra_editTxt.setText(items.get(titles.get(groupPosition)).get(2));
        periodo_txtVw.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}