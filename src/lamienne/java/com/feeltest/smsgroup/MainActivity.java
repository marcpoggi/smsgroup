package com.feeltest.smsgroup;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	final int ACTIVITY_CHOOSE_FILE = 1;
	private ListView malisteview;
	private MonArrayAdapterGroup monadapter;
	private ArrayList<Groupe> listegroupe;
	public static final String ACTION_SMS_SENT = "com.feeltest.smsgroup.SMS_SENT_ACTION";
	private BroadcastReceiver mbroadcastreceiver;
	private Context context;
	protected ProgressDialog p;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		final ActionBar actionBar = getActionBar();
	    actionBar.setHomeButtonEnabled(false);
	    actionBar.setDisplayShowHomeEnabled(false);
	    actionBar.setDisplayUseLogoEnabled(false);
		setContentView(R.layout.activity_main);
		context = getApplication().getApplicationContext();
		DBManager.setSharedContext(getApplicationContext());
		DBManager.sharedManager().open();
		malisteview = (ListView) findViewById(R.id.listView_main);
		listegroupe = DBManager.sharedManager().getAllGroup();
		monadapter = new MonArrayAdapterGroup(this,R.layout.lignegroup,listegroupe);
		malisteview.setAdapter(monadapter);
		malisteview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		malisteview.clearChoices();
		malisteview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((MonArrayAdapterGroup) malisteview.getAdapter()).setSelectedItem(position);
				invalidateOptionsMenu();			
			}});
		
		mbroadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
         String message = null;
           boolean error = true;
            p.incrementProgressBy(1);
            if (p.getMax()==p.getProgress())
           	 {
           	 unregisterReceiver(mbroadcastreceiver);
           	 }
            switch (getResultCode()) {
            case Activity.RESULT_OK:
               message = "Message sent!";
            error = false;
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                message = "Error.";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                message = "Error: No service.";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                message = "Error: Null PDU.";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                message = "Error: Radio off.";
                break;
            }
        //    Toast.makeText(context, message, Toast.LENGTH_SHORT)
	    //      .show();    
        }
		};
	
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
	    super.onPrepareOptionsMenu(menu);
	    MenuItem menu_edit = menu.findItem(R.id.item_edit);
	    MenuItem menu_view = menu.findItem(R.id.item_view);
	    MenuItem menu_mail = menu.findItem(R.id.item_mail);
	    MenuItem menu_corbeille = menu.findItem(R.id.item_poubelle);
	   	int position = monadapter.getSelectedItem();
	   	if (position==-1)
	   	{
	   		menu_view.setEnabled(false);
		   	menu_edit.setEnabled(false);
		      menu_mail.setEnabled(false);
		      menu_corbeille.setEnabled(false);
	   	}
	   	else 
	   		menu_view.setEnabled(true);
	    if (position==0)
	    {
	      menu_edit.setEnabled(false);
	      menu_mail.setEnabled(false);
	      menu_corbeille.setEnabled(false);
	    }
	    else if  (position>0)
	    {
    	menu_edit.setEnabled(true);
    	menu_mail.setEnabled(true);
    	menu_corbeille.setEnabled(true);
	    }
	   
	    return true;  
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.item_add:
	      Toast.makeText(this, "Add selected", Toast.LENGTH_SHORT)
	          .show();
	       add_group();
	      break;
	    case R.id.item_poubelle:
    	  int position = monadapter.getSelectedItem();
		  int id_group = (monadapter).getId(position);
	  	  delete_groupe(id_group,position);
	      break;
	    case R.id.item_view:
		   Intent monintent = new Intent(this,TesteurActivity.class);
		//   monintent.putExtra("id_group", value);
		   int position2 = monadapter.getSelectedItem();
		   int id_group2 = (monadapter).getId(position2);
		   monintent.putExtra("id_group", id_group2);
		   startActivity(monintent);
		   break;
		   case R.id.item_mail:
			  int position3 = monadapter.getSelectedItem();
			  int id_group3 = (monadapter).getId(position3);
			  envoitexto(id_group3);
		      break;
		   case R.id.item_edit:
			  int position4 = monadapter.getSelectedItem();
			  int id_group4 = (monadapter).getId(position4);
			  editgroupe(id_group4,position4);
		      break;
		   case R.id.item_help :
			   AlertDialog.Builder alert = new AlertDialog.Builder(this); 
			   alert.setTitle("help page");
			   WebView webView = new WebView(this);
			   webView.loadUrl("file:///android_asset/help.html");
			   alert.setView(webView);
			   alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			       @Override
			       public void onClick(DialogInterface dialog, int id) {
			           dialog.dismiss();
			       }
			   });
			   alert.show();
		      break;
         case R.id.item_sauve :
                DBManager.sharedManager().sauve();
                break;
            case R.id.item_charge :
                DBManager.sharedManager().import_bd();
                break;
	    default:
	      break;
	    }
		return super.onOptionsItemSelected(item);
	}

	private void editgroupe(final int id_group3, final int position4) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.groupname);


		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		input.setText(monadapter.getItem(position4).getnomgroup());
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		String value = input.getText().toString();
		int  success = DBManager.sharedManager().updateGroupe(id_group3, value);
		if (success==1) listegroupe.get(position4).setnomgroup(value);
		  }
		});

		alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();

		
	}

	private void envoitexto(final int id_group3) {
		AlertDialog.Builder builder1=new AlertDialog.Builder(this);
		LayoutInflater mInflater= getLayoutInflater();
		final View mavue = mInflater.inflate(R.layout.textesms, null);
		final EditText input = (EditText) mavue.findViewById(R.id.edit_texto);
		final TextView sms_count = (TextView) mavue.findViewById(R.id.counter);
		sms_count.setText("0 / 160 ");
					final TextWatcher txwatcher = new TextWatcher() {
		   public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		   }

		   public void onTextChanged(CharSequence s, int start, int before, int count) {

		      sms_count.setText(String.valueOf(s.length()+" / 160 "));
		   }

		   public void afterTextChanged(Editable s) {
		   }
		};
		input.addTextChangedListener(txwatcher);
		builder1.setView(mavue);
		
		builder1.setNegativeButton(R.string.annuler,  new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			    // TODO Auto-generated method stub
			  
			   }
			  });
		builder1.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   ArrayList<Testeur> listetesteur;
				   listetesteur = DBManager.sharedManager().getAllTesteur(id_group3);
				   p= new ProgressDialog(MainActivity.this);
				   p.setMessage(context.getResources().getString(R.string.progress_message));
				   p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				   p.setMax(listetesteur.size());
				   p.setProgress(0);
				   p.setCancelable(true);
				   p.show();
				   String body= input.getText().toString();
				   sendsms(body,listetesteur);
			   }
			  });
		AlertDialog mydialog=builder1.create();
		mydialog.getWindow().setSoftInputMode(
		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		mydialog.show();
			   }
	
protected void sendsms(final String body ,final ArrayList<Testeur>listetesteur) {
	 new Thread() {
	        public void run() {
	            try {
		           registerReceiver(mbroadcastreceiver, new IntentFilter(ACTION_SMS_SENT));
		           SmsManager sms = SmsManager.getDefault();
				   for (int i=0; i<listetesteur.size();i++)
				   {  List<String> messages = sms.divideMessage(body);
				   for (String message : messages) {
			            sms.sendTextMessage(listetesteur.get(i).getTel(), null, message, PendingIntent.getBroadcast(
			                    getBaseContext(), 0, new Intent(ACTION_SMS_SENT), 0), null);
				   }
				   }
	            }
	            catch (Exception e) {
	            //    System.out.println("ERROR!!!" + e.getMessage());
	            }
	        }
	    }.start();
		
	}


	private void delete_groupe(int id_group, int position) {
		DBManager.sharedManager().deleteGroupe(id_group);	
		listegroupe.remove(position);
		monadapter.notifyDataSetChanged();
		malisteview.clearChoices();
	}


	private void add_group() {
		// TODO Auto-generated method stub
	//	ListView maliste = new ListView(this);
    	final CharSequence items[]= getResources().getStringArray(R.array.choix_import);	
		AlertDialog.Builder builder1=new AlertDialog.Builder(this);
		builder1.setItems(items, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: 
				ajoutmanuel();
				break;
			case 1 : 
				ajoutcsv();
				break;				
				}
			}
		});
		
builder1.setNegativeButton(R.string.annuler,  new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			   }
			  });
		
		builder1.show();
	}

protected void ajoutmanuel() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.groupname);


		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  creegroup(value);
		  }
		});

		alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	protected int creegroup(String value) {
		// TODO Auto-generated method stub
		Groupe nouveaugroupe=new Groupe(value);
		nouveaugroupe = DBManager.sharedManager().savegroupe(nouveaugroupe);
		if (!nouveaugroupe.equals(null))
		{
		listegroupe.add(nouveaugroupe);
	//	il suffit de modifier listenomgroupe , ...le notify se charge de recharger ce membre 
		monadapter.notifyDataSetChanged();
		return nouveaugroupe.getId(); 
		}
		else return 0;
	}
	


	protected void ajoutcsv() {
    	File smsgroupfile  = new File(Environment.getExternalStorageDirectory()+File.separator+"smsgroup_csv");
        if (!smsgroupfile.exists())
	        	{
	        	smsgroupfile.mkdir();
	        	smsgroupfile=null;
		        }		 
	      Intent chooseFile;
	      Intent intent;
	      chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
	      chooseFile.setType("file/*.csv");
	      intent = Intent.createChooser(chooseFile, getResources().getString(R.string.choixfichier));
	      startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);	     
		
	}

		 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		switch(requestCode) {
		case ACTIVITY_CHOOSE_FILE:
			{
				
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ","; 
			String cvsSplitBy2 = ";"; 
			if (resultCode == RESULT_OK){
			Uri uri = data.getData();
			String filename = uri.getPath();
			String shortname = uri.getLastPathSegment();
			String[] testeur;
			try {
				br = new BufferedReader(new FileReader(filename));
				if (br.ready())
			  	{
			  	 int new_groupe = creegroup(shortname);
			  	 
				while ((line = br.readLine()) != null) {
					Testeur nouveautesteur;	 
				        // use comma as separator
					if (line.contains(cvsSplitBy))
					testeur = line.split(cvsSplitBy);
					else 
					testeur =	line.split(cvsSplitBy2);
					
					switch (testeur.length){
					case 1:
						nouveautesteur = new Testeur( testeur[0], "", new_groupe);
						nouveautesteur = DBManager.sharedManager().savetesteur(nouveautesteur);
						break;
					case 2:
						nouveautesteur = new Testeur( testeur[0], testeur[1], new_groupe);
						nouveautesteur = DBManager.sharedManager().savetesteur(nouveautesteur);
						break; 
					}
				 }
			  	}
			}
				
			catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
									}
		  
				}
			}
		}
	}

		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}	
		
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();

		}
}
	


	
