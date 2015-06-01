package com.feeltest.smsgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MonArrayAdapterGroup extends ArrayAdapter<Groupe> {
	private final Context context;
	private LayoutInflater inflator ;
	private List<Groupe> monarray;
	private int selectedItem=-1;


	public MonArrayAdapterGroup(Context context, int resource, List<Groupe> listgroupe) {
		super(context, resource, listgroupe);
		this.context=context;
		this.monarray=listgroupe;
	}


	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflator.inflate(R.layout.lignegroup, parent,false);  
	    ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
	    iv.setImageDrawable( getContext().getResources().getDrawable(R.drawable.group_icon));
	    TextView tv = (TextView)convertView.findViewById(R.id.TextView1);
	    tv.setText(monarray.get(position).getnomgroup());	    
	    return convertView;
	}


	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public Groupe getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}
	
	public int getId(int position) {
		// TODO Auto-generated method stub
		return getItem(position).getId();
	}


    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

}
