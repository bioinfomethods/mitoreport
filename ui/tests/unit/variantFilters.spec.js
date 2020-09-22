import * as filters from '@/shared/variantFilters'

describe('variantFilters', () => {
  describe('posFilter', () => {
    it.each`
      input        | pos          | expResult
      ${null}      | ${152}       | ${true}
      ${undefined} | ${152}       | ${true}
      ${'152'}     | ${152}       | ${true}
      ${'152-'}    | ${152}       | ${true}
      ${'-152'}    | ${152}       | ${true}
      ${'152-152'} | ${152}       | ${true}
      ${'151-153'} | ${152}       | ${true}
      ${'-'}       | ${152}       | ${true}
      ${'-151'}    | ${152}       | ${false}
      ${'153-'}    | ${152}       | ${false}
      ${'151'}     | ${152}       | ${false}
      ${'152'}     | ${null}      | ${false}
      ${'152'}     | ${undefined} | ${false}
    `('posFilter($input, $pos) is $expResult', ({ input, pos, expResult }) => {
      expect(filters.posFilter(input, pos)).toBe(expResult)
    })
  })

  describe('iContainsFilter', () => {
    it.each`
      input        | value        | expResult
      ${null}      | ${'hello'}   | ${true}
      ${undefined} | ${'hello'}   | ${true}
      ${'hello'}   | ${'hello'}   | ${true}
      ${'hello'}   | ${null}      | ${true}
      ${'hello'}   | ${undefined} | ${true}
      ${'hello'}   | ${''}        | ${true}
      ${'ElL'}     | ${'hello'}   | ${true}
      ${'hello'}   | ${'ElL'}     | ${false}
    `(
      'iContainsFilter($input, $value) is $expResult',
      ({ input, value, expResult }) => {
        expect(filters.iContainsFilter(input, value)).toBe(expResult)
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
