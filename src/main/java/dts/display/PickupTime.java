package dts.display;

import java.util.Date;

public class PickupTime {
	
	private Date start;
	private Date end;
	
	
	public PickupTime() {
		
	}
	
	public PickupTime(Date start, Date end) {
		super();
		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
	
	
}
