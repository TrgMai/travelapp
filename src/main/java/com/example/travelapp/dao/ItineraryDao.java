package com.example.travelapp.dao;

import com.example.travelapp.model.Itinerary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItineraryDao extends BaseDao {
    private static final String SQL_FIND_BY_TOUR = """
            SELECT id, tour_id, day_no, title, place, activity, note
            FROM itineraries
            WHERE tour_id = ?
            ORDER BY day_no ASC
            """;

    private static final String SQL_DELETE_BY_TOUR = """
            DELETE FROM itineraries
            WHERE tour_id = ?
            """;

    private static final String SQL_INSERT = """
            INSERT INTO itineraries (id, tour_id, day_no, title, place, activity, note)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    public List<Itinerary> findByTourId(String tourId) {
        List<Itinerary> list = new ArrayList<>();
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_TOUR)) {
            ps.setString(1, tourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            logger.error("findByTourId itineraries", e);
        }
        return list;
    }

    public void replaceAllForTour(String tourId, List<Itinerary> items) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement pdel = c.prepareStatement(SQL_DELETE_BY_TOUR)) {
                pdel.setString(1, tourId);
                pdel.executeUpdate();
            }
            try (PreparedStatement pins = c.prepareStatement(SQL_INSERT)) {
                for (Itinerary it : items) {
                    if (it.getId() != null)
                        pins.setString(1, it.getId());
                    else
                        pins.setObject(1, null);
                    pins.setString(2, tourId);
                    if (it.getDayNo() != null)
                        pins.setInt(3, it.getDayNo());
                    else
                        pins.setNull(3, Types.INTEGER);
                    pins.setString(4, it.getTitle());
                    pins.setString(5, it.getPlace());
                    pins.setString(6, it.getActivity());
                    pins.setString(7, it.getNote());
                    pins.addBatch();
                }
                pins.executeBatch();
            }
            c.commit();
        } catch (SQLException e) {
            logger.error("replaceAllForTour itineraries", e);
        }
    }

    private static Itinerary map(ResultSet rs) throws SQLException {
        Itinerary it = new Itinerary();
        it.setId(rs.getString("id"));
        it.setTourId(rs.getString("tour_id"));
        int dn = rs.getInt("day_no");
        it.setDayNo(rs.wasNull() ? null : dn);
        it.setTitle(rs.getString("title"));
        it.setPlace(rs.getString("place"));
        it.setActivity(rs.getString("activity"));
        it.setNote(rs.getString("note"));
        return it;
    }
}
