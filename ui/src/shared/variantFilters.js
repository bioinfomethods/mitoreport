import * as _ from 'lodash'

export function rangeTextFilter(input, value) {
  if (!input) {
    return true
  }

  let range = _.split(input, '-', 2)

  if (range.length === 0) {
    return true
  } else if (range.length === 1) {
    return parseInt(range[0]) === value
  } else {
    let lower = range[0] || 0
    let upper = range[1] || 99999999
    // console.debug(
    //   `range=${range},range[0]=${range[0]},range[1]=${range[1]},lower=${lower},upper=${upper}`
    // )
    return _.inRange(value, parseInt(lower), parseInt(upper) + 1)
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
