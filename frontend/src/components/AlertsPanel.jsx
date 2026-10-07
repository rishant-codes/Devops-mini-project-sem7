function AlertsPanel({ stations }) {
  const alerts = stations.filter((s) => s.latestAqi != null && s.latestAqi > 100)

  return (
    <div className="mt-3 rounded-xl border border-rose-200 bg-rose-50/60 p-5" data-testid="alerts-panel">
      {alerts.length === 0 ? (
        <p className="text-sm text-slate-500" data-testid="no-alerts">
          No active alerts. All stations within acceptable AQI range.
        </p>
      ) : (
        <ul className="space-y-2">
          {alerts.map((s) => (
            <li key={s.id} className="text-sm text-rose-700" data-testid={`alert-${s.id}`}>
              <span className="font-semibold">{s.name}</span> is reporting AQI {s.latestAqi} ({s.status}).
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default AlertsPanel
