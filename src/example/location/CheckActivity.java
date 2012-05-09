package example.location;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CheckActivity extends Activity
{
	public boolean isGPSFix = false;
	public boolean isGPSOn = false;
	private TextView locText;
    Button buttonStart, buttonStop;
	Location mLastLocation;
	long mLastLocUpdateTime = SystemClock.elapsedRealtime();	
	LocationManager locationManager;
	ArrayList<measurementVector> measurements = new ArrayList<measurementVector>();
	MyLocationListener locationListener;
    MyOnClickListener onClickListener;
    MyGPSListener myGPSListener;
    String prevState = "";
	
    int SAMPLES = 10;
    
	static int count = 0;
	int measurementIndex;
	
	public void onCreate(Bundle savedInstanceState)
	{
		locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);    	
    	locationListener = new MyLocationListener();
    	onClickListener = new MyOnClickListener();
    	myGPSListener = new MyGPSListener();
    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		locText = (TextView)findViewById(R.id.locText);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);        
        buttonStart.setOnClickListener(onClickListener);
        buttonStop.setOnClickListener(onClickListener);
        locationManager.addGpsStatusListener(myGPSListener);
	}
	
	public void startGPS()
    {
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    
    public void stopGPS()
    {
    	locationManager.removeUpdates(locationListener);
    }
    
    public void displayData(String str)
    {
    	
    	locText.setText(str);
    }
    
    void detectLocation()
    {
    	int i;
    	String currState = "";
    	boolean allFix = true;
    	float avgSNR, snrSum = 0;
    	float speed;
    	int avgNumSat, satSum = 0;
    	i = 0;
    	while((i < measurements.size()) && (allFix))
    	{
    		allFix = measurements.get(i).getIsGpsFix();
    		snrSum = snrSum + measurements.get(i).getSignalStrength();
    		satSum = satSum + measurements.get(i).getNumSatellites();   		
    		i++;
    	}
    	// gps has been fixed
    	if( i == measurements.size())
    	{
    		avgSNR = snrSum/measurements.size();
    		avgNumSat = satSum/measurements.size();
    		speed = getSpeed();
    		Toast.makeText(getApplicationContext(), "speed = " + speed + ": snr = " + avgSNR + ": sat = " + avgNumSat, 1000).show();
    		
    		// SNR should be between 30-35. Best is when it is around 40
    		// After trying it seems to be < 20 for indoors
    		if( (avgNumSat < 5) || (avgSNR < 20) )
    			currState = "indoor";
    		else if((prevState == "indoor") && (speed == 0 ))
    			currState = "indoor";
    		else
    			currState = "outdoor";
    	}
    	else
    	{
    		currState = "indoor";
    	}
    	prevState = currState;
    	displayData(currState + " : " + System.currentTimeMillis());
    	measurements.clear();
    }
   
    public float getSpeed()
    {
    	float speed = 0;
    	float results[] = new float[3];
    	float dist = 0;
    	
    	int i = 0;
    	while(i+1 < measurements.size())
    	{
    		Location.distanceBetween(measurements.get(i).getLat(), measurements.get(i).getLon(), measurements.get(i+1).getLat(), measurements.get(i).getLon(), results);
    		dist = dist + results[0];
    		i++;
    	}
    	speed = dist/(measurements.get(measurements.size()-1).getCurrTime() - measurements.get(0).getCurrTime());
    	return speed;
    }
    
	public class MyOnClickListener implements OnClickListener
    {
    	@Override
    	public void onClick(View src) 
    	{
    		switch (src.getId())
    		{
    			case R.id.buttonStart:
    				startGPS();
    				break;
    			case R.id.buttonStop:
    				stopGPS();
    				break;
    		}
    	}
    }
	
	public class MyGPSListener implements GpsStatus.Listener
	{		
		@Override
		public void onGpsStatusChanged(int event) 
		{
			switch(event) 
			{
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					measurementIndex = measurements.size();
					measurements.add(new measurementVector());
					
					if((SystemClock.elapsedRealtime() - mLastLocUpdateTime) < 3000)
					{
						GpsStatus myGpsStatus = locationManager.getGpsStatus(null);
						setSatCountAndSNR(myGpsStatus, measurementIndex);						
						isGPSFix = true;
					}
					else
						isGPSFix = false;
					
					measurements.get(measurementIndex).setCurrTime(System.currentTimeMillis());
					measurements.get(measurementIndex).setIsGpsFix(isGPSFix);
					break;
				case GpsStatus.GPS_EVENT_FIRST_FIX:
				{
					GpsStatus myGpsStatus = locationManager.getGpsStatus(null);
					measurementIndex = measurements.size();
					measurements.add(new measurementVector());
					setSatCountAndSNR(myGpsStatus, measurementIndex);
					measurements.get(measurementIndex).setCurrTime(System.currentTimeMillis());
					isGPSFix = true;
					measurements.get(measurementIndex).setIsGpsFix(isGPSFix);					
					break;
				}
				case GpsStatus.GPS_EVENT_STOPPED:
					isGPSOn = false;
					break;
				case GpsStatus.GPS_EVENT_STARTED:
					isGPSOn = true;
					break;
			}
		}
		void setSatCountAndSNR(GpsStatus myGpsStatus, int measurementIndex)
		{
			int satCount = 0;
			if( myGpsStatus != null)
			{
				Iterable<GpsSatellite> iSatellites = myGpsStatus.getSatellites();
				Iterator<GpsSatellite> it = iSatellites.iterator();
				float snrSum = 0;
				while ( it.hasNext() ) 
				{ 
					satCount++;
					GpsSatellite oSat = (GpsSatellite) it.next() ; 
					snrSum = snrSum + oSat.getSnr();
				} 
				measurements.get(measurementIndex).setNumSatellites(satCount);
				if( satCount != 0 )
					measurements.get(measurementIndex).setSignalStrength(snrSum/satCount);
				else
					measurements.get(measurementIndex).setSignalStrength(0);
			}			
		}
		
	}
	
	public class MyLocationListener implements LocationListener
    {
		@Override
		public void onLocationChanged(Location location) 
		{
			if(location == null) 
				return;
			else
			{
				mLastLocation = location;
				mLastLocUpdateTime = SystemClock.elapsedRealtime();
				if(measurements.get(measurementIndex).getIsLocUpdated() == false)
				{
					measurements.get(measurementIndex).setLat(location.getLatitude());
					measurements.get(measurementIndex).setLon(location.getLongitude());
					measurements.get(measurementIndex).setAccuracy(location.getAccuracy());
					measurements.get(measurementIndex).setIsLocUpdated(true);
				}
				if(measurements.size() >= SAMPLES)
					detectLocation();
			}
		}
		
		@Override
		public void onProviderDisabled(String provider) {}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}