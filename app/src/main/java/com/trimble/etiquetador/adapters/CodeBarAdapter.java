package com.trimble.etiquetador.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trimble.etiquetador.model.CodeBar;
import com.trimble.etiquetador.R;
import com.trimble.etiquetador.Observer;

import java.util.ArrayList;

/**
 * Created by jorge on 19/1/17.
 */
public class CodeBarAdapter extends ArrayAdapter<CodeBar> {
    private ArrayList<CodeBar> rfids;
    private Context context;
    private Observer observer;

    public CodeBarAdapter(Context context, ArrayList<CodeBar> rfids, Observer observer) {
        super(context,0,rfids);
        this.rfids = rfids;
        this.context = context;
        this.observer=observer;
    }

    static class ViewHolder{
        public TextView txtCode;
        public ImageView imgEstado;
    }

    public ArrayList<CodeBar> getRfids() {
        return rfids;
    }

    public CodeBarAdapter setRfids(ArrayList<CodeBar> rfids) {
        this.rfids = rfids;
        return this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.rfid_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.txtCode = (TextView)convertView.findViewById(R.id.txtRfidCode);
            viewHolder.imgEstado=(ImageView)convertView.findViewById(R.id.imgEstado);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder)convertView.getTag();
        final CodeBar currentRfid = this.rfids.get(position);
        holder.txtCode.setText(currentRfid.getCode());
        //holder.imgEstado.setText(currentRfid.getEstado());
        if(currentRfid.getEstado().equals("Si Detectado")){
            //int id = context.getResources().getIdentifier("drawable/checkmark.png",null,null);
            holder.imgEstado.setImageResource(R.drawable.checkmark);
        }else{
            holder.imgEstado.setImageResource(R.drawable.no);
        }
        return convertView;
    }
}
