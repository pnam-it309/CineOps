export const sharedStandaloneRoutes = {
    path: '/shared',
    component: () => import('@/layout/MainLayout.vue'), // Simple layout or MainLayout
    children: [
        {
            path: 'invoices/:id',
            name: 'StandaloneInvoiceDetail',
            component: () => import('@/pages/shared/StandaloneInvoiceDetail.vue'),
            meta: { title: 'Chi tiết hóa đơn' }
        },
    ]
};

