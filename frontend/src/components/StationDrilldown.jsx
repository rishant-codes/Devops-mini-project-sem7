function StationDrilldown({ detail, loading, error, onClose }) {
  return (
    <div className="fixed inset-0 z-10 flex items-center justify-center bg-black/30 px-4" data-testid="drilldown-panel">
      <div className="w-full max-w-lg rounded-xl bg-white p-6 shadow-lg">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-slate-900">
            {loading ? "Loading..." : detail?.name}
          </h3>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600"
            data-testid="close-drilldown"
          >
            Close
          </button>
        </div>

        {error && <p className="mt-3 text-sm text-rose-600">{error}</p>}

        {!loading && detail && (
          <>
            <p className="mt-1 text-sm text-slate-500">{detail.location}</p>
            <div className="mt-4 max-h-64 overflow-y-auto divide-y divide-slate-100">
              {detail.history.length === 0 && (
                <p className="py-3 text-sm text-slate-400">No readings recorded yet.</p>
              )}
              {detail.history.map((r) => (
                <div key={r.id} className="flex items-center justify-between py-2 text-sm">
                  <span className="text-slate-500">{new Date(r.recordedAt).toLocaleString()}</span>
                  <span className="font-medium text-slate-800">AQI {r.aqi}</span>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default StationDrilldown
