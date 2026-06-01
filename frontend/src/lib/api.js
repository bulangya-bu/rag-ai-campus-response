const API_PREFIX = "/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_PREFIX}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  const payload = await response.json().catch(() => ({
    success: false,
    message: "服务返回了无法解析的响应"
  }));

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || "请求失败");
  }

  return payload.data;
}

export const api = {
  getOverview() {
    return request("/dashboard/overview");
  },
  getSystemStatus() {
    return request("/system/status");
  },
  listModelConfigs() {
    return request("/model-configs");
  },
  getActiveModelConfig() {
    return request("/model-configs/active");
  },
  createModelConfig(data) {
    return request("/model-configs", {
      method: "POST",
      body: JSON.stringify(data)
    });
  },
  updateModelConfig(id, data) {
    return request(`/model-configs/${id}`, {
      method: "PUT",
      body: JSON.stringify(data)
    });
  },
  activateModelConfig(id) {
    return request(`/model-configs/${id}/activate`, {
      method: "POST"
    });
  },
  testModelConfig(data) {
    return request("/model-configs/test", {
      method: "POST",
      body: JSON.stringify(data)
    });
  },
  deleteModelConfig(id) {
    return request(`/model-configs/${id}`, {
      method: "DELETE"
    });
  },
  listSessions() {
    return request("/chat/sessions");
  },
  createSession(title) {
    return request("/chat/sessions", {
      method: "POST",
      body: JSON.stringify({ title })
    });
  },
  getConversation(sessionId) {
    return request(`/chat/sessions/${sessionId}`);
  },
  sendMessage(sessionId, content) {
    return request(`/chat/sessions/${sessionId}/messages`, {
      method: "POST",
      body: JSON.stringify({ content })
    });
  },
  deleteSession(sessionId) {
    return request(`/chat/sessions/${sessionId}`, {
      method: "DELETE"
    });
  },
  listKnowledgeDocuments(params = {}) {
    const search = new URLSearchParams();
    if (params.query) {
      search.set("query", params.query);
    }
    if (params.category) {
      search.set("category", params.category);
    }
    const queryString = search.toString();
    return request(`/knowledge-documents${queryString ? `?${queryString}` : ""}`);
  },
  getKnowledgeDocument(id) {
    return request(`/knowledge-documents/${id}`);
  },
  createKnowledgeDocument(data) {
    return request("/knowledge-documents", {
      method: "POST",
      body: JSON.stringify(data)
    });
  },
  updateKnowledgeDocument(id, data) {
    return request(`/knowledge-documents/${id}`, {
      method: "PUT",
      body: JSON.stringify(data)
    });
  },
  deleteKnowledgeDocument(id) {
    return request(`/knowledge-documents/${id}`, {
      method: "DELETE"
    });
  }
};
