package com.example.travelapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
	private String id;
	private String bookingId;
	private String guideId;
	private BigDecimal amount;
	private String category;
	private String note;
	private LocalDate spentAt;

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

	public String getGuideId() {
		return guideId;
	}

	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDate getSpentAt() {
		return spentAt;
	}

	public void setSpentAt(LocalDate spentAt) {
		this.spentAt = spentAt;
	}
}
