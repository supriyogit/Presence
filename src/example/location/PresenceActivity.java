package example.location;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PresenceActivity extends Activity
{
	int SAMPLECOUNT = 10;
    private TextView locText;
    Button buttonStart, buttonStop;
    boolean GPSOn = false;
    
    LocationManager locationManager;
    myLocationListener locationListener;
    myOnClickListener onClickListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {    	
    	locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);    	
    	locationListener = new myLocationListener();
    	onClickListener = new myOnClickListener();
    	//locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locText = (TextView)findViewById(R.id.locText);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);        
        buttonStart.setOnClickListener(onClickListener);
        buttonStop.setOnClickListener(onClickListener);
    }
    
    public void startGPS()
    {
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    	GPSOn = true;
    }
    
    public void stopGPS()
    {
    	locationManager.removeUpdates(locationListener);
    	GPSOn = false;
    }
    public void detectLocation(float[] dataArr)
    {
    	int i;
    	double var = 0, avg = 0, sum = 0;
    	
    	for( i = 0; i < dataArr.length; i++)
    	{
    		sum = sum + dataArr[i];
    	}
    	avg = sum/(double)dataArr.length;
    	
    	sum = 0;
    	for(i = 0; i < dataArr.length; i++)
    	{
    		sum = sum + Math.pow((dataArr[i]-avg), 2.0);
    	}
    	var = sum/(double)dataArr.length; 
    	Toast.makeText(this, "Avg = " + avg + "Var = " + var, 3000).show();
    }
    
    public void displayData(String str)
    {
    	locText.setText(str);
    }
    
    public class myOnClickListener implements OnClickListener
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
    public class myLocationListener implements LocationListener
    {
    	int numSamples = 0;
    	float[] dataArr = new float[SAMPLECOUNT];
    	double lastLon = 0, lastLat = 0;
    	double currLon = 0, currLat = 0;
		@Override
		public void onLocationChanged(Location location) 
		{
			Toast.makeText(getApplicationContext(), "count = " + numSamples, 3000).show();
			if( numSamples < SAMPLECOUNT )
			{
				if( location != null )
				{
					currLon = location.getLongitude();
					currLat = location.getLatitude();
					displayData(currLon + "," + currLat);
					
					if( ( lastLat != 0 ) && ( lastLon != 0 ) )
					{
						float[] results = new float[3];
						Location.distanceBetween(currLat, currLon, lastLat, lastLon, results);
						dataArr[numSamples] = results[0];
					}
					lastLat = currLat;
					lastLon = currLon;
					numSamples++;
				}
			}
			else
			{
				numSamples = 0;
				detectLocation(dataArr);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
			// TODO Auto-generated method stub
    }
}