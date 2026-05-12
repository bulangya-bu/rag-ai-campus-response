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
