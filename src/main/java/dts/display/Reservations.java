package dts.display;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservations {
	
	private List<ReservationDetails> reservasionsDetails;

	public Reservations(List<ReservationDetails> reservationsDetails) {
		super();
		this.reservasionsDetails = reservationsDetails;
	}
	
	public Reservations() {
		
	}

	@JsonProperty("reservasionsDetails")
	public List<ReservationDetails> getReservasionsDetails() {
		return reservasionsDetails;
	}

	public void setReservasionsDetails(List<ReservationDetails> reservasionsDetails) {
		this.reservasionsDetails = reservasionsDetails;
	}
	
	
	
}
