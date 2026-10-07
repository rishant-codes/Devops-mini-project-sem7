import { useCallback, useEffect, useState } from "react"
import StatCard from "./components/StatCard"
import StationTable from "./components/StationTable"
import AddReadingForm from "./components/AddReadingForm"
import AlertsPanel from "./components/AlertsPanel"
import StationDrilldown from "./components/StationDrilldown"
import { getDashboard, getStationDetail } from "./api"

function App() {
  const [query, setQuery] = useState("")
  const [dashboard, setDashboard] = useState(null)
  const [loadError, setLoadError] = useState(null)

  const [selectedStationId, setSelectedStationId] = useState(null)
  const [stationDetail, setStationDetail] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [detailError, setDetailError] = useState(null)

  const refreshDashboard = useCallback(() => {
    getDashboard(query)
      .then((data) => {
        setDashboard(data)
        setLoadError(null)
      })
      .catch((err) => setLoadError(err.message))
  }, [query])

  useEffect(() => {
    refreshDashboard()
  }, [refreshDashboard])

  useEffect(() => {
    if (selectedStationId == null) return
    setDetailLoading(true)
    setDetailError(null)
    getStationDetail(selectedStationId)
      .then((data) => setStationDetail(data))
      .catch((err) => setDetailError(err.message))
      .finally(() => setDetailLoading(false))
  }, [selectedStationId])

  const allStations = dashboard?.stations ?? []

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto max-w-6xl px-6 py-5">
          <h1 className="text-xl font-semibold tracking-tight text-slate-900">
            Air Quality Monitoring Platform
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Real-time station readings and status overview
          </p>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-8">
        {loadError && (
          <p className="mb-4 rounded-lg bg-rose-50 px-4 py-2 text-sm text-rose-700" data-testid="dashboard-error">
            Could not reach the backend: {loadError}
          </p>
        )}

        <section className="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <StatCard
            label="Stations Online"
            value={dashboard ? `${dashboard.stationsOnline} / ${dashboard.stationsTotal}` : "-"}
          />
          <StatCard label="Average AQI" value={dashboard ? dashboard.averageAqi : "-"} />
          <StatCard label="Active Alerts" value={dashboard ? dashboard.activeAlerts : "-"} />
        </section>

        <section className="mt-8">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
              Station Dashboard
            </h2>
            <input
              type="text"
              placeholder="Search stations..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm"
              data-testid="search-input"
            />
          </div>
          <StationTable stations={dashboard?.stations ?? []} onSelect={setSelectedStationId} />
        </section>

        <section className="mt-8">
          <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
            Record a New Reading
          </h2>
          <AddReadingForm stations={allStations} onSubmitted={refreshDashboard} />
        </section>

        <section className="mt-8">
          <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
            Alerts &amp; Exceptions
          </h2>
          <AlertsPanel stations={allStations} />
        </section>
      </main>

      {selectedStationId != null && (
        <StationDrilldown
          detail={stationDetail}
          loading={detailLoading}
          error={detailError}
          onClose={() => {
            setSelectedStationId(null)
            setStationDetail(null)
          }}
        />
      )}
    </div>
  )
}

export default App
