export function getVariants() {
  return Promise.resolve({ data: window.variants })
}

export function getDeletions() {
  return Promise.resolve({ data: window.deletions })
}
