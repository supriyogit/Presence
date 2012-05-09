package example.location;

public class inferredState
{
	String currPos;
	String currActivityState;
	long timestamp;
	public String getCurrPos() {
		return currPos;
	}
	public void setCurrPos(String currPos) {
		this.currPos = currPos;
	}
	public String getCurrActivityState() {
		return currActivityState;
	}
	public void setCurrActivityState(String currActivityState) {
		this.currActivityState = currActivityState;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}