import { createRouter, createWebHistory } from "vue-router";
import DashboardView from "../views/DashboardView.vue";
import ChatView from "../views/ChatView.vue";
import KnowledgeView from "../views/KnowledgeView.vue";

const routes = [
  {
    path: "/",
    redirect: "/dashboard"
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView
  },
  {
    path: "/chat",
    name: "chat",
    component: ChatView
  },
  {
    path: "/knowledge",
    name: "knowledge",
    component: KnowledgeView
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
