import { createRouter, createWebHistory } from "vue-router";
import DashboardView from "../views/DashboardView.vue";
import ChatView from "../views/ChatView.vue";
import KnowledgeView from "../views/KnowledgeView.vue";
import ModelConfigView from "../views/ModelConfigView.vue";

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
  },
  {
    path: "/model-settings",
    name: "model-settings",
    component: ModelConfigView
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
