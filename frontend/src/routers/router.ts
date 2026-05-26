import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AboutView from '../views/AboutView.vue'
import MapView from '../views/MapView.vue'
import StemwijzerView from '../views/StemwijzerView.vue'
import AdminQuestionListView from '../views/AdminQuestionListView.vue'
import AdminStandpointsView from '../views/AdminStandpointsView.vue'  // ← NIEUW!
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import CoalitieMakerView from "@/views/CoalitieMakerView.vue";
import DiscussionView from '@/views/DiscussionView.vue'
import AccountView from '@/views/AccountView.vue'
import PollingStationView from '@/views/PollingStationView.vue'
import CandidateView from "@/views/CandidateView.vue";
import SingleDiscussionView from "@/views/SingleDiscussionView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/results',
      name: 'results',
      component: () => import('../views/ResultsView.vue')
    },
    {
      path: '/over-ons',
      name: 'about',
      component: AboutView
    },
    {
      path: '/kaart',
      name: 'map',
      component: MapView
    },
    {
      path: '/stemwijzer',
      name: 'stemwijzer',
      component: StemwijzerView
    },
    {
      path: '/adminQuestionList',
      name: 'adminQuestionList',
      component: AdminQuestionListView
    },
    {
      path: '/admin/standpoints',  // ← NIEUW!
      name: 'adminStandpoints',
      component: AdminStandpointsView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/CoalitieMaker',
      name: 'CoalitieMaker',
      component: CoalitieMakerView
    },
    {
      path: '/discussies',
      name: 'discussions',
      component: DiscussionView
    },
    {
      path: '/discussies/:id',
      name: 'discussion',
      component: SingleDiscussionView
    },
    {
      path: '/account',
      name: 'account',
      component: AccountView
    },
    {
      path: '/Stembureaus',
      name: 'pollingStations',
      component: PollingStationView
    },
    {
      path: '/kandidaten',
      name: 'candidates',
      component: CandidateView
    },
  ]
})

export default router
