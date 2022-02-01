package com.paypal.bfs.test.bookingserv.repository;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.paypal.bfs.test.bookingserv.api.model.Booking;

public interface BookingRepository {

	ResponseEntity<List<String>> findAll();

	ResponseEntity<String> save(Booking booking) throws SQLException, ParseException;

}
