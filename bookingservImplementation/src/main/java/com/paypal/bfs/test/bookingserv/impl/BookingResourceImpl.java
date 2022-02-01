package com.paypal.bfs.test.bookingserv.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.bfs.test.bookingserv.api.BookingResource;
import com.paypal.bfs.test.bookingserv.api.model.Booking;
import com.paypal.bfs.test.bookingserv.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingResourceImpl implements BookingResource {

	@Autowired
	BookingService bookingService;

	@Override
	public ResponseEntity<String> create(Booking booking) {
		return bookingService.add(booking);
	}

	@Override
	public ResponseEntity<List<String>> fetch() {
		return bookingService.getAllBooking();
	}

}
