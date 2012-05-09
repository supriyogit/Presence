package example.location; 

public class measurementVector
{
	/* These are from the GPS */
	double lat, lon;	
	int numSatellites;
	float signalStrength;
	float accuracy;
	long currTime;
	boolean isGPSFix;
	boolean isLocUpdated = false;
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public int getNumSatellites() {
		return numSatellites;
	}
	public void setNumSatellites(int numSatellites) {
		this.numSatellites = numSatellites;
	}
	public float getSignalStrength() {
		return signalStrength;
	}
	public void setSignalStrength(float signalStrength) {
		this.signalStrength = signalStrength;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public long getCurrTime() {
		return currTime;
	}
	public void setCurrTime(long currTime) {
		this.currTime = currTime;
	}		
	public void setIsGpsFix(boolean isGPSFix) {
		this.isGPSFix = isGPSFix;
	}
	public boolean getIsGpsFix() {
		return this.isGPSFix;
	}
	public void setIsLocUpdated(boolean isLocUpdated) {
			this.isGPSFix = isLocUpdated;
	}
	public boolean getIsLocUpdated() {
			return this.isLocUpdated;
	}
}