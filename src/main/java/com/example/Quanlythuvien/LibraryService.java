package com.example.Quanlythuvien;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {

	private final JdbcTemplate jdbc;

	public LibraryService(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Map<String, Object> dashboard() {
		return jdbc.queryForMap("""
				SELECT
					(SELECT COUNT(*) FROM Sach) AS "bookTitles",
					(SELECT COALESCE(SUM(soLuong), 0) FROM Sach) AS "bookCopies",
					(SELECT COUNT(*) FROM DocGia) AS "readers",
					(SELECT COUNT(*) FROM PhieuMuon WHERE trangThai = 'Đang mượn') AS "activeLoans",
					(SELECT COUNT(*) FROM PhieuMuon WHERE trangThai = 'Đang mượn' AND hanTra < CURRENT_DATE) AS "overdueLoans",
					(SELECT COUNT(*) FROM TheLoai) AS "categories"
				""");
	}

	public List<Map<String, Object>> books(String search) {
		String term = search == null ? "" : search.trim();
		List<Map<String, Object>> books = jdbc.queryForList("""
				SELECT s.maSach AS "maSach", s.tenSach AS "tenSach", s.maTL AS "maTL",
					t.tenTL AS "tenTheLoai", s.maNXB AS "maNXB", n.tenNXB AS "tenNXB",
					s.namXuatBan AS "namXuatBan", s.soLuong AS "soLuong"
				FROM Sach s
				JOIN TheLoai t ON t.maTL = s.maTL
				JOIN NhaXuatBan n ON n.maNXB = s.maNXB
				WHERE LOWER(s.tenSach) LIKE LOWER(?) OR LOWER(s.maSach) LIKE LOWER(?)
				ORDER BY s.tenSach
				""", "%" + term + "%", "%" + term + "%");
		Map<String, List<String>> authorsByBook = new LinkedHashMap<>();
		for (Map<String, Object> row : jdbc.queryForList("""
				SELECT st.maSach AS "maSach", a.tenTG AS "tenTG"
				FROM Sach_TacGia st JOIN TacGia a ON a.maTG = st.maTG
				ORDER BY st.maSach, a.tenTG
				""")) {
			authorsByBook.computeIfAbsent((String) row.get("maSach"), key -> new java.util.ArrayList<>())
					.add((String) row.get("tenTG"));
		}
		for (Map<String, Object> book : books) {
			book.put("tacGia", String.join(", ", authorsByBook.getOrDefault(book.get("maSach"), List.of())));
		}
		return books;
	}

	@Transactional
	public void saveBook(BookRequest book, String existingId) {
		require(book.maSach(), "Mã sách");
		require(book.tenSach(), "Tên sách");
		require(book.maTL(), "Thể loại");
		require(book.maNXB(), "Nhà xuất bản");
		if (book.soLuong() == null || book.soLuong() < 0) {
			throw new IllegalArgumentException("Số lượng sách phải lớn hơn hoặc bằng 0.");
		}

		if (existingId == null) {
			jdbc.update("""
					INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
					VALUES (?, ?, ?, ?, ?, ?)
					""", book.maSach().trim(), book.tenSach().trim(), book.maTL().trim(),
					book.maNXB().trim(), book.namXuatBan(), book.soLuong());
		} else {
			jdbc.update("""
					UPDATE Sach SET tenSach = ?, maTL = ?, maNXB = ?, namXuatBan = ?, soLuong = ?
					WHERE maSach = ?
					""", book.tenSach().trim(), book.maTL().trim(), book.maNXB().trim(),
					book.namXuatBan(), book.soLuong(), existingId);
		}
		jdbc.update("DELETE FROM Sach_TacGia WHERE maSach = ?", existingId == null ? book.maSach().trim() : existingId);
		if (book.maTGs() != null) {
			for (String authorId : book.maTGs().stream().distinct().toList()) {
				if (authorId != null && !authorId.isBlank()) {
					jdbc.update("INSERT INTO Sach_TacGia (maSach, maTG) VALUES (?, ?)",
							existingId == null ? book.maSach().trim() : existingId, authorId.trim());
				}
			}
		}
	}

	public void deleteBook(String id) {
		jdbc.update("DELETE FROM Sach WHERE maSach = ?", id);
	}

	public List<Map<String, Object>> categories() {
		return jdbc.queryForList("SELECT maTL AS \"maTL\", tenTL AS \"tenTL\", moTa AS \"moTa\" FROM TheLoai ORDER BY tenTL");
	}

	@Transactional
	public void saveCategory(CategoryRequest category) {
		require(category.maTL(), "Mã thể loại");
		require(category.tenTL(), "Tên thể loại");
		jdbc.update("""
				MERGE INTO TheLoai (maTL, tenTL, moTa) KEY (maTL) VALUES (?, ?, ?)
				""", category.maTL().trim(), category.tenTL().trim(), category.moTa());
	}

	public List<Map<String, Object>> publishers() {
		return jdbc.queryForList("SELECT maNXB AS \"maNXB\", tenNXB AS \"tenNXB\", diaChi AS \"diaChi\" FROM NhaXuatBan ORDER BY tenNXB");
	}

	@Transactional
	public void savePublisher(PublisherRequest publisher) {
		require(publisher.maNXB(), "Mã nhà xuất bản");
		require(publisher.tenNXB(), "Tên nhà xuất bản");
		jdbc.update("""
				MERGE INTO NhaXuatBan (maNXB, tenNXB, diaChi) KEY (maNXB) VALUES (?, ?, ?)
				""", publisher.maNXB().trim(), publisher.tenNXB().trim(), publisher.diaChi());
	}

	public List<Map<String, Object>> authors() {
		return jdbc.queryForList("SELECT maTG AS \"maTG\", tenTG AS \"tenTG\", quocTich AS \"quocTich\" FROM TacGia ORDER BY tenTG");
	}

	@Transactional
	public void saveAuthor(AuthorRequest author) {
		require(author.maTG(), "Mã tác giả");
		require(author.tenTG(), "Tên tác giả");
		jdbc.update("""
				MERGE INTO TacGia (maTG, tenTG, quocTich) KEY (maTG) VALUES (?, ?, ?)
				""", author.maTG().trim(), author.tenTG().trim(), author.quocTich());
	}

	public List<Map<String, Object>> readers(String search) {
		String term = search == null ? "" : search.trim();
		return jdbc.queryForList("""
				SELECT maDG AS "maDG", hoTen AS "hoTen", ngaySinh AS "ngaySinh",
					soDienThoai AS "soDienThoai", email AS "email", diaChi AS "diaChi"
				FROM DocGia
				WHERE LOWER(hoTen) LIKE LOWER(?) OR LOWER(maDG) LIKE LOWER(?)
					OR LOWER(COALESCE(email, '')) LIKE LOWER(?)
				ORDER BY hoTen
				""", "%" + term + "%", "%" + term + "%", "%" + term + "%");
	}

	@Transactional
	public void saveReader(ReaderRequest reader, String existingId) {
		require(reader.maDG(), "Mã độc giả");
		require(reader.hoTen(), "Họ tên độc giả");
		if (existingId == null) {
			jdbc.update("""
					INSERT INTO DocGia (maDG, hoTen, ngaySinh, soDienThoai, email, diaChi)
					VALUES (?, ?, ?, ?, ?, ?)
					""", reader.maDG().trim(), reader.hoTen().trim(), sqlDate(reader.ngaySinh()),
					reader.soDienThoai(), reader.email(), reader.diaChi());
		} else {
			jdbc.update("""
					UPDATE DocGia SET hoTen = ?, ngaySinh = ?, soDienThoai = ?, email = ?, diaChi = ?
					WHERE maDG = ?
					""", reader.hoTen().trim(), sqlDate(reader.ngaySinh()), reader.soDienThoai(),
					reader.email(), reader.diaChi(), existingId);
		}
	}

	public void deleteReader(String id) {
		jdbc.update("DELETE FROM DocGia WHERE maDG = ?", id);
	}

	public List<Map<String, Object>> accounts() {
		return jdbc.queryForList("""
				SELECT maTK AS "maTK", hoTen AS "hoTen", vaiTro AS "vaiTro"
				FROM TaiKhoan ORDER BY hoTen
				""");
	}

	@Transactional
	public void saveAccount(AccountRequest account) {
		require(account.maTK(), "Mã tài khoản");
		require(account.tenDangNhap(), "Tên đăng nhập");
		require(account.matKhau(), "Mật khẩu");
		require(account.hoTen(), "Họ tên");
		require(account.vaiTro(), "Vai trò");
		jdbc.update("""
				MERGE INTO TaiKhoan (maTK, tenDangNhap, matKhau, hoTen, vaiTro)
				KEY (maTK) VALUES (?, ?, ?, ?, ?)
				""", account.maTK().trim(), account.tenDangNhap().trim(), account.matKhau(),
				account.hoTen().trim(), account.vaiTro().trim());
	}

	public List<Map<String, Object>> loans() {
		return jdbc.queryForList("""
				SELECT p.maPM AS "maPM", p.maDG AS "maDG", d.hoTen AS "tenDocGia",
					p.maTK AS "maTK", p.ngayMuon AS "ngayMuon", p.hanTra AS "hanTra",
					p.trangThai AS "trangThai",
					(SELECT COUNT(*) FROM ChiTietPhieuMuon c WHERE c.maPM = p.maPM) AS "soDauSach",
					(SELECT COALESCE(SUM(c.tienPhat), 0) FROM ChiTietPhieuMuon c WHERE c.maPM = p.maPM) AS "tienPhat"
				FROM PhieuMuon p
				JOIN DocGia d ON d.maDG = p.maDG
				ORDER BY p.ngayMuon DESC, p.maPM DESC
				""");
	}

	@Transactional
	public void createLoan(LoanRequest loan) {
		require(loan.maPM(), "Mã phiếu mượn");
		require(loan.maDG(), "Độc giả");
		require(loan.maTK(), "Thủ thư");
		if (loan.items() == null || loan.items().isEmpty()) {
			throw new IllegalArgumentException("Hãy chọn ít nhất một cuốn sách.");
		}
		LocalDate today = LocalDate.now();
		LocalDate dueDate = loan.hanTra() == null ? today.plusDays(14) : loan.hanTra();
		if (dueDate.isBefore(today)) {
			throw new IllegalArgumentException("Hạn trả không được nằm trước ngày mượn.");
		}
		jdbc.update("""
				INSERT INTO PhieuMuon (maPM, maDG, maTK, ngayMuon, hanTra, trangThai)
				VALUES (?, ?, ?, ?, ?, 'Đang mượn')
				""", loan.maPM().trim(), loan.maDG().trim(), loan.maTK().trim(),
				Date.valueOf(today), Date.valueOf(dueDate));

		for (LoanItem item : loan.items()) {
			if (item.soLuong() == null || item.soLuong() < 1) {
				throw new IllegalArgumentException("Số lượng mượn phải ít nhất là 1.");
			}
			int changed = jdbc.update("UPDATE Sach SET soLuong = soLuong - ? WHERE maSach = ? AND soLuong >= ?",
					item.soLuong(), item.maSach(), item.soLuong());
			if (changed == 0) {
				throw new IllegalArgumentException("Sách " + item.maSach() + " không tồn tại hoặc không đủ số lượng.");
			}
			jdbc.update("""
					INSERT INTO ChiTietPhieuMuon (maPM, maSach, soLuong, tienPhat)
					VALUES (?, ?, ?, 0)
					""", loan.maPM().trim(), item.maSach().trim(), item.soLuong());
		}
	}

	@Transactional
	public Map<String, Object> returnLoan(String loanId) {
		List<Map<String, Object>> loan = jdbc.queryForList(
				"SELECT hanTra AS \"hanTra\", trangThai AS \"trangThai\" FROM PhieuMuon WHERE maPM = ?", loanId);
		if (loan.isEmpty()) {
			throw new IllegalArgumentException("Không tìm thấy phiếu mượn " + loanId + ".");
		}
		if (!"Đang mượn".equals(loan.getFirst().get("trangThai"))) {
			throw new IllegalArgumentException("Phiếu mượn này đã được trả.");
		}

		LocalDate today = LocalDate.now();
		LocalDate dueDate = ((Date) loan.getFirst().get("hanTra")).toLocalDate();
		long lateDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, today));
		List<Map<String, Object>> items = jdbc.queryForList(
				"SELECT maSach AS \"maSach\", soLuong AS \"soLuong\" FROM ChiTietPhieuMuon WHERE maPM = ?", loanId);
		long totalFine = 0;
		for (Map<String, Object> item : items) {
			int quantity = ((Number) item.get("soLuong")).intValue();
			long fine = lateDays * 1_000L * quantity;
			totalFine += fine;
			jdbc.update("UPDATE Sach SET soLuong = soLuong + ? WHERE maSach = ?", quantity, item.get("maSach"));
			jdbc.update("UPDATE ChiTietPhieuMuon SET ngayTra = ?, tienPhat = ? WHERE maPM = ? AND maSach = ?",
					Date.valueOf(today), fine, loanId, item.get("maSach"));
		}
		jdbc.update("UPDATE PhieuMuon SET trangThai = 'Đã trả' WHERE maPM = ?", loanId);
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("maPM", loanId);
		result.put("tienPhat", totalFine);
		result.put("soNgayTre", lateDays);
		return result;
	}

	public List<Map<String, Object>> receipts() {
		return jdbc.queryForList("""
				SELECT p.maPN AS "maPN", p.maTK AS "maTK", t.hoTen AS "tenThuThu",
					p.ngayNhap AS "ngayNhap", p.ghiChu AS "ghiChu",
					(SELECT COALESCE(SUM(c.soLuong), 0) FROM ChiTietPhieuNhap c WHERE c.maPN = p.maPN) AS "tongSoLuong",
					(SELECT COALESCE(SUM(c.soLuong * c.donGia), 0) FROM ChiTietPhieuNhap c WHERE c.maPN = p.maPN) AS "tongTien"
				FROM PhieuNhap p JOIN TaiKhoan t ON t.maTK = p.maTK
				ORDER BY p.ngayNhap DESC, p.maPN DESC
				""");
	}

	@Transactional
	public void createReceipt(ReceiptRequest receipt) {
		require(receipt.maPN(), "Mã phiếu nhập");
		require(receipt.maTK(), "Thủ thư");
		if (receipt.items() == null || receipt.items().isEmpty()) {
			throw new IllegalArgumentException("Hãy chọn ít nhất một đầu sách để nhập.");
		}
		LocalDate today = LocalDate.now();
		jdbc.update("""
				INSERT INTO PhieuNhap (maPN, maTK, ngayNhap, ghiChu) VALUES (?, ?, ?, ?)
				""", receipt.maPN().trim(), receipt.maTK().trim(), Date.valueOf(today), receipt.ghiChu());
		for (ReceiptItem item : receipt.items()) {
			if (item.soLuong() == null || item.soLuong() < 1 || item.donGia() == null || item.donGia() < 0) {
				throw new IllegalArgumentException("Số lượng phải lớn hơn 0 và đơn giá không được âm.");
			}
			int changed = jdbc.update("UPDATE Sach SET soLuong = soLuong + ? WHERE maSach = ?",
					item.soLuong(), item.maSach());
			if (changed == 0) {
				throw new IllegalArgumentException("Không tìm thấy sách " + item.maSach() + ".");
			}
			jdbc.update("""
					INSERT INTO ChiTietPhieuNhap (maPN, maSach, soLuong, donGia) VALUES (?, ?, ?, ?)
					""", receipt.maPN().trim(), item.maSach().trim(), item.soLuong(), item.donGia());
		}
	}

	private static Date sqlDate(LocalDate date) {
		return date == null ? null : Date.valueOf(date);
	}

	private static void require(String value, String label) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(label + " không được để trống.");
		}
	}

	public record BookRequest(String maSach, String tenSach, String maTL, String maNXB,
			Integer namXuatBan, Integer soLuong, List<String> maTGs) {
	}

	public record CategoryRequest(String maTL, String tenTL, String moTa) {
	}

	public record PublisherRequest(String maNXB, String tenNXB, String diaChi) {
	}

	public record AuthorRequest(String maTG, String tenTG, String quocTich) {
	}

	public record ReaderRequest(String maDG, String hoTen, LocalDate ngaySinh, String soDienThoai,
			String email, String diaChi) {
	}

	public record AccountRequest(String maTK, String tenDangNhap, String matKhau, String hoTen, String vaiTro) {
	}

	public record LoanRequest(String maPM, String maDG, String maTK, LocalDate hanTra, List<LoanItem> items) {
	}

	public record LoanItem(String maSach, Integer soLuong) {
	}

	public record ReceiptRequest(String maPN, String maTK, String ghiChu, List<ReceiptItem> items) {
	}

	public record ReceiptItem(String maSach, Integer soLuong, Integer donGia) {
	}
}
