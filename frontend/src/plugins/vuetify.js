/**
 * plugins/vuetify.js
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Composables
import { createVuetify } from 'vuetify'
// Styles
import '@mdi/font/css/materialdesignicons.css'

import 'vuetify/styles'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  theme: {
    defaultTheme: 'seoulMateTheme',
    themes: {
      seoulMateTheme: {
        dark: false,
        colors: {
          'background': '#FFFFFF',
          'surface': '#FFFFFF',
          'primary': '#F8496C', // SeoulSkycoral (Main)
          'secondary': '#FF8EA5', // Pink
          'accent': '#FFC8D3', // Light Pink
          'error': '#B00020',
          'info': '#2196F3',
          'success': '#4CAF50',
          'warning': '#FB8C00',

          // Custom Text Colors
          'text-main': '#000000',
          'text-sub': '#929292', // Gray
          'gray-light': '#E9E9E9', // Light Gray
        },
      },
    },
  },
})
