package com.example.Quanlythuvien;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/api")
public class LibraryController {

	private final LibraryService library;

	public LibraryController(LibraryService library) {
		this.library = library;
	}

	@GetMapping("/dashboard")
	public Map<String, Object> dashboard() {
		return library.dashboard();
	}

	@GetMapping("/books")
	public Object books(@RequestParam(defaultValue = "") String q) {
		return library.books(q);
	}

	@PostMapping("/books")
	public ResponseEntity<Void> createBook(@RequestBody LibraryService.BookRequest book) {
		library.saveBook(book, null);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/books/{id}")
	public ResponseEntity<Void> updateBook(@PathVariable String id, @RequestBody LibraryService.BookRequest book) {
		library.saveBook(book, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/books/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable String id) {
		library.deleteBook(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/categories")
	public Object categories() {
		return library.categories();
	}

	@PostMapping("/categories")
	public ResponseEntity<Void> saveCategory(@RequestBody LibraryService.CategoryRequest category) {
		library.saveCategory(category);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/publishers")
	public Object publishers() {
		return library.publishers();
	}

	@PostMapping("/publishers")
	public ResponseEntity<Void> savePublisher(@RequestBody LibraryService.PublisherRequest publisher) {
		library.savePublisher(publisher);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/authors")
	public Object authors() {
		return library.authors();
	}

	@PostMapping("/authors")
	public ResponseEntity<Void> saveAuthor(@RequestBody LibraryService.AuthorRequest author) {
		library.saveAuthor(author);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/readers")
	public Object readers(@RequestParam(defaultValue = "") String q) {
		return library.readers(q);
	}

	@PostMapping("/readers")
	public ResponseEntity<Void> createReader(@RequestBody LibraryService.ReaderRequest reader) {
		library.saveReader(reader, null);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/readers/{id}")
	public ResponseEntity<Void> updateReader(@PathVariable String id, @RequestBody LibraryService.ReaderRequest reader) {
		library.saveReader(reader, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/readers/{id}")
	public ResponseEntity<Void> deleteReader(@PathVariable String id) {
		library.deleteReader(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/accounts")
	public Object accounts() {
		return library.accounts();
	}

	@PostMapping("/accounts")
	public ResponseEntity<Void> saveAccount(@RequestBody LibraryService.AccountRequest account) {
		library.saveAccount(account);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/loans")
	public Object loans() {
		return library.loans();
	}

	@PostMapping("/loans")
	public ResponseEntity<Void> createLoan(@RequestBody LibraryService.LoanRequest loan) {
		library.createLoan(loan);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/loans/{id}/return")
	public Map<String, Object> returnLoan(@PathVariable String id) {
		return library.returnLoan(id);
	}

	@GetMapping("/receipts")
	public Object receipts() {
		return library.receipts();
	}

	@PostMapping("/receipts")
	public ResponseEntity<Void> createReceipt(@RequestBody LibraryService.ReceiptRequest receipt) {
		library.createReceipt(receipt);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}

@RestControllerAdvice
class LibraryApiErrors {

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<Map<String, String>> invalidRequest(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	ResponseEntity<Map<String, String>> conflictingData(DataIntegrityViolationException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("error", "Dữ liệu bị trùng hoặc đang được sử dụng bởi một phiếu khác."));
	}
}
