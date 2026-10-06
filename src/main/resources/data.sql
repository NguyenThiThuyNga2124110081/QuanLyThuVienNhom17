INSERT INTO TheLoai (maTL, tenTL, moTa)
SELECT 'TL001', 'Văn học', 'Tiểu thuyết, truyện ngắn và thơ'
WHERE NOT EXISTS (SELECT 1 FROM TheLoai WHERE maTL = 'TL001');
INSERT INTO TheLoai (maTL, tenTL, moTa)
SELECT 'TL002', 'Công nghệ', 'Công nghệ thông tin và kỹ thuật'
WHERE NOT EXISTS (SELECT 1 FROM TheLoai WHERE maTL = 'TL002');
INSERT INTO TheLoai (maTL, tenTL, moTa)
SELECT 'TL003', 'Kinh tế', 'Kinh doanh, quản trị và tài chính'
WHERE NOT EXISTS (SELECT 1 FROM TheLoai WHERE maTL = 'TL003');
INSERT INTO TheLoai (maTL, tenTL, moTa)
SELECT 'TL004', 'Kỹ năng sống', 'Phát triển bản thân và kỹ năng'
WHERE NOT EXISTS (SELECT 1 FROM TheLoai WHERE maTL = 'TL004');

INSERT INTO NhaXuatBan (maNXB, tenNXB, diaChi)
SELECT 'NXB001', 'Nhà xuất bản Trẻ', 'TP. Hồ Chí Minh'
WHERE NOT EXISTS (SELECT 1 FROM NhaXuatBan WHERE maNXB = 'NXB001');
INSERT INTO NhaXuatBan (maNXB, tenNXB, diaChi)
SELECT 'NXB002', 'Nhà xuất bản Kim Đồng', 'Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM NhaXuatBan WHERE maNXB = 'NXB002');
INSERT INTO NhaXuatBan (maNXB, tenNXB, diaChi)
SELECT 'NXB003', 'Nhà xuất bản Thế Giới', 'Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM NhaXuatBan WHERE maNXB = 'NXB003');

INSERT INTO TacGia (maTG, tenTG, quocTich)
SELECT 'TG001', 'Nguyễn Nhật Ánh', 'Việt Nam'
WHERE NOT EXISTS (SELECT 1 FROM TacGia WHERE maTG = 'TG001');
INSERT INTO TacGia (maTG, tenTG, quocTich)
SELECT 'TG002', 'Dale Carnegie', 'Hoa Kỳ'
WHERE NOT EXISTS (SELECT 1 FROM TacGia WHERE maTG = 'TG002');
INSERT INTO TacGia (maTG, tenTG, quocTich)
SELECT 'TG003', 'Robert C. Martin', 'Hoa Kỳ'
WHERE NOT EXISTS (SELECT 1 FROM TacGia WHERE maTG = 'TG003');
INSERT INTO TacGia (maTG, tenTG, quocTich)
SELECT 'TG004', 'Haruki Murakami', 'Nhật Bản'
WHERE NOT EXISTS (SELECT 1 FROM TacGia WHERE maTG = 'TG004');

INSERT INTO TaiKhoan (maTK, tenDangNhap, matKhau, hoTen, vaiTro)
SELECT 'TK001', 'thuthu', 'demo-only', 'Thủ thư Demo', 'Thủ thư'
WHERE NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK = 'TK001');

INSERT INTO DocGia (maDG, hoTen, ngaySinh, soDienThoai, email, diaChi)
SELECT 'DG001', 'Trần Minh Anh', DATE '2002-04-12', '0901234567', 'minhanh@example.com', 'Quận 1, TP. Hồ Chí Minh'
WHERE NOT EXISTS (SELECT 1 FROM DocGia WHERE maDG = 'DG001');
INSERT INTO DocGia (maDG, hoTen, ngaySinh, soDienThoai, email, diaChi)
SELECT 'DG002', 'Lê Hoàng Nam', DATE '2001-09-25', '0912345678', 'hoangnam@example.com', 'Cầu Giấy, Hà Nội'
WHERE NOT EXISTS (SELECT 1 FROM DocGia WHERE maDG = 'DG002');
INSERT INTO DocGia (maDG, hoTen, ngaySinh, soDienThoai, email, diaChi)
SELECT 'DG003', 'Phạm Thu Hà', DATE '2003-02-08', '0987654321', 'thuha@example.com', 'Hải Châu, Đà Nẵng'
WHERE NOT EXISTS (SELECT 1 FROM DocGia WHERE maDG = 'DG003');

INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S001', 'Mắt biếc', 'TL001', 'NXB001', 2019, 12
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S001');
INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S002', 'Đắc nhân tâm', 'TL004', 'NXB003', 2020, 8
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S002');
INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S003', 'Clean Code', 'TL002', 'NXB003', 2021, 5
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S003');
INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S004', 'Rừng Na Uy', 'TL001', 'NXB001', 2022, 7
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S004');
INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S005', 'Tuổi trẻ đáng giá bao nhiêu', 'TL004', 'NXB001', 2020, 10
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S005');
INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S006', 'Nhà giả kim', 'TL001', 'NXB002', 2021, 6
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S006');
INSERT INTO Sach (maSach, tenSach, maTL, maNXB, namXuatBan, soLuong)
SELECT 'S007', 'Tư duy nhanh và chậm', 'TL003', 'NXB003', 2022, 4
WHERE NOT EXISTS (SELECT 1 FROM Sach WHERE maSach = 'S007');

INSERT INTO Sach_TacGia (maSach, maTG)
SELECT 'S001', 'TG001' WHERE NOT EXISTS (SELECT 1 FROM Sach_TacGia WHERE maSach = 'S001' AND maTG = 'TG001');
INSERT INTO Sach_TacGia (maSach, maTG)
SELECT 'S002', 'TG002' WHERE NOT EXISTS (SELECT 1 FROM Sach_TacGia WHERE maSach = 'S002' AND maTG = 'TG002');
INSERT INTO Sach_TacGia (maSach, maTG)
SELECT 'S003', 'TG003' WHERE NOT EXISTS (SELECT 1 FROM Sach_TacGia WHERE maSach = 'S003' AND maTG = 'TG003');
INSERT INTO Sach_TacGia (maSach, maTG)
SELECT 'S004', 'TG004' WHERE NOT EXISTS (SELECT 1 FROM Sach_TacGia WHERE maSach = 'S004' AND maTG = 'TG004');
INSERT INTO Sach_TacGia (maSach, maTG)
SELECT 'S005', 'TG001' WHERE NOT EXISTS (SELECT 1 FROM Sach_TacGia WHERE maSach = 'S005' AND maTG = 'TG001');
