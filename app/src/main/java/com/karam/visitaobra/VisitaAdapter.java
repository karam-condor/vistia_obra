package com.karam.visitaobra;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;

public class VisitaAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> titles;
    HashMap<String, ArrayList<String>> items;

    public VisitaAdapter(Context context, ArrayList<String> titles, HashMap<String, ArrayList<String>> items) {
        this.context = context;
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
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.visita_layout, null);
        }
        Spinner spinner = convertView.findViewById(R.id.obra_perido_spinner);
        TextInputEditText fase_obra_editTxt = convertView.findViewById(R.id.obra_fase_editTxt);
        TextInputEditText obs_obra_editTxt = convertView.findViewById(R.id.obra_obs_editTxt);
        //set texts
        switch (items.get(titles.get(groupPosition)).get(3)){
            case "yes":
                spinner.setEnabled(true);
                fase_obra_editTxt.setEnabled(true);
                obs_obra_editTxt.setEnabled(true);
                break;
            case "no":
                spinner.setEnabled(false);
                fase_obra_editTxt.setEnabled(false);
                obs_obra_editTxt.setEnabled(false);
                break;
        }
        switch (items.get(titles.get(groupPosition)).get(0)){
            case "MANHÃ":
                spinner.setSelection(0);
                break;
            case "TARDE":
                spinner.setSelection(1);
                break;
            case "MANHÃ/TARDE":
                spinner.setSelection(2);
                break;
        }
        fase_obra_editTxt.setText(items.get(titles.get(groupPosition)).get(1));
        obs_obra_editTxt.setText(items.get(titles.get(groupPosition)).get(2));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
