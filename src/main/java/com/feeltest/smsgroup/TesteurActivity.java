package com.feeltest.smsgroup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TesteurActivity extends Activity {

    protected static final String ACTION_SMS_SENT = "com.feeltest.smsgroup.SMS_SENT_ACTION";
    private static final int PICK_CONTACT_REQUEST = 0;
    private final int ID_MENU_MOVE = 1;
    private ListView malisteview;
    private ArrayList<Testeur> listetesteur;
    private MonArrayAdapterTesteur monadapter;
    private int id_group;
    private ArrayList<Integer> checked = new ArrayList<Integer>();
    private Context context;
    private BroadcastReceiver mbroadcastreceiver;
    protected ProgressDialog p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_testeur);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        malisteview = (ListView) findViewById(R.id.listView_main);
        Bundle monextra;
        context = getApplication().getApplicationContext();
        monextra = getIntent().getExtras();
        id_group = monextra.getInt("id_group");

        listetesteur = DBManager.sharedManager().getAllTesteur(id_group);
        monadapter = new MonArrayAdapterTesteur(this, R.layout.lignetesteur, listetesteur);
        malisteview.setAdapter(monadapter);

        malisteview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        malisteview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                toggle_check(position);
                invalidateOptionsMenu();
            }

            private void toggle_check(int position) {
                Integer id_testeur = monadapter.getId(position);
                if (checked.contains(id_testeur)) {
                    checked.remove(id_testeur);
                } else checked.add(id_testeur);
            }
        });

        mbroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message;
                p.incrementProgressBy(1);
                if (p.getMax() == p.getProgress()) {
                    unregisterReceiver(mbroadcastreceiver);
                }
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        message = "Error.";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        message = "Error: No service.";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        message = "Error: Null PDU.";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        message = "Error: Radio off.";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        break;
                }


            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        MenuItem menu_view = menu.findItem(R.id.item_view);
        menu.add(Menu.NONE, ID_MENU_MOVE, 5, R.string.move);
        menu_view.setEnabled(false);
        if (id_group == 0) menu.setGroupVisible(R.id.item_add, false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menu_edit = menu.findItem(R.id.item_edit);
        MenuItem menu_mail = menu.findItem(R.id.item_mail);
        MenuItem menu_move = menu.findItem(ID_MENU_MOVE);
        MenuItem menu_call = menu.findItem(R.id.item_call);
        MenuItem menu_filsms = menu.findItem(R.id.item_filsms);

        if (checked.size() != 1) {
            menu_edit.setEnabled(false);
            menu_call.setEnabled(false);
            menu_filsms.setEnabled(false);
        } else {
            menu_edit.setEnabled(true);
            menu_call.setEnabled(true);
            menu_filsms.setEnabled(true);
        }
        if (checked.size() == 0) {
            menu_mail.setEnabled(false);
            menu_move.setEnabled(false);

        } else {
            menu_mail.setEnabled(true);
            menu_move.setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.item_add1:
                ajoutmanuel();
                break;
            case R.id.item_add2:
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                break;
            case R.id.item_poubelle:
                delete_testeur(checked);
                break;
            case R.id.item_edit:
                int id_group4 = checked.get(0);
                editcontact(id_group4);
                break;
            case R.id.item_mail:
                creetexto(checked);
                break;

            case R.id.item_filsms:
                readcontactmessage(listetesteur.get(trouvePosition(checked.get(0))).getTel());
                break;
            case R.id.item_call:
                callcontact(listetesteur.get(trouvePosition(checked.get(0))).getTel());
                break;

            case ID_MENU_MOVE:
                move(checked);
                break;
            case R.id.item_help:
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void move(final ArrayList<Integer> checked2) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final List<Integer> listegroupeid = DBManager.sharedManager().getAllGroupId();
        ArrayList<String> listegroupename = DBManager.sharedManager().getAllGroupName();
        String[] liste = listegroupename.toArray(new String[0]);
        alert.setItems(liste, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int res = DBManager.sharedManager().moveTesteursto(checked2, listegroupeid.get(which));
                if (res > 0) {
                    for (int i = 0; i < checked.size(); i++) {
                        listetesteur.remove(trouvePosition(checked.get(i)));
                    }
                    monadapter.notifyDataSetChanged();
                }
            }
        });
        alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    private void readcontactmessage(final String telstring) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address",telstring);
        startActivity(smsIntent);

    }

    private void callcontact(final String telstring) {

    try

    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + telstring.toString()));
        startActivity(callIntent);
    }

    catch(
    ActivityNotFoundException activityException
    )

    {
       // Log.e("Calling a Phone Number", "Call failed", activityException);
    }

}
	private void editcontact(final int id_contact) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.telname);
		// Set an EditText view to get user input 
		LayoutInflater mInflater = getLayoutInflater();
		final View mavue = mInflater.inflate(R.layout.datatesteur_view, null);
		EditText vue_tel = (EditText) mavue.findViewById(R.id.editTel);
		EditText vue_name = (EditText) mavue.findViewById(R.id.editName);
		final int position =trouvePosition(id_contact);
		vue_tel.setText(listetesteur.get(position).getTel());
		vue_name.setText(listetesteur.get(position).getNomPrenom());
		alert.setView(mavue);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String tel = ((EditText) mavue.findViewById(R.id.editTel)).getText().toString();
			String name = ((EditText) mavue.findViewById(R.id.editName)).getText().toString();
			updateTesteur(id_contact,tel,name,position);
		  }
		});

		alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		
	}



	private void creetexto(final ArrayList<Integer> checked) {
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

				   }
				  });
			builder1.setPositiveButton("OK",  new DialogInterface.OnClickListener() {


				@Override
				   public void onClick(DialogInterface dialog, int which) {
				
					   p= new ProgressDialog(TesteurActivity.this);
					   p.setMessage(context.getResources().getString(R.string.progress_message));
					   p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					   p.setMax(checked.size());
					   p.setProgress(0);
					   p.setCancelable(true);
					   p.show();
					   String body= input.getText().toString();
					   sendsms(body);
				   }
				  });
			AlertDialog mydialog=builder1.create();
			mydialog.getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

			mydialog.show();
			
	}


	protected void sendsms(final String body) {
		 new Thread() {
		        public void run() {
		            try {
		           registerReceiver(mbroadcastreceiver, new IntentFilter(ACTION_SMS_SENT));
		           SmsManager sms = SmsManager.getDefault();	  
		 		   ArrayList<Testeur> selectesteur = new ArrayList<Testeur>() ;
				   for (int i = 0; i < checked.size(); i++) {
						int position = trouvePosition(checked.get(i));
						selectesteur.add(listetesteur.get(position));
					}
				   for (int i=0; i<selectesteur.size();i++)
				   {  List<String> messages = sms.divideMessage(body);
				   for (String message : messages) {
			            sms.sendTextMessage(selectesteur.get(i).getTel(), null, message, PendingIntent.getBroadcast(
			                    getBaseContext(), 0, new Intent(ACTION_SMS_SENT), 0), null);
				   }
				   }	
		            } catch (Exception e) {
		       //         System.out.println("ERROR!!!" + e.getMessage());
		            }
		        }
		    }.start();	
	}



	private void delete_testeur(ArrayList<Integer> checked) {
		if (id_group==0)
		{
		DBManager.sharedManager().deleteTesteur(checked);
		}
		else 
		{
			DBManager.sharedManager().moveTesteursto(checked, 0);
		}
		
		for (int i = 0; i < checked.size(); i++) {
			int position = trouvePosition(checked.get(i));
			listetesteur.remove(position);
		}
		checked.clear();
		
		monadapter.notifyDataSetChanged();
		malisteview.clearChoices();
		
		
	}


	private int trouvePosition(Integer cherche) {
		
		for (int i = 0; i < listetesteur.size(); i++) {
		if (listetesteur.get(i).getId()==cherche) return i;
		}
		return 0;	
	}



	protected void ajoutmanuel() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.telname);


		// Set an EditText view to get user input 
		LayoutInflater mInflater = getLayoutInflater();
		final View mavue = mInflater.inflate(R.layout.datatesteur_view, null);
		alert.setView(mavue);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String tel = ((EditText) mavue.findViewById(R.id.editTel)).getText().toString();
		  String name = ((EditText) mavue.findViewById(R.id.editName)).getText().toString();

		  creetesteur(tel,name);
		  }
		});

		alert.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	protected void creetesteur(String tel, String name) {
		Testeur nouveautesteur=new Testeur(tel, name , id_group);
		nouveautesteur = DBManager.sharedManager().savetesteur(nouveautesteur);
		if (nouveautesteur!=null)
		{
		listetesteur.add(nouveautesteur);
	//	il suffit de modifier listenomgroupe , ...le notify se charge de recharger ce membre 
		monadapter.notifyDataSetChanged();
		}
	}

	protected int updateTesteur(int id_contact, String tel, String name, int position) {
		Testeur nouveautesteur=new Testeur(tel);
		nouveautesteur.setId(id_contact);
		nouveautesteur.setNomPrenom(name);
		int res = DBManager.sharedManager().update(nouveautesteur);
		if (res==1)
		{
		listetesteur.get(position).setNomPrenom(name);
		listetesteur.get(position).setTel(tel);
		monadapter.notifyDataSetChanged();
		}
		return res;
		
	}

@Override
protected void onPause() {
	super.onPause();

}
@Override
protected void onResume() {
	super.onResume();
}
@Override
public void onActivityResult( int requestCode, int resultCode, Intent intent ) {

    super.onActivityResult( requestCode, resultCode, intent );
    if ( requestCode == PICK_CONTACT_REQUEST ) {

        if ( resultCode == RESULT_OK ) {
                Uri pickedPhoneNumber = intent.getData();
                    // read id
                        String id = pickedPhoneNumber.getLastPathSegment();
                        /** read names **/
                        String[] projection = {Phone.NUMBER,Phone.DISPLAY_NAME};
                        Cursor cursor = getContentResolver().query(pickedPhoneNumber, projection, null, null, null);
                        cursor.moveToFirst();

                        // Retrieve the phone number from the NUMBER column
                        int column = cursor.getColumnIndex(Phone.NUMBER);
                        String number = cursor.getString(column);
                        column=cursor.getColumnIndex(Phone.DISPLAY_NAME);
                        String name = cursor.getString(column);
                        creetesteur(number,name);
                    }
                }
            
            }
        
    }


