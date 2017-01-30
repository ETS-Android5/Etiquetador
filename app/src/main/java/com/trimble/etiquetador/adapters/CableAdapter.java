package com.trimble.etiquetador.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.trimble.etiquetador.R;
import com.trimble.etiquetador.model.Cable;

import java.util.ArrayList;

public class CableAdapter extends ArrayAdapter<Cable> {
    private ArrayList<Cable> cables;
    private Context context;

    public CableAdapter(Context context, ArrayList<Cable> cables){
        super(context,0,cables);
        this.context = context;
        this.cables = cables;
    }

    static class ViewHolder{
        public TextView tagid;
        public TextView operadora;
        public TextView tipo;
        public TextView uso;
        public TextView escable;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.poste_item,parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tagid = (TextView) convertView.findViewById(R.id.viewtagid);
            viewHolder.operadora = (TextView) convertView.findViewById(R.id.viewoperadora);
            viewHolder.tipo = (TextView) convertView.findViewById(R.id.viewtipo);
            viewHolder.uso = (TextView) convertView.findViewById(R.id.viewuso);
            viewHolder.escable = (TextView) convertView.findViewById(R.id.viewescable);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Cable currentCable = this.cables.get(position);
        holder.tagid.setText(currentCable.getTagid());
        holder.operadora.setText(currentCable.getOperadora());
        holder.tipo.setText(currentCable.getTipo());
        holder.uso.setText(currentCable.getUso());
        if(currentCable.isEscable()){
            holder.escable.setText("Cable");
        }
        else{
            holder.escable.setText("NoEsCable");
        }
        return convertView;
    }
}
