export const mockMovies = [
  // --- PHIM ĐANG CHIẾU (NOW SHOWING) ---
  {
    id: 1,
    title: "Dune: Part Two",
    vietnameseTitle: "Dune: Hành Tinh Cát - Phần Hai",
    genre: "Sci-Fi, Adventure",
    duration: 166,
    rating: "T16",
    imdbRating: 8.9,
    releaseDate: "2024-03-01",
    poster: "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
    backdrop: "https://image.tmdb.org/t/p/original/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
    trailer: "https://www.youtube.com/watch?v=Way9Dexny3w",
    synopsis: "Hành trình huyền thoại của Paul Atreides tiếp tục khi anh hợp tác với Chani và người Fremen để trả thù những kẻ đã hủy hoại gia đình mình.",
    director: "Denis Villeneuve",
    cast: ["Timothée Chalamet", "Zendaya", "Rebecca Ferguson"],
    status: "now-showing"
  },
  {
    id: 2,
    title: "Kung Fu Panda 4",
    vietnameseTitle: "Kung Fu Panda 4",
    genre: "Animation, Action, Comedy",
    duration: 94,
    rating: "P",
    imdbRating: 7.6,
    releaseDate: "2024-03-08",
    poster: "https://image.tmdb.org/t/p/w500/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg",
    backdrop: "https://image.tmdb.org/t/p/original/1XDDXPXGiI8id7MrUxK36ke7gkX.jpg",
    trailer: "https://www.youtube.com/watch?v=_inKs4eeHiI",
    synopsis: "Sau khi Po được chọn trở thành Thủ lĩnh tinh thần của Thung lũng Bình Yên, anh cần tìm và huấn luyện một Chiến binh Rồng mới.",
    director: "Mike Mitchell",
    cast: ["Jack Black", "Awkwafina", "Viola Davis"],
    status: "now-showing"
  },
  {
    id: 3,
    title: "Godzilla x Kong: The New Empire",
    vietnameseTitle: "Godzilla x Kong: Đế Chế Mới",
    genre: "Action, Sci-Fi, Monster",
    duration: 115,
    rating: "K",
    imdbRating: 7.2,
    releaseDate: "2024-03-29",
    poster: "https://image.tmdb.org/t/p/w500/tMefBSflR6PGQLv7WvFPpKLZkyk.jpg",
    backdrop: "https://image.tmdb.org/t/p/original/j3Z3XktmWB1VqkS8i40mwZBD39m.jpg",
    trailer: "https://www.youtube.com/watch?v=lV1OOlGwExM",
    synopsis: "Hai titan cổ đại Godzilla và Kong đụng độ trong một trận chiến hoành tráng khi con người khám phá nguồn gốc bí ẩn của chúng.",
    director: "Adam Wingard",
    cast: ["Rebecca Hall", "Brian Tyree Henry", "Dan Stevens"],
    status: "now-showing"
  },
  {
    id: 4,
    title: "Mai",
    vietnameseTitle: "Mai",
    genre: "Romance, Drama",
    duration: 131,
    rating: "T18",
    imdbRating: 7.3,
    releaseDate: "2024-02-10",
    poster: "https://image.tmdb.org/t/p/w500/vi428Q22Z07YkO9C6Wwb0n7eJ7.jpg", // Poster phim Việt
    backdrop: "https://image.tmdb.org/t/p/original/47e4c6W83g6d2K7E4w1Z8k6.jpg", // Thay bằng ảnh hợp lý nếu link chết
    trailer: "https://www.youtube.com/watch?v=5ZnWpYJ_WKQ",
    synopsis: "Câu chuyện về cuộc đời của người phụ nữ tên Mai và mối tình chị em đầy sóng gió với Dương.",
    director: "Trấn Thành",
    cast: ["Phương Anh Đào", "Tuấn Trần", "Trấn Thành"],
    status: "now-showing"
  },

  // --- PHIM SẮP CHIẾU (COMING SOON) ---
  {
    id: 5,
    title: "Deadpool & Wolverine",
    vietnameseTitle: "Deadpool Và Wolverine",
    genre: "Action, Comedy, Superhero",
    duration: 120, // Dự kiến
    rating: "T18",
    imdbRating: 0, // Chưa chiếu
    releaseDate: "2024-07-26",
    poster: "https://image.tmdb.org/t/p/w500/jBWpWd74542QG4J6Jc63k5J.jpg", // Placeholder đẹp
    backdrop: "https://image.tmdb.org/t/p/original/dvBCdCohwWbsP5qAaglOXagDMtk.jpg",
    trailer: "https://www.youtube.com/watch?v=uJMCNJP2ipI",
    synopsis: "Wolverine đang phục hồi chấn thương thì gặp gỡ Deadpool lắm mồm. Họ lập team để đánh bại kẻ thù chung.",
    director: "Shawn Levy",
    cast: ["Ryan Reynolds", "Hugh Jackman", "Emma Corrin"],
    status: "coming-soon"
  },
  {
    id: 6,
    title: "Inside Out 2",
    vietnameseTitle: "Những Mảnh Ghép Cảm Xúc 2",
    genre: "Animation, Family, Fantasy",
    duration: 100,
    rating: "P",
    imdbRating: 0,
    releaseDate: "2024-06-14",
    poster: "https://image.tmdb.org/t/p/w500/kxX7s1r4Q8Mg6V.jpg", // Cần thay link thật nếu die
    backdrop: "https://image.tmdb.org/t/p/original/pWsD91G2R1Da3MPS6KNJySTyrDr.jpg",
    trailer: "https://www.youtube.com/watch?v=LEjhY15eCx0",
    synopsis: "Riley bước vào tuổi dậy thì, và trụ sở cảm xúc đón chào những thành viên mới: Lo Âu, Ghen Tị...",
    director: "Kelsey Mann",
    cast: ["Amy Poehler", "Maya Hawke", "Lewis Black"],
    status: "coming-soon"
  },
  {
    id: 7,
    title: "Joker: Folie à Deux",
    vietnameseTitle: "Joker: Điên Có Đôi",
    genre: "Crime, Drama, Musical",
    duration: 135,
    rating: "T18",
    imdbRating: 0,
    releaseDate: "2024-10-04",
    poster: "https://image.tmdb.org/t/p/w500/kZJ1Z8k.jpg", // Placeholder
    backdrop: "https://image.tmdb.org/t/p/original/5Eip60UDiPLASyKjmHCOchhC4.jpg", // Placeholder
    trailer: "https://www.youtube.com/watch?v=xy8aJw1vYHo",
    synopsis: "Phần tiếp theo của Joker (2019), khám phá mối quan hệ điên loạn giữa Arthur Fleck và Harley Quinn.",
    director: "Todd Phillips",
    cast: ["Joaquin Phoenix", "Lady Gaga", "Zazie Beetz"],
    status: "coming-soon"
  },
  {
    id: 8,
    title: "Despicable Me 4",
    vietnameseTitle: "Kẻ Trộm Mặt Trăng 4",
    genre: "Animation, Comedy, Family",
    duration: 95,
    rating: "P",
    imdbRating: 0,
    releaseDate: "2024-07-03",
    poster: "https://image.tmdb.org/t/p/w500/3w84hCFJATpiCO5g8hpdWVPBbmq.jpg",
    backdrop: "https://image.tmdb.org/t/p/original/fDmci71SMkfZM8RnCuXfZDPqD74.jpg",
    trailer: "https://www.youtube.com/watch?v=qQdBZXy15GU",
    synopsis: "Gru quay trở lại với những phi vụ mới cùng bầy Minions quậy phá.",
    director: "Chris Renaud",
    cast: ["Steve Carell", "Kristen Wiig", "Will Ferrell"],
    status: "coming-soon"
  }
];

export const mockBanners = [
  {
    id: 1,
    title: "Avatar: Dòng Chảy Của Nước",
    subtitle: "Đang Chiếu Tại Rạp",
    movieId: 1,
    image: "https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=1920&q=80"
  },
  {
    id: 2,
    title: "Dune: Hành Tinh Cát - Phần 2",
    subtitle: "Sắp Ra Mắt",
    movieId: 5,
    image: "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=1920&q=80"
  },
  {
    id: 3,
    title: "Ưu Đãi Đặc Biệt - Giảm 20%",
    subtitle: "Đặt Vé Ngay Hôm Nay",
    movieId: null,
    image: "https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=1920&q=80"
  }
];

export const mockPromotions = [
  {
    id: 1,
    title: "Ưu Đãi Cuối Tuần",
    description: "Giảm ngay 20% cho tất cả các vé đặt vào cuối tuần",
    image: "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?auto=format&fit=crop&w=600",
    validUntil: "2026-02-28",
    code: "WEEKEND20"
  },
  {
    id: 2,
    title: "Ưu Đãi Sinh Viên",
    description: "Sinh viên được giảm 15% khi xuất trình thẻ sinh viên",
    image: "https://images.unsplash.com/photo-1517604931442-7e0c8ed0083c?auto=format&fit=crop&w=600",
    validUntil: "2026-12-31",
    code: "STUDENT15"
  },
  {
    id: 3,
    title: "Đặt Sớm Nhận Ưu Đãi",
    description: "Đặt trước 12 giờ trưa và tiết kiệm 10% giá vé",
    image: "https://images.unsplash.com/photo-1542204172-3c35b6999679?auto=format&fit=crop&w=600",
    validUntil: "2026-03-31",
    code: "EARLY10"
  }
];

export const mockCinemas = [
  { id: 1, name: "CineOps Central", location: "District 1, HCM" },
  { id: 2, name: "CineOps Riverside", location: "District 7, HCM" },
  { id: 3, name: "CineOps Mall", location: "District 2, HCM" },
  { id: 4, name: "CineOps Plaza", location: "District 10, HCM" }
];

export const mockShowtimes = [
  { id: 101, movieId: 1, cinemaId: 1, time: "10:00 AM", format: "2D", price: 85000 },
  { id: 102, movieId: 1, cinemaId: 1, time: "01:30 PM", format: "3D", price: 125000 },
  { id: 103, movieId: 1, cinemaId: 1, time: "04:00 PM", format: "2D", price: 85000 },
  { id: 104, movieId: 1, cinemaId: 1, time: "07:30 PM", format: "IMAX", price: 155000 },
  { id: 105, movieId: 1, cinemaId: 1, time: "10:15 PM", format: "2D", price: 95000 },
  { id: 201, movieId: 1, cinemaId: 2, time: "05:00 PM", format: "2D", price: 85000 },
  { id: 301, movieId: 2, cinemaId: 1, time: "11:00 AM", format: "2D", price: 80000 },
  { id: 302, movieId: 2, cinemaId: 1, time: "02:00 PM", format: "2D", price: 80000 }
];

export const mockFood = [
  { 
    id: 1, 
    name: "Classic Combo", 
    description: "1 Large Popcorn + 1 Large Soda", 
    price: 95000, 
    image: "https://dummyimage.com/200x150/f59e0b/ffffff?text=Classic+Combo"
  },
  { 
    id: 2, 
    name: "Couple Combo", 
    description: "1 Large Popcorn + 2 Regular Sodas", 
    price: 125000, 
    image: "https://dummyimage.com/200x150/ec4899/ffffff?text=Couple+Combo"
  },
  { 
    id: 3, 
    name: "Family Deal", 
    description: "2 Large Popcorns + 4 Soft Drinks", 
    price: 245000, 
    image: "https://dummyimage.com/200x150/3b82f6/ffffff?text=Family+Deal"
  },
  { 
    id: 4, 
    name: "Single Popcorn", 
    description: "Large Sweet/Salty Popcorn", 
    price: 65000, 
    image: "https://dummyimage.com/200x150/10b981/ffffff?text=Popcorn"
  }
];


