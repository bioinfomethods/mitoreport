export const MIN_POS = 200

export const MAX_POS = 16300

export const MAX_READ_DEPTH = 10000

export const SAVE_INTERVAL_MS = 5000

export const DEFAULT_SNACKBAR_OPTS = {
  active: false,
  color: 'green',
  message: null,
  timeout: 3000,
}

export const CONSEQUENCES = [
  { id: 'frameshift_variant', name: 'frameshift_variant' },
  { id: 'inframe_deletion', name: 'inframe_deletion' },
  { id: 'missense_variant', name: 'missense_variant' },
  { id: 'stop_gained', name: 'stop_gained' },
  { id: 'synonymous_variant', name: 'synonymous_variant' },
  { id: 'upstream_gene_variant', name: 'upstream_gene_variant' },
]
