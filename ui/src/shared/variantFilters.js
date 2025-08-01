import * as _ from 'lodash'

export function consequenceFilter(selectedConsequences, consequenceValue) {
  if (
    !selectedConsequences ||
    _.isEmpty(selectedConsequences) ||
    !consequenceValue
  ) {
    return true
  }

  return selectedConsequences.indexOf(consequenceValue.displayTerm) >= 0
}

export function curationFilter(curationSearch, variantTags, disease) {
  if (!curationSearch) {
    return true
  }

  if (curationSearch && _.isEmpty(variantTags)) {
    return false
  }

  const matchesTagNames = (variantTags.map(t => t.tag) || []).some(t =>
    iContainsFilter(curationSearch, t, false)
  )

  const matchesNotes = (variantTags.map(t => t.notes) || []).some(n =>
    iContainsFilter(curationSearch, n, false)
  )

  return (
    matchesTagNames ||
    iContainsFilter(curationSearch, disease, false) ||
    matchesNotes
  )
}

export function rangeTextFilter(input, value) {
  if (!input) {
    return true
  }

  let range = _.split(input, '-', 2)

  if (range.length === 0) {
    return true
  } else if (range.length === 1) {
    return _.toNumber(range[0]) === value
  } else {
    let lower = range[0] || Number.MIN_SAFE_INTEGER
    let upper = range[1] || Number.MAX_SAFE_INTEGER

    return _.inRange(
      value,
      _.toNumber(lower),
      _.toNumber(upper) + 0.0000000000001 // ensure end is inclusive because _.inRange end is exclusive
    )
  }
}

export function iContainsFilter(input, value, showBlank = true) {
  const safeInput = input || ''
  const safeValue = value || ''

  if (showBlank && !safeValue) return true

  return safeValue
    .toString()
    .toUpperCase()
    .includes(safeInput.toString().toUpperCase())
}

export function predicateFilter(predicate, value) {
  const safeValue = value || ''

  return _.isFunction(predicate) && predicate(safeValue)
}

/**
 * Check that a value is in a set.
 * To decide whether a variant should be shown in a table?
 *
 * Return true if no set provided, or set is empty.
 * Iterate over the set
 */
export function inSetFilter(set, value) {
  if (!set || set.length === 0) {
    return true
  }

  if (value instanceof Object) {
    return _.some(set, item => _.isEqual(item, value))
  } else {
    return set.includes(value)
  }
}

/**
 * Check if any of the values belong to a set
 *
 * If set is empty, return true
 * If values are empty, return false
 * If any of the values are found in the set. Return true
 */
export function setInSetFilter(set, values) {
  if (!set || set.length === 0) {
    return true
  }

  if (!values || values.length === 0) {
    return false
  }

  var result = false

  values.forEach(value => {
    if (value instanceof Object) {
      if (_.some(set, item => _.isEqual(item, value))) result = true
    } else {
      if (set.includes(value)) result = true
    }
  })

  return result
}

/**
 * Trio filter for gnomAD Haplotype filter.
 * Passes in the words: "True", "False" or "Null"
 */
export function trioFilter(set, value) {
  if (!set || set.length === 0) {
    return true
  }

  if (value === true && set.includes('True')) {
    return true
  } else if (value === false && set.includes('False')) {
    return true
  } else if (value === undefined && set.includes('Null')) {
    return true
  } else {
    return false
  }
}
