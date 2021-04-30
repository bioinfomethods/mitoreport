import * as underTest from '@/shared/variantFilters'

describe('variantFilters', () => {
  describe('consequenceFilter', () => {
    it.each`
      selectedConsequences | consequenceValue      | expResult | i
      ${null}              | ${{ displayTerm: 1 }} | ${true}   | ${1}
      ${undefined}         | ${{ displayTerm: 1 }} | ${true}   | ${2}
      ${[1]}               | ${null}               | ${true}   | ${3}
      ${[1]}               | ${undefined}          | ${true}   | ${4}
      ${[1]}               | ${{ displayTerm: 1 }} | ${true}   | ${5}
      ${[2]}               | ${{ displayTerm: 1 }} | ${false}  | ${6}
      ${[2]}               | ${{ displayTerm: 2 }} | ${true}   | ${7}
      ${[2]}               | ${{ displayTerm: 3 }} | ${false}  | ${8}
      ${[]}                | ${{ displayTerm: 1 }} | ${true}   | ${9}
      ${[]}                | ${{ displayTerm: 3 }} | ${true}   | ${10}
      ${[]}                | ${null}               | ${true}   | ${11}
      ${[]}                | ${undefined}          | ${true}   | ${12}
    `(
      '$i) consequenceFilter($selectedConsequences, $consequenceValue) is $expResult',
      ({ selectedConsequences, consequenceValue, expResult }) => {
        expect(
          underTest.consequenceFilter(selectedConsequences, consequenceValue)
        ).toBe(expResult)
      }
    )
  })

  describe('curationFilter', () => {
    it.each`
      curationSearch | curationValue                                                                    | expResult
      ${null}        | ${undefined}                                                                     | ${true}
      ${undefined}   | ${undefined}                                                                     | ${true}
      ${''}          | ${undefined}                                                                     | ${true}
      ${'term'}      | ${undefined}                                                                     | ${false}
      ${'term'}      | ${null}                                                                          | ${false}
      ${'term'}      | ${{}}                                                                            | ${false}
      ${'term'}      | ${{ selectedTagNames: [], variantNote: '' }}                                     | ${false}
      ${'term'}      | ${{ selectedTagNames: ['Review', 'FalsePositive'], variantNote: 'Hello world' }} | ${false}
      ${'review'}    | ${{ selectedTagNames: ['FalsePositive', 'Review'], variantNote: '' }}            | ${true}
      ${'review'}    | ${{ selectedTagNames: ['FalsePositive', 'Review'], variantNote: undefined }}     | ${true}
      ${'hello'}     | ${{ selectedTagNames: [], variantNote: 'Hello world' }}                          | ${true}
      ${'hello'}     | ${{ selectedTagNames: undefined, variantNote: 'Hello world' }}                   | ${true}
      ${'review'}    | ${{ selectedTagNames: ['Review', 'FalsePositive'], variantNote: 'Must review' }} | ${true}
    `(
      'curationFilter($curationSearch, $curationValue) is $expResult',
      ({ curationSearch, curationValue, expResult }) => {
        expect(underTest.curationFilter(curationSearch, curationValue)).toBe(
          expResult
        )
      }
    )
  })

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
        expect(underTest.rangeTextFilter(input, value)).toBe(expResult)
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
        expect(underTest.iContainsFilter(input, value, showBlank)).toBe(
          expResult
        )
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
        expect(underTest.predicateFilter(predicate, value)).toBe(expResult)
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
        expect(underTest.inSetFilter(input, value)).toBe(expResult)
      }
    )
  })
})
