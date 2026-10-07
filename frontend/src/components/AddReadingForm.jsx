import { useState } from "react"
import { createReading } from "../api"

function AddReadingForm({ stations, onSubmitted }) {
  const [stationId, setStationId] = useState("")
  const [aqi, setAqi] = useState("")
  const [pm25, setPm25] = useState("")
  const [pm10, setPm10] = useState("")
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)

    if (!stationId || aqi === "") {
      setError("Station and AQI are required.")
      return
    }

    setSubmitting(true)
    try {
      await createReading({
        stationId: Number(stationId),
        aqi: Number(aqi),
        pm25: pm25 === "" ? 0 : Number(pm25),
        pm10: pm10 === "" ? 0 : Number(pm10),
      })
      setAqi("")
      setPm25("")
      setPm10("")
      onSubmitted()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="mt-3 rounded-xl border border-slate-200 bg-white p-5 shadow-sm"
      data-testid="add-reading-form"
    >
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-4">
        <select
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          value={stationId}
          onChange={(e) => setStationId(e.target.value)}
          data-testid="station-select"
        >
          <option value="">Select station</option>
          {stations.map((s) => (
            <option key={s.id} value={s.id}>
              {s.name}
            </option>
          ))}
        </select>
        <input
          type="number"
          placeholder="AQI"
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          value={aqi}
          onChange={(e) => setAqi(e.target.value)}
          data-testid="aqi-input"
        />
        <input
          type="number"
          placeholder="PM2.5"
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          value={pm25}
          onChange={(e) => setPm25(e.target.value)}
          data-testid="pm25-input"
        />
        <input
          type="number"
          placeholder="PM10"
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          value={pm10}
          onChange={(e) => setPm10(e.target.value)}
          data-testid="pm10-input"
        />
      </div>
      {error && (
        <p className="mt-2 text-sm text-rose-600" data-testid="form-error">
          {error}
        </p>
      )}
      <button
        type="submit"
        disabled={submitting}
        className="mt-3 rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
        data-testid="submit-reading"
      >
        {submitting ? "Submitting..." : "Add Reading"}
      </button>
    </form>
  )
}

export default AddReadingForm
