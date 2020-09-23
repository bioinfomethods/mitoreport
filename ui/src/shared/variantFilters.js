import * as _ from 'lodash'

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

export function iContainsFilter(input, value) {
  if (!input || !value) return true

  return value
    .toString()
    .toUpperCase()
    .includes(input.toString().toUpperCase())
}

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
