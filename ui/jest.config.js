module.exports = {
  preset: '@vue/cli-plugin-unit-jest',
  transform: {
    '^.+\\.vue$': 'vue-jest',
  },
  collectCoverage: true,
  coverageReporters: ['lcov', 'text-summary'],
}
