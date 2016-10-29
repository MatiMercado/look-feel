package ar.edu.itba.hci.q2.g4.androidapp;

import java.io.Serializable;

public class Purchase implements Serializable {
	private final Purchase.state state;
	/**
	 * Convertion of R.string.order_state_confirmed, etc. into string
	 */
	private final String stateString;
	private String stateStringLong;
	private final String id;
	private final String confirmedDate;
	private String transportedDate;
	private String deliveredDate;
	private final String price;
	private String addressName;
	private String province;
	private String city;
	private String telephone;
	private String zipCode;
	private String street;
	private String number;
	private String floor;
	private String door;



	public enum state {
		CONFIRMED, TRANSPORTED, DELIVERED
	}

	public Purchase(Purchase.state state, String stateString, String id, String confirmedDate, String price) {
		this.state = state;
		this.stateString = stateString;
		this.id = id;
		this.confirmedDate = confirmedDate;
		this.price = price;
	}

	public Purchase(Purchase.state state, String stateString,
	                String stateStringLong, String id,
	                String confirmedDate, String transportedDate,
	                String deliveredDate, String price, String addressName,
	                String province, String city, String telephone,
	                String zipCode, String street, String number,
	                String floor, String door) {
		this.state = state;
		this.stateString = stateString;
		this.stateStringLong = stateStringLong;
		this.id = id;
		this.confirmedDate = confirmedDate;
		this.transportedDate = transportedDate;
		this.deliveredDate = deliveredDate;
		this.price = price;
		this.addressName = addressName;
		this.province = province;
		this.city = city;
		this.telephone = telephone;
		this.zipCode = zipCode;
		this.street = street;
		this.number = number;
		this.floor = floor;
		this.door = door;
	}

	public Purchase.state getState() {
		return state;
	}

	public String getStateString() {
		return stateString;
	}

	public String getStateStringLong() {
		return stateStringLong;
	}

	public String getId() {
		return id;
	}

	public String getConfirmedDate() {
		return confirmedDate;
	}

	public String getPrice() {
		return price;
	}

	public String getTransportedDate() {
		return transportedDate;
	}

	public String getDeliveredDate() {
		return deliveredDate;
	}

	public String getAddressName() {
		return addressName;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getStreet() {
		return street;
	}

	public String getNumber() {
		return number;
	}

	public String getFloor() {
		return floor;
	}

	public String getDoor() {
		return door;
	}
}
