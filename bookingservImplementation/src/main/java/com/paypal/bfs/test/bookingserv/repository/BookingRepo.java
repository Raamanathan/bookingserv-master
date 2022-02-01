package com.paypal.bfs.test.bookingserv.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.paypal.bfs.test.bookingserv.api.model.Booking;

@Repository
public class BookingRepo implements BookingRepository {

	Connection conn;

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	@PostConstruct
	void init() {
		try {
			conn = DriverManager.getConnection("jdbc:h2:mem:dcbapp", "sa", "password");
		} catch (SQLException e) {
			throw new RuntimeException("Error creating connection factory");
		}
	}

	@Override
	public ResponseEntity<List<String>> findAll() {

		PreparedStatement stmt = null;
		String query = "SELECT * from BOOKING";

		List<String> bookingList = new ArrayList<String>();
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Booking booking = new Booking();
				booking.setId(rs.getInt("id"));
				booking.setFirstName(rs.getString("first_name"));
				booking.setLastName(rs.getString("last_name"));
				booking.setAdditionalProperty("date_of_birth", rs.getDate("date_of_birth"));
				booking.setAdditionalProperty("checkin", rs.getDate("checkin"));
				booking.setAdditionalProperty("checkout", rs.getDate("checkout"));
				booking.setAdditionalProperty("totalprice", rs.getDouble("totalprice"));
				booking.setAdditionalProperty("deposit", rs.getDouble("deposit"));
				booking.setAdditionalProperty("address",
						new com.paypal.bfs.test.bookingserv.api.model.Address(rs.getString("line1"),
								rs.getString("line2"), rs.getString("city"), rs.getString("state"),
								rs.getString("country"), rs.getInt("zipcode")));
				bookingList.add(booking.toString());
			}
		} catch (SQLException e) {
			return new ResponseEntity<List<String>>(new ArrayList<String>(), HttpStatus.INTERNAL_SERVER_ERROR);

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					return new ResponseEntity<List<String>>(new ArrayList<String>(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		return new ResponseEntity<List<String>>(bookingList, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> save(Booking booking) throws SQLException, ParseException {
		PreparedStatement stmt = conn.prepareStatement("insert into Booking values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		try {
			stmt.setInt(1, booking.getId());
			stmt.setString(2, booking.getFirstName());
			stmt.setString(3, booking.getLastName());
			Map<String, Object> addlProps = booking.getAdditionalProperties();
			Date dob = getSqlDate(addlProps.get("date_of_birth"));
			Date currentDate = new Date(Calendar.getInstance().getTime().getTime());
			compareDates(dob, currentDate, "Date of Birth", "Today's date");// can handle age limit here
			stmt.setDate(4, dob);
			Date checkinDate = getSqlDate(addlProps.get("checkin"));
			Date checkoutDate = getSqlDate(addlProps.get("checkout"));
			compareDates(checkinDate, checkoutDate, "checkin date", "checkout date");
			compareDates(currentDate, checkoutDate, "Today's date", "checkout date");
			compareDates(currentDate, checkinDate, "Today's date", "checkin date");
			stmt.setDate(5, checkinDate);
			stmt.setDate(6, checkoutDate);
			stmt.setDouble(7, Double.parseDouble((String) addlProps.get("totalprice")));
			stmt.setDouble(8, Double.parseDouble((String) addlProps.get("deposit")));
			@SuppressWarnings("unchecked")
			Map<String, String> address = (Map<String, String>) addlProps.get("address");
			stmt.setString(9, address.get("line1"));
			stmt.setString(10, address.get("line2"));
			stmt.setString(11, address.get("city"));
			stmt.setString(12, address.get("state"));
			stmt.setString(13, address.get("country"));
			stmt.setInt(14, Integer.parseInt(address.get("zipcode")));
			if (stmt.executeUpdate() == 1) {
				return new ResponseEntity<String>(booking.toString(), HttpStatus.CREATED);
			} else {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		} catch (SQLException | ParseException e) {
			if (e.getMessage().contains("Unique index or primary key violation")) {
				throw new SQLException("Duplicate record cannot be created");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + " " + HttpStatus.BAD_REQUEST);
		} catch (NullPointerException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage() + " ID cannot be null " + HttpStatus.BAD_REQUEST);
		} finally {
			stmt.close();
		}
	}

	private void compareDates(Date checkinDate, Date checkoutDate, String string1, String string2) throws SQLException {
		//checkinDate.getTime();
		if (checkinDate.compareTo(checkoutDate) >= 0)
			throw new SQLException("Date error -> " + string1 + " cannot be equal to or greater than " + string2);
	}

	private Date getSqlDate(Object object) throws ParseException {
		java.util.Date date = sdf1.parse((String) object);
		sdf1.setLenient(false);
		return new java.sql.Date(date.getTime());
	}

}
