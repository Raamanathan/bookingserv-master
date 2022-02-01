package com.paypal.bfs.test.bookingserv.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.paypal.bfs.test.bookingserv.api.model.Booking;
import com.paypal.bfs.test.bookingserv.repository.BookingRepo;

@Service
public class BookingService {

	@Autowired
	BookingRepo bookingRepo;

	public ResponseEntity<List<String>> getAllBooking() {
		return bookingRepo.findAll();
	}

	public ResponseEntity<String> add(Booking booking) {
		try {
			return bookingRepo.save(booking);
		} catch (SQLException | ParseException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + " "+ HttpStatus.BAD_REQUEST);
		}

	}

}
