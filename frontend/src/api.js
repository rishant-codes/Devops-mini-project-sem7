const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

async function request(path, options) {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.error || `Request failed: ${response.status}`)
  }
  if (response.status === 204) return null
  return response.json()
}

export function getDashboard(query) {
  const q = query ? `?q=${encodeURIComponent(query)}` : ""
  return request(`/api/dashboard${q}`)
}

export function getStationDetail(id) {
  return request(`/api/stations/${id}`)
}

export function createReading(payload) {
  return request("/api/readings", {
    method: "POST",
    body: JSON.stringify(payload),
  })
}
