import { ROUTES_CONSTANTS } from '@/constants/routeConstants';

const StaffLayout = () => import('@/layouts/StaffLayout.vue');

export const staffRoutes = {
    path: ROUTES_CONSTANTS.STAFF.path, // /staff
    component: StaffLayout,
    meta: { requiresAuth: true, roles: ['ROLE_STAFF', 'ROLE_ADMIN'], title: 'Nhân viên' },
    children: [
        {
            path: '',
            redirect: { name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name }
        },
        {
            path: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.path,
            name: ROUTES_CONSTANTS.STAFF.children.DASHBOARD.name,
            component: () => import('@/pages/staff/StaffDashboard.vue'),
            meta: { title: 'Tổng quan POS' }
        },
        {
            path: ROUTES_CONSTANTS.STAFF.children.SALES.path,
            name: ROUTES_CONSTANTS.STAFF.children.SALES.name,
            component: () => import('@/pages/staff/StaffSales.vue'),
            meta: { title: 'Quầy bán vé' }
        },
        {
            path: ROUTES_CONSTANTS.STAFF.children.CHECKIN.path,
            name: ROUTES_CONSTANTS.STAFF.children.CHECKIN.name,
            component: () => import('@/pages/staff/StaffCheckin.vue'),
            meta: { title: 'Soát vé' }
        },
        {
            path: ROUTES_CONSTANTS.STAFF.children.INVOICES.path,
            name: ROUTES_CONSTANTS.STAFF.children.INVOICES.name,
            component: () => import('@/pages/staff/StaffInvoices.vue'),
            meta: { title: 'Hóa đơn ngày' }
        },
        {
            path: ROUTES_CONSTANTS.STAFF.children.SHIFT.path,
            name: ROUTES_CONSTANTS.STAFF.children.SHIFT.name,
            component: () => import('@/pages/staff/StaffShift.vue'),
            meta: { title: 'Bàn giao ca' }
        },
    ]
};
