package com.example.travelapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
	private String id;
	private String bookingId;
	private String type;
	private BigDecimal amount;
	private LocalDateTime paidAt;
	private String note;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Payment payment = (Payment) o;
		return Objects.equals(id, payment.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}