import * as _ from 'lodash'

export function precisionTo(value, precision = 2) {
  if (!value || !_.isNumber(value)) return '0'

  return _.toNumber(value).toPrecision(precision)
}
