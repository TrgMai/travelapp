package com.example.travelapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Tour {
	private String id;
	private String name;
	private String route;
	private int days;
	private BigDecimal basePrice;
	private String description;
	private String coverImageUrl;
	private LocalDateTime createdAt;
	private List<Itinerary> itineraries;

	public Tour() {
	}

	public Tour(String id, String name, String route, int days, BigDecimal basePrice, String description, String coverImageUrl, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.route = route;
		this.days = days;
		this.basePrice = basePrice;
		this.description = description;
		this.coverImageUrl = coverImageUrl;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
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
		Tour tour = (Tour) o;
		return Objects.equals(id, tour.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public List<Itinerary> getItineraries() {
		return itineraries;
	}

	public void setItineraries(List<Itinerary> itineraries) {
		this.itineraries = itineraries;
	}
}