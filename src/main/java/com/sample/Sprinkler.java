package com.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Sprinkler {

	private Room room;
	private boolean on = false;
	
	
	public Sprinkler(Room room) {
		this.room = room;
	}
	
	public Room getRoom() {
		return room;
	}

	public boolean getOn() {
		return on;
	}
	
	public void setOn(boolean on) {
		this.on = on;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Sprinkler)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Sprinkler sprinkler = (Sprinkler) obj;		
		return new EqualsBuilder()
				.append(room, sprinkler.getRoom())
				.append(on, sprinkler.getOn())
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(room)
				.append(on)
				.toHashCode();	
	}	
}
