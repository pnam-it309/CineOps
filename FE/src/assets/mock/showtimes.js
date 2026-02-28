// Mock data suất chiếu cho 30 phim
// Khung giờ: Sáng sớm 08:00, Trưa 11:00, Chiều 14:30, Tối 18:00, Khuya 21:30
// Phòng: Phòng 01 (IMAX 3D - 40 ghế), Phòng 02 (2D Digital - 30 ghế)

const khungGios = [
  { id: 'kg-01-uuid', ten: 'Sáng sớm', gioBatDau: '08:00', gioKetThuc: '10:30' },
  { id: 'kg-02-uuid', ten: 'Trưa',     gioBatDau: '11:00', gioKetThuc: '13:30' },
  { id: 'kg-03-uuid', ten: 'Chiều',    gioBatDau: '14:30', gioKetThuc: '17:00' },
  { id: 'kg-04-uuid', ten: 'Tối',      gioBatDau: '18:00', gioKetThuc: '21:00' },
  { id: 'kg-05-uuid', ten: 'Khuya',    gioBatDau: '21:30', gioKetThuc: '23:59' },
];

const phongChieus = [
  { id: 'pc-001-uuid-001', tenPhong: 'Phòng 01', loaiManHinh: 'IMAX 3D',    tongGhe: 40 },
  { id: 'pc-002-uuid-002', tenPhong: 'Phòng 02', loaiManHinh: '2D Digital', tongGhe: 30 },
];

// Lấy 5 ngày gần nhất ứng với các thứ được chọn
const getNgayTheoThu = (thuIndexes) => {
  const result = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  for (let i = 0; i < 14; i++) {
    const d = new Date(today);
    d.setDate(today.getDate() + i);
    if (thuIndexes.includes(d.getDay())) {
      result.push(d.toISOString().split('T')[0]);
    }
    if (result.length >= 5) break;
  }
  return result;
};

let autoId = 1000;

const gen = (movieId, thuIndexes, phongIds, khungGioIds) => {
  const ngays = getNgayTheoThu(thuIndexes);
  const result = [];
  ngays.forEach(ngay => {
    phongIds.forEach(phongId => {
      khungGioIds.forEach(kgId => {
        const phong = phongChieus.find(p => p.id === phongId);
        const kg = khungGios.find(k => k.id === kgId);
        result.push({
          id: `sc-${++autoId}`,
          idPhim: movieId,
          idPhongChieu: phongId,
          tenPhong: phong.tenPhong,
          loaiManHinh: phong.loaiManHinh,
          tongGhe: phong.tongGhe,
          idKhungGio: kgId,
          tenKhungGio: kg.ten,
          gioBatDau: kg.gioBatDau,
          gioKetThuc: kg.gioKetThuc,
          ngayChieu: ngay,
          soGheTrong: Math.floor(Math.random() * phong.tongGhe),
          trangThai: 1,
        });
      });
    });
  });
  return result;
};

// 8 combo thứ chiếu — mỗi combo 5 ngày khác nhau
// để filter theo thứ ra kết quả khác nhau
const A = [1,2,3,4,5];     // T2 T3 T4 T5 T6
const B = [1,3,4,5,6];     // T2 T4 T5 T6 T7
const C = [2,3,4,5,6];     // T3 T4 T5 T6 T7
const D = [1,2,4,6,0];     // T2 T3 T5 T7 CN
const E = [1,3,5,6,0];     // T2 T4 T6 T7 CN
const F = [2,4,5,6,0];     // T3 T5 T6 T7 CN
const G = [1,2,3,6,0];     // T2 T3 T4 T7 CN
const H = [3,4,5,6,0];     // T4 T5 T6 T7 CN

const P1 = ['pc-001-uuid-001'];
const P2 = ['pc-002-uuid-002'];
const P12 = ['pc-001-uuid-001','pc-002-uuid-002'];

export const mockShowtimes = [
  ...gen(1,  A,  P12, ['kg-02-uuid','kg-03-uuid','kg-04-uuid','kg-05-uuid']), // Dune: T2-T6
  ...gen(2,  C,  P2,  ['kg-01-uuid','kg-02-uuid','kg-03-uuid']),              // KFP4: T3-T7
  ...gen(3,  B,  P1,  ['kg-03-uuid','kg-04-uuid','kg-05-uuid']),              // Godzilla: T2,T4-T7
  ...gen(4,  F,  P2,  ['kg-04-uuid','kg-05-uuid']),                           // Exhuma: T3,T5-CN
  ...gen(5,  D,  P1,  ['kg-02-uuid','kg-04-uuid','kg-05-uuid']),              // Civil War: T2,T3,T5,T7,CN
  ...gen(6,  H,  P2,  ['kg-01-uuid','kg-03-uuid','kg-04-uuid']),              // Fall Guy: T4-CN
  ...gen(7,  E,  P1,  ['kg-03-uuid','kg-04-uuid','kg-05-uuid']),              // Apes: T2,T4,T6,T7,CN
  ...gen(8,  A,  P2,  ['kg-02-uuid','kg-03-uuid']),                           // Challengers: T2-T6
  ...gen(9,  C,  P2,  ['kg-01-uuid','kg-02-uuid','kg-04-uuid']),              // Ghostbusters: T3-T7
  ...gen(10, H,  P1,  ['kg-04-uuid','kg-05-uuid']),                           // Gladiator II: T4-CN
  ...gen(11, A,  P12, ['kg-02-uuid','kg-03-uuid','kg-04-uuid','kg-05-uuid']), // Deadpool: T2-T6
  ...gen(12, D,  P2,  ['kg-01-uuid','kg-02-uuid','kg-03-uuid']),              // Inside Out 2: T2,T3,T5,T7,CN
  ...gen(13, C,  P1,  ['kg-03-uuid','kg-04-uuid','kg-05-uuid']),              // Furiosa: T3-T7
  ...gen(14, E,  P2,  ['kg-04-uuid','kg-05-uuid']),                           // Joker 2: T2,T4,T6,T7,CN
  ...gen(15, A,  P2,  ['kg-01-uuid','kg-02-uuid','kg-03-uuid']),              // Despicable: T2-T6
  ...gen(16, F,  P1,  ['kg-03-uuid','kg-04-uuid']),                           // Borderlands: T3,T5-CN
  ...gen(17, B,  P2,  ['kg-04-uuid','kg-05-uuid']),                           // Kraven: T2,T4-T7
  ...gen(18, G,  P1,  ['kg-02-uuid','kg-03-uuid','kg-04-uuid']),              // Venom 3: T2,T3,T4,T7,CN
  ...gen(19, H,  P2,  ['kg-01-uuid','kg-02-uuid']),                           // Mufasa: T4-CN
  ...gen(20, C,  P2,  ['kg-01-uuid','kg-02-uuid','kg-03-uuid']),              // Sonic 3: T3-T7
  ...gen(21, A,  P12, ['kg-02-uuid','kg-03-uuid','kg-04-uuid','kg-05-uuid']), // Fantastic Four: T2-T6
  ...gen(22, E,  P1,  ['kg-03-uuid','kg-04-uuid','kg-05-uuid']),              // Quiet Place: T2,T4,T6,T7,CN
  ...gen(23, F,  P2,  ['kg-02-uuid','kg-03-uuid','kg-04-uuid']),              // Twisters: T3,T5-CN
  ...gen(24, B,  P1,  ['kg-02-uuid','kg-04-uuid','kg-05-uuid']),              // Bad Boys: T2,T4-T7
  ...gen(25, D,  P12, ['kg-04-uuid','kg-05-uuid']),                           // Alien: T2,T3,T5,T7,CN
  ...gen(26, C,  P2,  ['kg-02-uuid','kg-03-uuid','kg-04-uuid']),              // It Ends: T3-T7
  ...gen(27, G,  P2,  ['kg-01-uuid','kg-03-uuid','kg-04-uuid']),              // Beetlejuice: T2,T3,T4,T7,CN
  ...gen(28, H,  P2,  ['kg-01-uuid','kg-02-uuid','kg-03-uuid']),              // Wild Robot: T4-CN
  ...gen(29, E,  P12, ['kg-03-uuid','kg-04-uuid','kg-05-uuid']),              // Wicked: T2,T4,T6,T7,CN
  ...gen(30, A,  P2,  ['kg-01-uuid','kg-02-uuid','kg-03-uuid']),              // Moana 2: T2-T6
];