package dts;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import dts.display.Reciever;
import dts.display.Reservations;
import dts.errors.BadRequest;

@Component
public class Jackson {
	

	private ObjectMapper jackson;
	
	@Autowired
	public Jackson(ObjectMapper jackson) {
		this.jackson = jackson;
	}
	
	// use JACKSON to store JSON String in the database
    public String fromMapToStringUsingJackson(Map<String, Object> attributes) {
  		// marshalling Java Object->JSON:
  		if (attributes != null) {
  			try {
  				return this.jackson.writeValueAsString(attributes);
  			}catch (Exception e) {
  				throw new BadRequest("Json input format is invalid!!!");
  			}
  		}else {
  			return "{}";
  		}
  	}
  	
    
    // use JACKSON to store JSON String in the database
    public String fromObjectToStringUsingJackson(Object attributes) {
  		// marshalling Java Object->JSON:
  		if (attributes != null) {
  			try {
  				return this.jackson.writeValueAsString(attributes);
  			}catch (Exception e) {
  				throw new BadRequest("Json input format is invalid!!!");
  			}
  		}else {
  			return "{}";
  		}
  	}
  	
  	public Map<String, Object> fromStringToMapUsingJackson(String attributes) {
 		// unmarshalling: JSON > Map
 		if (attributes != null) {
 			try {
 				return this.jackson.readValue(attributes, Map.class);
 			} catch (Exception e) {
 				throw new BadRequest("Json input format is invalid!!!");
 			}
 		}else {
 			return new HashMap<>();
 		}
 	}
  	
  	public Reservations fromStringToReservationsObject(String attributes) {
  		
  		if (attributes != null) {
 			try {
 				return this.jackson.readValue(attributes, Reservations.class);
 			} catch (Exception e) {
 				throw new BadRequest("Json input format is invalid!!!");
 			}
 		}else {
 			return null;
 		}
  	}
  	
  	
  	public Reservations fromMapToReservationsObject(Map<String, Object> attributes) {
  		
  		if (attributes != null) {
 			try {
 				String attributesAsString = this.jackson.writeValueAsString(attributes);
 				return this.jackson.readValue(attributesAsString, Reservations.class);
 			} catch (Exception e) {
 				throw new BadRequest("Json input format is invalid!!!");
 			}
 		}else {
 			return null;
 		}
  	}
  	
  	public Reciever fromMapToRecieverObject(Map<String, Object> attributes) {
  	
  		if (attributes != null) {
 			try {
 				String attributesAsString = this.jackson.writeValueAsString(attributes);
 				return this.jackson.readValue(attributesAsString, Reciever.class);
 			} catch (Exception e) {
 				throw new BadRequest("Json input format is invalid!!!");
 			}
 		}else {
 			return null;
 		}
  	}
  	
}
