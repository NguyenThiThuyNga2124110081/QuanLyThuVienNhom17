package com.example.Quanlythuvien;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class QuanlythuvienApplicationTests {

	@Autowired
	private LibraryService library;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void contextLoads() {
	}

	@Test
	void libraryListsAndDashboardLoadSeededData() {
		assertEquals(7, ((Number) library.dashboard().get("bookTitles")).intValue());
		assertEquals(3, ((Number) library.dashboard().get("readers")).intValue());
		assertTrue(library.books("").stream().anyMatch(book ->
				"S001".equals(book.get("maSach")) && "Nguyễn Nhật Ánh".equals(book.get("tacGia"))));
		assertEquals(4, library.categories().size());
		assertEquals(3, library.publishers().size());
		assertEquals(4, library.authors().size());
		assertEquals(3, library.readers("").size());
		assertEquals(1, library.accounts().size());
		assertTrue(library.loans().isEmpty());
		assertTrue(library.receipts().isEmpty());
	}

	@Test
	@Transactional
	void borrowingAndReturningRestoresAvailableCopies() {
		int copiesBefore = jdbc.queryForObject("SELECT soLuong FROM Sach WHERE maSach = 'S001'", Integer.class);
		library.createLoan(new LibraryService.LoanRequest("PMTEST01", "DG001", "TK001",
				LocalDate.now().plusDays(3), List.of(new LibraryService.LoanItem("S001", 2))));

		assertEquals(copiesBefore - 2,
				jdbc.queryForObject("SELECT soLuong FROM Sach WHERE maSach = 'S001'", Integer.class));
		Map<String, Object> receipt = library.returnLoan("PMTEST01");
		assertEquals(0L, receipt.get("tienPhat"));
		assertEquals(copiesBefore,
				jdbc.queryForObject("SELECT soLuong FROM Sach WHERE maSach = 'S001'", Integer.class));
		assertEquals("Đã trả", jdbc.queryForObject(
				"SELECT trangThai FROM PhieuMuon WHERE maPM = 'PMTEST01'", String.class));
	}
}
