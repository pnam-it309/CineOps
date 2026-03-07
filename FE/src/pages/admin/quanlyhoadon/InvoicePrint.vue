<template>
  <div class="invoice">
    <h2>HÓA ĐƠN THANH TOÁN</h2>

    <p>Mã hóa đơn: {{ invoice?.maHoaDon }}</p>
    <p>Ngày: {{ formatDate(invoice?.ngayTao) }}</p>
    <p>Khách hàng: {{ invoice?.tenKhachHang }}</p>

    <table border="1" width="100%">
      <thead>
        <tr>
          <th>STT</th>
          <th>Sản phẩm</th>
          <th>SL</th>
          <th>Đơn giá</th>
          <th>Thành tiền</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(item,index) in invoiceDetails" :key="index">
          <td>{{ index+1 }}</td>
          <td>{{ item.loai === 0 ? item.tenPhim : item.tenSanPham }}</td>
          <td>{{ item.soLuong || 1 }}</td>
          <td>{{ formatCurrency(item.donGia) }}</td>
          <td>{{ formatCurrency(item.donGia*(item.soLuong||1)) }}</td>
        </tr>
      </tbody>
    </table>

    <h3>Tổng: {{ formatCurrency(invoice?.tongTienThanhToan) }}</h3>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

const invoice = JSON.parse(sessionStorage.getItem("printInvoice"))
const invoiceDetails = JSON.parse(sessionStorage.getItem("printInvoiceDetails"))

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('vi-VN')
}

onMounted(() => {
  window.print()
})
</script>