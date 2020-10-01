import * as filters from '@/shared/variantFilters'

describe('variantFilters', () => {
  describe('rangeTextFilter, e.g. 156-173', () => {
    it.each`
      input          | value        | expResult
      ${null}        | ${152}       | ${true}
      ${undefined}   | ${152}       | ${true}
      ${'152'}       | ${152}       | ${true}
      ${'152-'}      | ${152}       | ${true}
      ${'-152'}      | ${152}       | ${true}
      ${'152-152'}   | ${152}       | ${true}
      ${'151-153'}   | ${152}       | ${true}
      ${'-'}         | ${152}       | ${true}
      ${'-151'}      | ${152}       | ${false}
      ${'153-'}      | ${152}       | ${false}
      ${'151'}       | ${152}       | ${false}
      ${'0.01'}      | ${0.01}      | ${true}
      ${'0.1-0.2'}   | ${0.15}      | ${true}
      ${'0.03-0.03'} | ${0.03}      | ${true}
      ${'0.01-0.1'}  | ${0.05}      | ${true}
      ${'0.01-0.1'}  | ${0.11}      | ${false}
      ${'152'}       | ${null}      | ${false}
      ${'152'}       | ${undefined} | ${false}
    `(
      'rangeTextFilter($input, $value) is $expResult',
      ({ input, value, expResult }) => {
        expect(filters.rangeTextFilter(input, value)).toBe(expResult)
      }
    )
  })

  describe('iContainsFilter', () => {
    it.each`
      input        | value        | showBlank | expResult
      ${null}      | ${'hello'}   | ${true}   | ${true}
      ${undefined} | ${'hello'}   | ${true}   | ${true}
      ${'hello'}   | ${'hello'}   | ${true}   | ${true}
      ${'hello'}   | ${null}      | ${true}   | ${true}
      ${'hello'}   | ${undefined} | ${true}   | ${true}
      ${'hello'}   | ${''}        | ${true}   | ${true}
      ${'ElL'}     | ${'hello'}   | ${true}   | ${true}
      ${'hello'}   | ${'ElL'}     | ${true}   | ${false}
      ${null}      | ${'hello'}   | ${false}  | ${true}
      ${undefined} | ${'hello'}   | ${false}  | ${true}
      ${'hello'}   | ${'hello'}   | ${false}  | ${true}
      ${'hello'}   | ${null}      | ${false}  | ${false}
      ${'hello'}   | ${undefined} | ${false}  | ${false}
      ${'hello'}   | ${''}        | ${false}  | ${false}
      ${'ElL'}     | ${'hello'}   | ${false}  | ${true}
      ${'hello'}   | ${'ElL'}     | ${false}  | ${false}
    `(
      'iContainsFilter($input, $value, $showBlank) is $expResult',
      ({ input, value, showBlank, expResult }) => {
        expect(filters.iContainsFilter(input, value, showBlank)).toBe(expResult)
      }
    )
  })

  describe('predicateFilter', () => {
    it.each`
      predicate    | value      | expResult
      ${null}      | ${'hello'} | ${false}
      ${undefined} | ${'hello'} | ${false}
    `(
      'predicateFilter($predicate, $value) is $expResult',
      ({ predicate, value, expResult }) => {
        expect(filters.predicateFilter(predicate, value)).toBe(expResult)
      }
    )
  })

  describe('inSetFilter', () => {
    it.each`
      input             | value    | expResult
      ${null}           | ${'INS'} | ${true}
      ${undefined}      | ${'INS'} | ${true}
      ${[]}             | ${'INS'} | ${true}
      ${['INS']}        | ${'INS'} | ${true}
      ${['DEL', 'INS']} | ${'INS'} | ${true}
      ${['DEL', 'SNP']} | ${'INS'} | ${false}
    `(
      'inSetFilter($input, $value) is $expResult',
      ({ input, value, expResult }) => {
        expect(filters.inSetFilter(input, value)).toBe(expResult)
      }
    )
  })
})
