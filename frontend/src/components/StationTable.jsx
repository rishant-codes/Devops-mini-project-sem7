import { statusStyles, statusLabels } from "../statusStyles"

function StationTable({ stations, onSelect }) {
  return (
    <div className="mt-3 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-slate-200" data-testid="station-table">
        <thead className="bg-slate-50">
          <tr>
            <th className="px-5 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
              Station
            </th>
            <th className="px-5 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
              AQI
            </th>
            <th className="px-5 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
              Status
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {stations.length === 0 && (
            <tr>
              <td colSpan={3} className="px-5 py-6 text-center text-sm text-slate-400" data-testid="no-stations">
                No stations match your search.
              </td>
            </tr>
          )}
          {stations.map((s) => (
            <tr
              key={s.id}
              className="cursor-pointer hover:bg-slate-50"
              onClick={() => onSelect(s.id)}
              data-testid={`station-row-${s.id}`}
            >
              <td className="px-5 py-3 text-sm font-medium text-slate-800">{s.name}</td>
              <td className="px-5 py-3 text-sm text-slate-600">{s.latestAqi ?? "-"}</td>
              <td className="px-5 py-3">
                {s.status && (
                  <span
                    className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${statusStyles[s.status]}`}
                  >
                    {statusLabels[s.status]}
                  </span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default StationTable
