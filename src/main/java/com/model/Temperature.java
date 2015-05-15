package com.model;

public class Temperature {

	private Room room;
	private Integer value;
	
	public Temperature(Room r, Integer v) {
		room = r;
		value = v;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
}
