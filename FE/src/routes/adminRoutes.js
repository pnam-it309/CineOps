import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const AdminLayout = () => import('@/layout/AdminLayout.vue');
export const printRoutes = {
    path: '/print/invoice',
    name: 'InvoicePrintStandalone', 
    component: () => import('@/pages/admin/quanlyhoadon/InvoicePrint.vue'),
    meta: { title: 'In hóa đơn' }
};

export const adminRoutes = {
    path: ROUTES_CONSTANTS.ADMIN.path, // /admin
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'], title: 'Quản trị' },
    children: [
        {
            path: '',
            redirect: { name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name }
        },
        
        // ── Root Menu (1-Click) ─────────────────────────────────
        {
            path: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.path,
            name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name,
            component: () => import('@/pages/admin/tongquan/AdminDashboard.vue'),
            meta: { title: 'Tổng quan' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.POS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.POS.name,
            component: () => import('@/pages/admin/AdminSales.vue'),
            meta: { title: 'Bán vé tại quầy' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.CHECKIN.path,
            name: ROUTES_CONSTANTS.ADMIN.children.CHECKIN.name,
            component: () => import('@/pages/admin/AdminCheckin.vue'),
            meta: { title: 'Check-in vé' }
        },

        // ── Sub-Menu: Quản lý Giao dịch ────────────────────────
        {
            path: ROUTES_CONSTANTS.ADMIN.children.INVOICES.path,
            name: ROUTES_CONSTANTS.ADMIN.children.INVOICES.name,
            component: () => import('@/pages/admin/quanlyhoadon/AdminInvoices.vue'),
            meta: { title: 'Quản lý hóa đơn' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.TICKETS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.TICKETS.name,
            component: () => import('@/pages/admin/quanlyve/TicketList.vue'),
            meta: { title: 'Quản lý vé' }
        },

        // ── Sub-Menu: Quản lý Phim & Rạp ────────────────────────
        // Quản lý phim
        {
            path: ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE.path,
            name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_SCHEDULE.name,
            component: () => import('@/pages/admin/quanliphim/AdminMovies.vue'),
            meta: { title: 'Quản lý phim' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.MOVIES_ADD.path,
            name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_ADD.name,
            component: () => import('@/pages/admin/quanliphim/MovieFormPage.vue'),
            meta: { title: 'Thêm phim mới' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.MOVIES_EDIT.path,
            name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_EDIT.name,
            component: () => import('@/pages/admin/quanliphim/MovieFormPage.vue'),
            meta: { title: 'Chỉnh sửa phim' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.MOVIES_DETAIL.path,
            name: ROUTES_CONSTANTS.ADMIN.children.MOVIES_DETAIL.name,
            component: () => import('@/pages/admin/quanliphim/MovieDetailPage.vue'),
            meta: { title: 'Chi tiết phim' }
        },
        
        // Lịch chiếu (Danh sách - Sơ đồ chuyển trong table)
        {
            path: ROUTES_CONSTANTS.ADMIN.children.SCHEDULE.path + '/list',
            name: ROUTES_CONSTANTS.ADMIN.children.SCHEDULE.children.LIST.name,
            component: () => import('@/pages/admin/lichChieu/AdminSchedule.vue'),
            meta: { title: 'Lịch chiếu' }
        },

        // Suất chiếu (tách riêng)
        {
            path: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.path,
            name: ROUTES_CONSTANTS.ADMIN.children.SHOWTIMES.name,
            component: () => import('@/pages/admin/suatchieu/AdminShowtimes.vue'),
            meta: { title: 'Suất chiếu' }
        },

        // Phòng chiếu
        {
            path: ROUTES_CONSTANTS.ADMIN.children.ROOMS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.ROOMS.name,
            component: () => import('@/pages/admin/AdminRooms.vue'),
            meta: { title: 'Phòng chiếu' }
        },

        // Ghế
        {
            path: ROUTES_CONSTANTS.ADMIN.children.SEATS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.SEATS.name,
            component: () => import('@/pages/admin/ghe/AdminSeats.vue'),
            meta: { title: 'Ghế' }
        },

        // ── Sub-Menu: Dịch vụ & Marketing ───────────────────────
        {
            path: ROUTES_CONSTANTS.ADMIN.children.FOOD.path,
            name: ROUTES_CONSTANTS.ADMIN.children.FOOD.name,
            component: () => import('@/pages/admin/sanphamdikem/AdminFood.vue'),
            meta: { title: 'Sản phẩm & Combo' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS.name,
            component: () => import('@/pages/admin/quanliphieugiamgia/AdminVouchers.vue'),
            meta: { title: 'Phiếu giảm giá' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS_ADD.path,
            name: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS_ADD.name,
            component: () => import('@/pages/admin/quanliphieugiamgia/VoucherFormPage.vue'),
            meta: { title: 'Phát hành phiếu giảm giá' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS_EDIT.path,
            name: ROUTES_CONSTANTS.ADMIN.children.VOUCHERS_EDIT.name,
            component: () => import('@/pages/admin/quanliphieugiamgia/VoucherFormPage.vue'),
            meta: { title: 'Chỉnh sửa phiếu giảm giá' }
        },

        // ── Sub-Menu: Hệ thống & Thống kê ──────────────────────
        {
            path: ROUTES_CONSTANTS.ADMIN.children.STATISTICS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.STATISTICS.name,
            component: () => import('@/pages/admin/thongke/AdminReports.vue'),
            meta: { title: 'Thống kê' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS.path,
            name: ROUTES_CONSTANTS.ADMIN.children.CUSTOMERS.name,
            component: () => import('@/pages/admin/quanlikhachhang/AdminCustomers.vue'),
            meta: { title: 'Khách hàng' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.STAFF.path,
            name: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
            component: () => import('@/pages/admin/quanlinhanvien/AdminStaff.vue'),
            meta: { title: 'Nhân viên' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.STAFF_ADD.path,
            name: ROUTES_CONSTANTS.ADMIN.children.STAFF_ADD.name,
            component: () => import('@/pages/admin/quanlinhanvien/StaffFormPage.vue'),
            meta: { title: 'Thêm nhân viên mới' }
        },
        {
            path: ROUTES_CONSTANTS.ADMIN.children.STAFF_EDIT.path,
            name: ROUTES_CONSTANTS.ADMIN.children.STAFF_EDIT.name,
            component: () => import('@/pages/admin/quanlinhanvien/StaffFormPage.vue'),
            meta: { title: 'Chỉnh sửa nhân viên' }
        },
    ]
};

