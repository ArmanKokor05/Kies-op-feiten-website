import {createApp} from "vue";

import adminPage from "./adminPage.vue";
import {adminRouter} from "./routers/adminRouter.ts"

createApp(adminPage).use(adminRouter).mount('#adminPage')
