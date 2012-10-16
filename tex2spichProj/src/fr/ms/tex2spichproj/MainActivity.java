package fr.ms.tex2spichproj;

import java.util.ArrayList;
import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSeekBarChangeListener{

	// variables declarations
	
	protected static final int RESULT_SPEECH = 1;
	
	private TextView textView3 = null;
	private TextView textView4 = null;
	private TextView textView5 = null;	
	private Button button3 = null;
	private Button button4 = null;
	private ImageButton button5 = null;
	
	private SeekBar speedBar = null;
	
	private TextToSpeech tts = null;
	
	private LocationManager lm = null;
	private LocationListener ll = null;
	private String bearing = "pas de satellite";
	private String speed = "pas de satellite";
	private double speedAuto = 0;
	private double speedLastAuto = 0;
	private double speedTreshold = 0.1; 
	private long speedTimeTreshold = 5;
	private Date speedNow = null;
	private Date speedBefore = null;
	
	//positions : 
	@SuppressWarnings("unused")
	private String latitude = "pas de satellite";
	@SuppressWarnings("unused")
	private String longitude = "pas de satellite";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
    //TextView creation
    textView3 = new TextView(this);
    textView3 = (TextView) findViewById(R.id.speedView);
    textView4 = new TextView(this);
    textView4 = (TextView) findViewById(R.id.bearing);
    textView5 = new TextView(this);
    textView5 = (TextView) findViewById(R.id.speak);
    textView5.setText("Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
    
    //SpeedBar
    speedBar = (SeekBar) findViewById(R.id.seekBarSpeed);
    speedBar.setOnSeekBarChangeListener(this);
    speedBar.setContentDescription("Régalge seuil vitesse auto");
    
    
    // edit text creation
    //editText = new EditText(this);
    //editText = (EditText) findViewById(R.id.editTextInvitation);
	  
	//location manager creation
	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	ll = new MyLocationListener();		
	//loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
   
	//dates creation
//	now = new Date();
//	Log.i("new Date() : now.getTime", " = " + now.getTime());
	speedBefore = new Date();
	
	//2point creation
	/**
	point1 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	point2 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);	
	point1.setLatitude(48.1);
    point1.setLongitude(-4.1);
    point2.setLatitude(48.9);
    point2.setLongitude(-4.9);
    int dist1to2 = (int)(point1.distanceTo(point2))/1000  ;
    test = (String.valueOf(dist1to2));
    textView5.setText(test);
    **/
	
	//OnInitListener Creation
	OnInitListener onInitListener = new OnInitListener() {
		@Override
		public void onInit(int status) {
			//Toast.makeText(getApplicationContext(), (Integer.valueOf(status)).toString(), Toast.LENGTH_SHORT).show();
		}
	};
	
    // tts creation
	tts = new TextToSpeech(this, onInitListener);
	
	// button creation
    button3 = new Button(this);
    button3 = (Button) findViewById(R.id.buttonSpeed);
	button4 = new Button(this);
    button4 = (Button) findViewById(R.id.buttonBearing);
	button5 = new ImageButton(this);
    button5 = (ImageButton) findViewById(R.id.buttonSpeak);

    // OnClickListener creation
    View.OnClickListener onclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v== button3){
				tts.speak("vitesse : " + speed, TextToSpeech.QUEUE_FLUSH, null);
			}
			if (v== button4){
				tts.speak("cap : " + bearing, TextToSpeech.QUEUE_FLUSH, null);
			}
			if (v== button5){
				 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	             intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");	 
	                try {
	                    startActivityForResult(intent, RESULT_SPEECH);
	                    //textView5.setText("");
	                } catch (ActivityNotFoundException a) {
	                    Toast.makeText(getApplicationContext(),"Pas de reconnaissance",Toast.LENGTH_SHORT).show();
	                }//end of catch
	            }// end of if button5
	        }// end of onclick		
		}; //end of new View.LocationListener	
	
	// button activation
	button3.setOnClickListener(onclickListener);
	button4.setOnClickListener(onclickListener);
	button5.setOnClickListener(onclickListener);
	
    }//end of oncreate

  @Override
  protected void onResume() {
    super.onResume();
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    //tts.speak("resume", tts.QUEUE_FLUSH, null);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    lm.removeUpdates(ll);
    //tts.speak("pause", tts.QUEUE_FLUSH, null);
  }
  
  /* Remove the locationlistener updates when Activity is stopped */
  @Override
  protected void onStop() {
    super.onStop();
    //tts.speak("stop", tts.QUEUE_FLUSH, null);
    tts.stop();
  }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
		//a revoir mais pas trop
        switch (requestCode) {
        	case RESULT_SPEECH: {
        		if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                	if ( (text.get(0).equals("vitesse"))){
                		tts.speak("vitesse : " + speed + "noeuds", TextToSpeech.QUEUE_FLUSH, null);
                	}
                	else if ( (text.get(0).equals("cap"))){
                		tts.speak("cap : " + bearing, TextToSpeech.QUEUE_FLUSH, null);
                	}
                	else {
                		Toast.makeText(getApplicationContext(),text.get(0),Toast.LENGTH_SHORT).show();
                	}
        		}
            break;
        	}// end of case
        }//end of switch 
    }//end of on Activity result 
	
	//method to round 1 decimal
	//public double arrondiLat(double val) {return (Math.floor(val*1000))/1000;}
	//public double arrondiLong(double val) {return (Math.floor(val*100))/100;}
	public double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	//public double arrondiBearing(double val) {return (Math.floor(val*100))/100;}

    public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());			
			speed = String.valueOf(arrondiSpeed(loc.getSpeed()*(1.94)));
			bearing = String.valueOf((int)loc.getBearing());
			
			speedAuto = arrondiSpeed(loc.getSpeed()*(1.94));
			speedNow = new Date();
			//Log.i("new Date() : now.getTime", " = " + SpeedNow.getTime());
			
			if (   	(
						   ( speedAuto < speedLastAuto - speedTreshold ) 
						|| ( speedAuto > speedLastAuto + speedTreshold )
					)
					&&
					(
					  (speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold*1000
					)
					
				)   {
			tts.stop();
			tts.speak("vitesse : " + speed + "noeuds", TextToSpeech.QUEUE_FLUSH, null);
			speedLastAuto = speedAuto;
			speedBefore = new Date();
			}
			
			//displaying value
			textView3.setText(speed);
			textView4.setText(bearing);    
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(),"Gps Disabled",Toast.LENGTH_SHORT).show();	
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show();	
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//Log.i("LocationListener","onStatusChanged");
		}
    	
    } //end of MyLocationListener

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		speedTreshold = (double) progress/10;
		textView5.setText("Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
		seekBar.setContentDescription(Double.valueOf(speedTreshold).toString() + "noeuds");
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		seekBar.setContentDescription("Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
		
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//textView5.setContentDescription("On stop Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
		tts.speak(" Le Seuil de la vitesse auto est réglé à : " + Double.valueOf(speedTreshold).toString(), TextToSpeech.QUEUE_ADD, null);
	}
 
}//end of Activity
