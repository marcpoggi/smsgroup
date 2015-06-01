package com.feeltest.smsgroup;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MonArrayAdapterTesteur extends ArrayAdapter<Testeur> {
	private final Context context;
	private LayoutInflater inflator ;
	private List<Testeur> monarray;


	public MonArrayAdapterTesteur(Context context, int resource, List<Testeur> listTesteur) {
		super(context, resource, listTesteur);
		this.context=context;
		this.monarray=listTesteur;
	}


	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflator.inflate(R.layout.lignetesteur, parent,false);  
	    ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
	    iv.setImageDrawable( getContext().getResources().getDrawable(R.drawable.testeur_icon));
	    TextView tv = (TextView)convertView.findViewById(R.id.TextView1);
	    TextView tv_tel = (TextView)convertView.findViewById(R.id.textView2);
	    String nom =(monarray.get(position).getNomPrenom());
	    String tel = (monarray.get(position).getTel());
	    if (nom!=null)   tv.setText(nom);	
	    if (tel!=null)   tv_tel.setText(tel);	
	    
	    return convertView;
	}


	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public Testeur getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}
	
	public int getId(int position) {
		// TODO Auto-generated method stub
		return getItem(position).getId();
	}
	
	
	
}
