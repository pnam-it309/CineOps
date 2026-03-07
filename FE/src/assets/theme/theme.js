/**
 * CineOps Brand Theme Configuration
 * For Ant Design Vue / Ant Design
 */

const cineOpsTheme = {
  token: {
    // Brand Colors
    colorPrimary: '#E31E24',
    colorInfo: '#E31E24',
    colorSuccess: '#10b981',
    colorWarning: '#f59e0b',
    colorError: '#ef4444',
    colorTextBase: '#1e293b',
    colorBgBase: '#ffffff',
    
    // Typography
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
    fontSize: 14,
    borderRadius: 8,
    
    // Components focus color
    colorPrimaryHover: '#A51C1C',
    colorPrimaryActive: '#A51C1C',
  },
  components: {
    Button: {
      borderRadius: 8,
      controlHeight: 40,
      fontWeight: 600,
      // Note: Gradient buttons are usually handled via CSS class or custom theme
    },
    Card: {
      borderRadiusLG: 16,
      boxShadowTertiary: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
    },
    Table: {
      fontSize: 14,
      fontSizeHeading: 13,
      fontWeightStrong: 700,
      headerBg: '#F8FAFC',
      headerColor: '#1e293b',
      paddingContentVerticalLG: 12,
    },
    Menu: {
      itemBorderRadius: 12,
      itemSelectedBg: '#FFF1F1',
      itemSelectedColor: '#E31E24',
    },
    Input: {
      controlHeight: 40,
      activeBorderColor: '#E31E24',
    }
  }
};

export default cineOpsTheme;
