import { defineStore } from 'pinia';

export const useBookingStore = defineStore('booking', {
  state: () => ({
    movieId: null,
    movieTitle: '',
    cinemaId: null,
    cinemaName: '',
    showtimeId: null,
    showtimeDate: '',
    showtimeTime: '',
    selectedSeats: [], // Array of seat objects { row: 'A', col: 1, type: 'regular', price: 85000 }
    selectedFood: [], // Array of food objects { id: 1, name: 'Popcorn', quantity: 1, price: 50000 }
    totalPrice: 0,
    status: 'pending' // pending, paid, cancelled
  }),

  getters: {
    seatsCount: (state) => state.selectedSeats.length,
    foodTotal: (state) => state.selectedFood.reduce((acc, item) => acc + (item.price * item.quantity), 0),
    seatsTotal: (state) => state.selectedSeats.reduce((acc, item) => acc + item.price, 0),
    finalTotal() {
      return this.seatsTotal + this.foodTotal;
    }
  },

  actions: {
    setBasicInfo(info) {
      this.movieId = info.movieId;
      this.movieTitle = info.movieTitle;
      this.cinemaId = info.cinemaId;
      this.cinemaName = info.cinemaName;
      this.showtimeId = info.showtimeId;
      this.showtimeDate = info.showtimeDate;
      this.showtimeTime = info.showtimeTime;
    },

    toggleSeat(seat) {
      const index = this.selectedSeats.findIndex(s => s.id === seat.id);
      if (index > -1) {
        this.selectedSeats.splice(index, 1);
      } else {
        this.selectedSeats.push(seat);
      }
    },

    addFood(food) {
      const existing = this.selectedFood.find(f => f.id === food.id);
      if (existing) {
        existing.quantity++;
      } else {
        this.selectedFood.push({ ...food, quantity: 1 });
      }
    },

    removeFood(foodId) {
      const index = this.selectedFood.findIndex(f => f.id === foodId);
      if (index > -1) {
        if (this.selectedFood[index].quantity > 1) {
          this.selectedFood[index].quantity--;
        } else {
          this.selectedFood.splice(index, 1);
        }
      }
    },

    resetBooking() {
      this.$reset();
    }
  }
});
