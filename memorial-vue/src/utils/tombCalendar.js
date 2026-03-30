export function pad2(n) {
  if (n == null || n === '') return '00'
  return String(n).padStart(2, '0')
}

/** 统一为 yyyy-MM-dd 字符串 */
export function normalizeYmd(val) {
  if (val == null || val === '') return ''
  const s = String(val).trim()
  return s.length >= 10 ? s.slice(0, 10) : s
}

/** 解析 yyyy-MM-dd */
export function parseYmd(s) {
  if (!s || typeof s !== 'string') return null
  const p = s.trim().split('-').map(Number)
  if (p.length !== 3 || p.some((x) => Number.isNaN(x))) return null
  return { y: p[0], m: p[1], d: p[2] }
}

/**
 * 展示为 2026-03-27 + 农/公 色块（HTML，用于 v-html）
 */
export function formatTombSide(ymdStr, isLunar) {
  const raw = normalizeYmd(ymdStr)
  const p = parseYmd(raw)
  if (!p) return '—'
  const ymd = `${p.y}-${pad2(p.m)}-${pad2(p.d)}`
  const tag = isLunar ? '农' : '公'
  const kind = isLunar ? 'lunar' : 'solar'
  return `<span class="tomb-cal-ymd">${ymd}</span><span class="tomb-cal-badge tomb-cal-badge--${kind}">${tag}</span>`
}

export function formatTombBirth(row) {
  if (!row) return '—'
  return formatTombSide(row.birthday, !!row.lunarFlag)
}

export function formatTombDeath(row) {
  if (!row) return '—'
  return formatTombSide(row.deathday, !!row.lunarFlag)
}

/** 仅 YYYY-MM-DD，无公/农标记 */
export function formatTombYmdOnly(val) {
  const raw = normalizeYmd(val)
  const p = parseYmd(raw)
  if (!p) return ''
  return `${p.y}-${pad2(p.m)}-${pad2(p.d)}`
}

/**
 * 纪念页：出生 — 逝世，仅在整段末尾显示「公历」或「农历」
 */
export function formatTombMemorialDateLine(row) {
  if (!row) return ''
  const b = formatTombYmdOnly(row.birthday)
  if (!b) return ''
  const d = formatTombYmdOnly(row.deathday)
  const tag = row.lunarFlag ? '农历' : '公历'
  const kind = row.lunarFlag ? 'lunar' : 'solar'
  const badge = `<span class="tomb-cal-badge tomb-cal-badge--memorial tomb-cal-badge--${kind}">${tag}</span>`
  if (d) {
    return `<span class="tomb-memorial-dates-line"><span class="tomb-cal-ymd">${b}</span><span class="tomb-memorial-sep">—</span><span class="tomb-cal-ymd">${d}</span>${badge}</span>`
  }
  return `<span class="tomb-memorial-dates-line"><span class="tomb-cal-ymd">${b}</span>${badge}</span>`
}
