package com.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Fire {
	
	private Room room;
	
	
	public Fire(Room room) {
		 this.room = room;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Fire)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Fire fire = (Fire) obj;
		return new EqualsBuilder()
				.append(room, fire.getRoom())
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(room)
				.toHashCode();	
	}	
}
