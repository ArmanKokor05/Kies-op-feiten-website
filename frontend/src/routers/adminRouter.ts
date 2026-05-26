import { createWebHashHistory, createRouter } from 'vue-router'
import adminPage from '../adminPage.vue';

  const routes = [
    { path: "/:panels*",
      name: "Items",
      component: adminPage
    }
  ];

  export const adminRouter = createRouter({
    history: createWebHashHistory(),
    routes,
  });
