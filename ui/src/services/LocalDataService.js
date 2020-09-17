export function getVariants() {
  return Promise.resolve({
    status: 200,
    statusText: 'OK',
    data: window.variants,
  })
}

export function getDeletions() {
  return Promise.resolve({
    status: 200,
    statusText: 'OK',
    data: window.deletions,
  })
}
