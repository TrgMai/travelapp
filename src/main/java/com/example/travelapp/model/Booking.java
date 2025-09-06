package com.example.travelapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


public class Booking {
	private String id;
	private String tourId;
	private String status;
	private BigDecimal totalPrice;
	private String note;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Booking() {
	}

	public Booking(String id, String tourId, String status, BigDecimal totalPrice,
	               String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.tourId = tourId;
		this.status = status;
		this.totalPrice = totalPrice;
		this.note = note;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTourId() {
		return tourId;
	}

	public void setTourId(String tourId) {
		this.tourId = tourId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
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

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Booking booking = (Booking) o;
		return Objects.equals(id, booking.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}