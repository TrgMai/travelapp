package com.example.travelapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {
	private String id;
	private String fullName;
	private LocalDate dob;
	private String gender;
	private String idType;
	private String idNo;
	private String phone;
	private String email;
	private String note;
	private LocalDateTime createdAt;

	public Customer() {
	}

	public Customer(String id, String fullName, LocalDate dob, String gender, String idType, String idNo, String phone, String email, String note, LocalDateTime createdAt) {
		this.id = id;
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
		this.idType = idType;
		this.idNo = idNo;
		this.phone = phone;
		this.email = email;
		this.note = note;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Customer customer = (Customer) o;
		return Objects.equals(id, customer.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}